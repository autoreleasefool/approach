import ComposableArchitecture
import FeatureActionLibrary
import GamesListFeature
import LeagueEditorFeature
import LeaguesRepositoryInterface
import ModelsLibrary
import PreferenceServiceInterface
import RecentlyUsedServiceInterface
import SeriesListFeature
import SeriesRepositoryInterface
import SortOrderLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

@Reducer
public struct LeaguesSection: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public let bowlerId: Bowler.ID
		public var leagues: IdentifiedArrayOf<League.List> = []
		@Shared public var ordering: League.Ordering
		@Shared public var recurrence: League.Recurrence?
		@Presents public var destination: Destination.State?

		var filter: LeaguesRepository.ListFilter {
			LeaguesRepository.ListFilter(bowler: bowlerId, recurrence: recurrence, ordering: ordering)
		}

		public init(
			bowlerId: Bowler.ID,
			ordering: Shared<League.Ordering>,
			recurrence: Shared<League.Recurrence?>
		) {
			self.bowlerId = bowlerId
			self._ordering = ordering
			self._recurrence = recurrence
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case didStartTask
			case didTapLeague(id: League.ID)
		}

		@CasePathable
		public enum Delegate { case doNothing }

		@CasePathable
		public enum Internal {
			case didLoadLeagues(Result<[League.List], Error>)
			case didLoadLeagueForEdits(Result<League.Edit, Error>)
			case didLoadLeagueToView(Result<League.SeriesHost, Error>)
			case didLoadEventSeriesToView(Result<(League.SeriesHost, Series.GameHost), Error>)

			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	@Reducer(state: .equatable)
	public enum Destination {
		case editor(LeagueEditor)
		case series(SeriesList)
		case games(GamesList)
	}

	public enum ErrorID: Hashable {
		case leagueNotFound
		case failedToLoadLeagues
	}

	public init() {}

	@Dependency(LeaguesRepository.self) var leagues
	@Dependency(RecentlyUsedService.self) var recentlyUsed
	@Dependency(SeriesRepository.self) var series

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didStartTask:
					return .run { [filter = state.filter] send in
						for try await leagues in leagues.list(filter: filter) {
							await send(.internal(.didLoadLeagues(.success(leagues))))
						}
					} catch: { error, send in
						await send(.internal(.didLoadLeagues(.failure(error))))
					}

				case let .didTapLeague(id):
					return .run { send in
						let league = try await leagues.seriesHost(id)
						switch league.recurrence {
						case .once:
							await send(.internal(.didLoadEventSeriesToView(Result {
								let series = try await series.eventSeries(league.id)
								return (league, series)
							})))
						case .repeating:
							await send(.internal(.didLoadLeagueToView(.success(league))))
						}
					} catch: { error, send in
						await send(.internal(.didLoadLeagueToView(.failure(error))))
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadLeagues(.success(leagues)):
					state.leagues = IdentifiedArray(uniqueElements: leagues)
					return .none

				case let .didLoadLeagueToView(.success(league)):
					state.destination = .series(SeriesList.State(league: league))
					return recentlyUsed.didRecentlyUse(.leagues, id: league.id, in: self)

				case let .didLoadEventSeriesToView(.success((event, series))):
					state.destination = .games(GamesList.State(series: series, host: event))
					return recentlyUsed.didRecentlyUse(.leagues, id: event.id, in: self)

				case let .didLoadLeagueForEdits(.success(league)):
					state.destination = .editor(LeagueEditor.State(value: .edit(league)))
					return recentlyUsed.didRecentlyUse(.leagues, id: league.id, in: self)

				case .didLoadLeagues(.failure):
					// TODO: handle error
					return .none

				case .didLoadLeagueToView(.failure),
						.didLoadEventSeriesToView(.failure),
						.didLoadLeagueForEdits(.failure):
					// TODO: handle error
					return .none

				case let .destination(.presented(.editor(.delegate(delegateAction)))):
					switch delegateAction {
					case .didFinishCreating, .didFinishUpdating, .didFinishArchiving:
						return .none
					}

				case .destination(.dismiss),
						.destination(.presented(.games(.delegate(.doNothing)))),
						.destination(.presented(.games(.internal))),
						.destination(.presented(.games(.view))),
						.destination(.presented(.series(.delegate(.doNothing)))),
						.destination(.presented(.series(.internal))),
						.destination(.presented(.series(.view))),
						.destination(.presented(.editor(.internal))),
						.destination(.presented(.editor(.binding))),
						.destination(.presented(.editor(.view))):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination)
	}
}

// MARK: - View

@ViewAction(for: LeaguesSection.self)
public struct LeaguesSectionView: View {
	@Bindable public var store: StoreOf<LeaguesSection>

	public init(store: StoreOf<LeaguesSection>) {
		self.store = store
	}

	public var body: some View {
		Section {
			if store.leagues.isEmpty {
				Text("No leagues")
			} else {
				ForEach(store.leagues) { league in
					Button { send(.didTapLeague(id: league.id)) } label: {
						LabeledContent(league.name, value: format(average: league.average))
					}
					.buttonStyle(.navigation)
				}
				.editor($store.scope(state: \.destination?.editor, action: \.internal.destination.editor))
				.series($store.scope(state: \.destination?.series, action: \.internal.destination.series))
				.games($store.scope(state: \.destination?.games, action: \.internal.destination.games))
			}
		}
		.task { await send(.didStartTask).finish() }
	}
}

extension View {
	fileprivate func editor(_ store: Binding<StoreOf<LeagueEditor>?>) -> some View {
		sheet(item: store) { (store: StoreOf<LeagueEditor>) in
			NavigationStack {
				LeagueEditorView(store: store)
			}
		}
	}

	fileprivate func series(_ store: Binding<StoreOf<SeriesList>?>) -> some View {
		navigationDestination(item: store) { (store: StoreOf<SeriesList>) in
			SeriesListView(store: store)
		}
	}

	fileprivate func games(_ store: Binding<StoreOf<GamesList>?>) -> some View {
		navigationDestination(item: store) { (store: StoreOf<GamesList>) in
			GamesListView(store: store)
		}
	}
}
