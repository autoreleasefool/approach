// swiftlint:disable file_length
import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import GamesListFeature
import GearRepositoryInterface
import LeagueEditorFeature
import LeaguesRepositoryInterface
import ModelsLibrary
import PreferenceServiceInterface
import RecentlyUsedServiceInterface
import ResourceListLibrary
import SeriesListFeature
import SeriesRepositoryInterface
import SortOrderLibrary
import StatisticsWidgetsLayoutFeature
import StringsLibrary
import ViewsLibrary

extension League.List: ResourceListItem {}

extension League.Ordering: CustomStringConvertible {
	public var description: String {
		switch self {
		case .byRecentlyUsed: return Strings.Ordering.mostRecentlyUsed
		case .byName: return Strings.Ordering.alphabetical
		}
	}
}

@Reducer
public struct LeaguesList: Reducer, Sendable {
	public typealias SectionList = SectionResourceList<League.List, League.List.FetchRequest>

	@ObservableState
	public struct State: Equatable {
		public let bowler: Bowler.Summary

		public var list: SectionList.State
		public var preferredGear: PreferredGear.State
		public var widgets: StatisticsWidgetLayout.State

		public var ordering: League.Ordering = .default
		public var filter: LeaguesFilter.State

		public var isShowingWidgets: Bool

		public var errors: Errors<ErrorID>.State = .init()

		@Presents public var destination: Destination.State?

		var isAnyFilterActive: Bool { filter.recurrence != nil }

		public init(bowler: Bowler.Summary) {
			self.bowler = bowler
			let filter: LeaguesFilter.State = .init()
			self.filter = filter
			self.widgets = .init(context: LeaguesList.widgetContext(forBowler: bowler.id), newWidgetSource: .bowler(bowler.id))
			self.preferredGear = .init(bowler: bowler.id)
			self.list = SectionList.State(
				features: [
					.add,
					.swipeToEdit,
					.swipeToArchive,
				],
				query: .init(
					filter: .init(bowler: bowler.id, recurrence: filter.recurrence),
					ordering: .default
				),
				listTitle: Strings.League.List.title,
				emptyContent: .init(
					image: Asset.Media.EmptyState.leagues,
					title: Strings.League.Error.Empty.title,
					message: Strings.League.Error.Empty.message,
					action: Strings.League.List.add
				)
			)

			@Dependency(\.preferences) var preferences
			self.isShowingWidgets = preferences.bool(forKey: .statisticsWidgetHideInLeagueList) != true
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case onAppear
			case didStartTask
			case didTapLeague(id: League.ID)
			case didTapFilterButton
			case didTapSortOrderButton
		}

		@CasePathable
		public enum Delegate { case doNothing }

		@CasePathable
		public enum Internal {
			case didLoadEditableLeague(Result<League.Edit, Error>)
			case didArchiveLeague(Result<League.List, Error>)
			case didLoadSeriesLeague(Result<League.SeriesHost, Error>)
			case didLoadEventSeries(Result<EventSeries, Error>)
			case didSetIsShowingWidgets(Bool)

			case errors(Errors<ErrorID>.Action)
			case preferredGear(PreferredGear.Action)
			case list(SectionList.Action)
			case widgets(StatisticsWidgetLayout.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	@Reducer(state: .equatable)
	public enum Destination {
		case editor(LeagueEditor)
		case filters(LeaguesFilter)
		case series(SeriesList)
		case games(GamesList)
		case sortOrder(SortOrder<League.Ordering>)
	}

	public enum SectionID: String {
		case leagues
		case events
	}

	public enum ErrorID: Hashable {
		case leagueNotFound
		case failedToArchiveLeague
		case failedToLoadPreferredGear
		case failedToSavePreferredGear
	}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(GearRepository.self) var gear
	@Dependency(LeaguesRepository.self) var leagues
	@Dependency(\.preferences) var preferences
	@Dependency(RecentlyUsedService.self) var recentlyUsed
	@Dependency(SeriesRepository.self) var series
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Scope(state: \.list, action: \.internal.list) {
			SectionResourceList { @Sendable in
				fetchResources(query: $0)
			}
		}

		Scope(state: \.preferredGear, action: \.internal.preferredGear) {
			PreferredGear()
		}

		Scope(state: \.widgets, action: \.internal.widgets) {
			StatisticsWidgetLayout()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didStartTask:
					return .run { send in
						for await _ in preferences.observe(keys: [.statisticsWidgetHideInLeagueList]) {
							await send(.internal(.didSetIsShowingWidgets(
								preferences.bool(forKey: .statisticsWidgetHideInLeagueList) != true
							)))
						}
					}

				case .didTapFilterButton:
					state.destination = .filters(.init(recurrence: state.filter.recurrence))
					return .none

				case .didTapSortOrderButton:
					state.destination = .sortOrder(.init(initialValue: state.ordering))
					return .none

				case let .didTapLeague(id):
					return .run { send in
						let seriesHost = try await leagues.seriesHost(id)
						switch seriesHost.recurrence {
						case .once:
							await send(.internal(.didLoadEventSeries(Result {
								let eventSeries = try await series.eventSeries(seriesHost.id)
								return EventSeries(host: seriesHost, series: eventSeries)
							})))
						case .repeating:
							await send(.internal(.didLoadSeriesLeague(.success(seriesHost))))
						}
					} catch: { error, send in
						await send(.internal(.didLoadSeriesLeague(.failure(error))))
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didSetIsShowingWidgets(isShowing):
					state.isShowingWidgets = isShowing
					return .none

				case let .didLoadEditableLeague(.success(league)):
					state.destination = .editor(.init(value: .edit(league)))
					return .none

				case let .didLoadSeriesLeague(.success(league)):
					state.destination = .series(.init(league: league))
					return recentlyUsed.didRecentlyUse(.leagues, id: league.id, in: self)

				case let .didLoadEventSeries(.success(event)):
					state.destination = .games(.init(series: event.series, host: event.host))
					return recentlyUsed.didRecentlyUse(.leagues, id: event.host.id, in: self)

				case .didArchiveLeague(.success):
					return .none

				case let .didLoadSeriesLeague(.failure(error)),
					let .didLoadEditableLeague(.failure(error)),
					let .didLoadEventSeries(.failure(error)):
					return state.errors
						.enqueue(.leagueNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case let .didArchiveLeague(.failure(error)):
					return state.errors
						.enqueue(.failedToArchiveLeague, thrownError: error, toastMessage: Strings.Error.Toast.failedToArchive)
						.map { .internal(.errors($0)) }

				case .widgets(.delegate(.doNothing)):
					return .none

				case let .preferredGear(.delegate(delegateAction)):
					switch delegateAction {
					case let .errorUpdatingPreferredGear(.failure(error)):
						return state.errors
							.enqueue(.failedToSavePreferredGear, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
							.map { .internal(.errors($0)) }

					case let .errorLoadingGear(.failure(error)):
						return state.errors
							.enqueue(.failedToLoadPreferredGear, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
							.map { .internal(.errors($0)) }
					}

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(league):
						return .run { send in
							await send(.internal(.didLoadEditableLeague(Result {
								try await leagues.edit(league.id)
							})))
						}

					case let .didArchive(league):
						return .run { send in
							await send(.internal(.didArchiveLeague(Result {
								try await leagues.archive(league.id)
								return league
							})))
						}

					case .didAddNew, .didTapEmptyStateButton:
						state.destination = .editor(.init(value: .create(.default(withId: uuid(), forBowler: state.bowler.id))))
						return .none

					case .didTap, .didDelete, .didMove:
						return .none
					}

				case let .destination(.presented(.sortOrder(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didTapOption(option):
						state.ordering = option
						return state.list.updateQuery(
							to: .init(
								filter: .init(bowler: state.bowler.id, recurrence: state.filter.recurrence),
								ordering: state.ordering
							)
						)
						.map { .internal(.list($0)) }
					}

				case let .destination(.presented(.filters(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didChangeFilters(recurrence):
						state.filter.recurrence = recurrence
						return state.list.updateQuery(
							to: .init(
								filter: .init(bowler: state.bowler.id, recurrence: state.filter.recurrence),
								ordering: state.ordering
							)
						)
						.map { .internal(.list($0)) }
					}

				case let .destination(.presented(.editor(.delegate(delegateAction)))):
					switch delegateAction {
					case .didFinishCreating, .didFinishUpdating, .didFinishArchiving:
						return .none
					}

				case .errors(.delegate(.doNothing)):
					return .none
				case .destination(.dismiss),
						.destination(.presented(.series(.internal))),
						.destination(.presented(.series(.view))),
						.destination(.presented(.series(.delegate(.doNothing)))),
						.destination(.presented(.filters(.internal))),
						.destination(.presented(.filters(.view))),
						.destination(.presented(.filters(.binding))),
						.destination(.presented(.editor(.internal))),
						.destination(.presented(.editor(.view))),
						.destination(.presented(.editor(.binding))),
						.destination(.presented(.sortOrder(.internal))),
						.destination(.presented(.sortOrder(.view))),
						.destination(.presented(.games(.internal))),
						.destination(.presented(.games(.view))),
						.destination(.presented(.games(.delegate(.doNothing)))),
						.preferredGear(.internal), .preferredGear(.view),
						.list(.internal), .list(.view), .list(.binding),
						.widgets(.internal), .widgets(.view),
						.errors(.internal), .errors(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination)

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .view(.didTapLeague):
				return Analytics.League.Viewed()
			case .internal(.list(.delegate(.didArchive))):
				return Analytics.League.Archived()
			default:
				return nil
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didLoadSeriesLeague(.failure(error))),
				let .internal(.didArchiveLeague(.failure(error))),
				let .internal(.didLoadEditableLeague(.failure(error))),
				let .internal(.didLoadEventSeries(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}

	private func fetchResources(
		query: League.List.FetchRequest
	) -> AsyncThrowingStream<[SectionList.Section], Error> {
		AsyncThrowingStream { continuation in
			let task = Task {
				do {
					for try await allLeagues in leagues.list(
						bowledBy: query.filter.bowler,
						withRecurrence: query.filter.recurrence,
						ordering: query.ordering
					) {
						let repeating = IdentifiedArrayOf<League.List>(
							uniqueElements: allLeagues.filter { $0.recurrence == .repeating }
						)
						let oneOffs = IdentifiedArrayOf<League.List>(
							uniqueElements: allLeagues.filter { $0.recurrence == .once }
						)

						continuation.yield([
							repeating.isEmpty ? nil : SectionList.Section(
								id: SectionID.leagues.rawValue,
								title: Strings.League.List.Repeating.title,
								items: repeating
							),
							oneOffs.isEmpty ? nil : SectionList.Section(
								id: SectionID.events.rawValue,
								title: Strings.League.List.Once.title,
								items: oneOffs
							),
						].compactMap { $0 })
					}
				}
			}

			continuation.onTermination = { _ in task.cancel() }
		}
	}
}

extension LeaguesList {
	public static func widgetContext(forBowler: Bowler.ID) -> String {
		"leaguesList-\(forBowler)"
	}
}

extension LeaguesList {
	public struct EventSeries {
		let host: League.SeriesHost
		let series: Series.GameHost
	}
}
