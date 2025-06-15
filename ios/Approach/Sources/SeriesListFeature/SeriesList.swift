import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import EquatablePackageLibrary
import ErrorsFeature
import FeatureActionLibrary
import Foundation
import GamesListFeature
import LeagueEditorFeature
import LeaguesRepositoryInterface
import ListContentLibrary
import ModelsLibrary
import ResourceListLibrary
import SeriesEditorFeature
import SeriesRepositoryInterface
import SortOrderLibrary
import StringsLibrary
import ViewsLibrary

extension Series.Ordering: CustomStringConvertible {
	public var description: String {
		switch self {
		case .oldestFirst: Strings.Ordering.oldestFirst
		case .newestFirst: Strings.Ordering.newestFirst
		case .highestToLowest: Strings.Ordering.highestToLowest
		case .lowestToHighest: Strings.Ordering.lowestToHighest
		}
	}
}

extension Series.List: ResourceListItem {
	public var name: String {
		date.longFormat
	}
}

@Reducer
public struct SeriesList: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var league: League.SeriesHost

		@Shared public var fetchRequest: Series.List.FetchRequest
		@Shared public var ordering: Series.Ordering

		public var list: SectionResourceList<Series.List, Series.List.FetchRequest>.State

		public var seriesToNavigate: Series.ID?

		public var errors: Errors<ErrorID>.State = .init()

		@Presents public var destination: Destination.State?

		var hasPreBowls: Bool {
			list.sections?.first { $0.id == SectionID.preBowl.rawValue }?.items.isEmpty == false
		}

		public init(league: League.SeriesHost) {
			self.league = league
			let ordering = Shared(value: Series.Ordering.newestFirst)
			let fetchRequest = Shared(
				value: Series.List.FetchRequest(
					league: league.id,
					ordering: ordering.wrappedValue
				)
			)
			self._fetchRequest = fetchRequest
			self._ordering = ordering

			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToArchive,
				],
				query: SharedReader(fetchRequest),
				listTitle: nil,
				emptyContent: .init(
					image: Asset.Media.EmptyState.series,
					title: Strings.Series.Error.Empty.title,
					message: Strings.Series.Error.Empty.message,
					action: Strings.Series.List.add
				)
			)
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case onAppear
			case didTapEditButton
			case didTapSortOrderButton
			case didTapUpdatePreBowlsButton
			case didTapSeries(Series.ID)
		}

		@CasePathable
		public enum Internal {
			case didArchiveSeries(Result<Series.List, Error>)
			case didLoadEditableSeries(Result<Series.Edit, Error>)
			case didLoadEditableLeague(Result<League.Edit, Error>)
			case didLoadGameSeries(Result<Series.GameHost, Error>)
			case didChangeOrdering(Series.Ordering)

			case errors(Errors<ErrorID>.Action)
			case destination(PresentationAction<Destination.Action>)
			case list(SectionResourceList<Series.List, Series.List
				.FetchRequest>.Action)
		}

		@CasePathable
		public enum Delegate { case doNothing }

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	public enum ErrorID: Hashable, Sendable {
		case leagueNotFound
		case seriesNotFound
		case failedToArchiveSeries
	}

	public enum SectionID: String {
		case regular
		case preBowl
	}

	@Reducer(state: .equatable)
	public enum Destination {
		case seriesEditor(SeriesEditor)
		case leagueEditor(LeagueEditor)
		case games(GamesList)
		case sortOrder(SortOrderLibrary.SortOrder<Series.Ordering>)
		case preBowl(SeriesPreBowlEditor)
	}

	public init() {}

	@Dependency(\.calendar) var calendar
	@Dependency(\.date) var date
	@Dependency(\.dismiss) var dismiss
	@Dependency(LeaguesRepository.self) var leagues
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

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .publisher {
						state.$ordering.publisher
							.map { .internal(.didChangeOrdering($0)) }
					}

				case let .didTapSeries(id):
					return .run { send in
						await send(.internal(.didLoadGameSeries(Result {
							try await series.gameHost(id)
						})))
					}

				case .didTapEditButton:
					return .run { [id = state.league.id] send in
						await send(.internal(.didLoadEditableLeague(Result {
							try await leagues.edit(id)
						})))
					}

				case .didTapSortOrderButton:
					state.destination = .sortOrder(.init(initialValue: state.$ordering))
					return .none

				case .didTapUpdatePreBowlsButton:
					state.destination = .preBowl(.init(league: state.league.id))
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didChangeOrdering(ordering):
					state.$fetchRequest.withLock { $0.ordering = ordering }
					return .none

				case let .didLoadEditableSeries(.success(series)):
					state.destination = .seriesEditor(.init(value: .edit(series), inLeague: state.league))
					return .none

				case let .didLoadEditableLeague(.success(league)):
					state.destination = .leagueEditor(.init(value: .edit(league)))
					return .none

				case let .didLoadGameSeries(.success(series)):
					state.destination = .games(.init(series: series, host: state.league))
					return .none

				case .didArchiveSeries(.success):
					return .none

				case let .didArchiveSeries(.failure(error)):
					return state.errors
						.enqueue(.failedToArchiveSeries, thrownError: error, toastMessage: Strings.Error.Toast.failedToArchive)
						.map { .internal(.errors($0)) }

				case let .didLoadEditableSeries(.failure(error)):
					return state.errors
						.enqueue(.seriesNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case let .didLoadEditableLeague(.failure(error)):
					return state.errors
						.enqueue(.leagueNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case let .didLoadGameSeries(.failure(error)):
					return state.errors
						.enqueue(.seriesNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case .list(.internal(.sectionsResponse)):
					if let seriesToNavigate = state.seriesToNavigate {
						if let destination = state.list.findResource(byId: seriesToNavigate) {
							state.seriesToNavigate = nil
							state.destination = .games(.init(series: destination.asGameHost, host: state.league))
						} else {
							return .send(.internal(.didLoadEditableSeries(.failure(SeriesListError.seriesNotFound(seriesToNavigate)))))
						}
					}
					return .none

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(series):
						return .run { send in
							await send(.internal(.didLoadEditableSeries(Result {
								try await self.series.edit(series.id)
							})))
						}

					case let .didArchive(series):
						return .run { send in
							await send(.internal(.didArchiveSeries(Result {
								try await self.series.archive(series.id)
								return series
							})))
						}

					case .didAddNew, .didTapEmptyStateButton:
						state.destination = .seriesEditor(.init(
							value: .create(.default(withId: uuid(), onDate: calendar.startOfDay(for: date()), inLeague: state.league)),
							inLeague: state.league
						))
						return .none

					case .didTap, .didDelete, .didMove:
						return .none
					}

				case let .destination(.presented(.seriesEditor(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didFinishCreating(created):
						if let series = state.list.findResource(byId: created.id) {
							state.destination = .games(.init(series: series.asGameHost, host: state.league))
						} else {
							state.seriesToNavigate = created.id
						}
						return .none

					case .didFinishArchiving, .didFinishUpdating:
						return .none
					}

				case let .destination(.presented(.leagueEditor(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didFinishUpdating(league):
						state.league = league.asSeriesHost
						return .none

					case .didFinishArchiving:
						return .run { _ in await dismiss() }

					case .didFinishCreating:
						return .none
					}

				case .destination(.presented(.preBowl(.delegate(.doNothing)))):
					return .none

				case .destination(.presented(.games(.delegate(.doNothing)))):
					return .none

				case .destination(.presented(.sortOrder(.delegate(.doNothing)))):
					return .none

				case .errors(.delegate(.doNothing)):
					return .none

				case .destination(.dismiss),
						.destination(.presented(.seriesEditor(.view))),
						.destination(.presented(.seriesEditor(.internal))),
						.destination(.presented(.seriesEditor(.binding))),
						.destination(.presented(.preBowl(.view))),
						.destination(.presented(.preBowl(.internal))),
						.destination(.presented(.preBowl(.binding))),
						.destination(.presented(.leagueEditor(.binding))),
						.destination(.presented(.leagueEditor(.view))),
						.destination(.presented(.leagueEditor(.internal))),
						.destination(.presented(.games(.view))), .destination(.presented(.games(.internal))),
						.destination(.presented(.sortOrder(.internal))), .destination(.presented(.sortOrder(.view))),
						.list(.view), .list(.internal), .list(.binding),
						.errors(.view), .errors(.internal):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination)

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .view(.didTapSeries):
				return Analytics.Series.Viewed()
			case .internal(.didArchiveSeries(.success)):
				return Analytics.Series.Archived()
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
			case let .internal(.didArchiveSeries(.failure(error))),
				let .internal(.didLoadEditableSeries(.failure(error))),
				let .internal(.didLoadEditableLeague(.failure(error))),
				let .internal(.didLoadGameSeries(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}

	private func fetchResources(
		query: Series.List.FetchRequest
	) -> AsyncThrowingStream<[SectionResourceList<Series.List, Series.List.FetchRequest>.Section], Swift.Error> {
		AsyncThrowingStream { continuation in
			let task = Task {
				do {
					for try await allSeries in series.list(bowledIn: query.league, orderedBy: query.ordering) {
						let preBowlSeries: IdentifiedArrayOf<Series.List>
						let regularSeries: IdentifiedArrayOf<Series.List>
						switch query.ordering {
						case .newestFirst:
							preBowlSeries = .init(uniqueElements: allSeries.filter {
								switch $0.preBowl {
								case .preBowl: return $0.appliedDate == nil
								case .regular: return false
								}
							})

							regularSeries = .init(uniqueElements: allSeries.filter { !preBowlSeries.ids.contains($0.id) })
						case .oldestFirst, .highestToLowest, .lowestToHighest:
							preBowlSeries = []
							regularSeries = .init(uniqueElements: allSeries)
						}

						continuation.yield([
							preBowlSeries.isEmpty ? nil : .init(
								id: SectionID.preBowl.rawValue,
								title: Strings.Series.PreBowl.title,
								items: preBowlSeries
							),
							regularSeries.isEmpty ? nil : .init(
								id: SectionID.regular.rawValue,
								title: Strings.Series.List.title,
								items: regularSeries
							),
						].compactMap { $0 })
					}
				} catch {
					continuation.finish(throwing: error)
				}
			}

			continuation.onTermination = { _ in task.cancel() }
		}
	}
}
