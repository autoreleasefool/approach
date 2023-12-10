import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import EquatableLibrary
import ErrorsFeature
import FeatureActionLibrary
import FeatureFlagsServiceInterface
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
		case .oldestFirst: return Strings.Ordering.oldestFirst
		case .newestFirst: return Strings.Ordering.newestFirst
		case .highestToLowest: return Strings.Ordering.highestToLowest
		case .lowestToHighest: return Strings.Ordering.lowestToHighest
		}
	}
}

extension Series.List: ResourceListItem {
	public var name: String {
		date.longFormat
	}
}

// swiftlint:disable:next type_body_length
public struct SeriesList: Reducer {
	public struct State: Equatable {
		public var league: League.SeriesHost
		public var ordering: Series.Ordering = .newestFirst

		public var list: SectionResourceList<Series.List, Series.List.FetchRequest>.State

		public var seriesToNavigate: Series.ID?

		public var errors: Errors<ErrorID>.State = .init()

		@PresentationState public var destination: Destination.State?

		public init(league: League.SeriesHost) {
			self.league = league
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToArchive,
				],
				query: .init(league: league.id, ordering: ordering),
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

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case onAppear
			case didTapEditButton
			case didTapSortOrderButton
			case didTapSeries(Series.ID)
		}

		public enum InternalAction: Equatable {
			case didArchiveSeries(TaskResult<Series.List>)
			case didLoadEditableSeries(TaskResult<Series.Edit>)
			case didLoadEditableLeague(TaskResult<League.Edit>)

			case errors(Errors<ErrorID>.Action)
			case destination(PresentationAction<Destination.Action>)
			case list(SectionResourceList<Series.List, Series.List
				.FetchRequest>.Action)
		}

		public enum DelegateAction: Equatable { case doNothing }

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public enum ErrorID: Hashable {
		case leagueNotFound
		case seriesNotFound
		case failedToArchiveSeries
	}

	public enum SectionID: String {
		case regular
		case preBowl
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case seriesEditor(SeriesEditor.State)
			case leagueEditor(LeagueEditor.State)
			case games(GamesList.State)
			case sortOrder(SortOrderLibrary.SortOrder<Series.Ordering>.State)
		}

		public enum Action: Equatable {
			case seriesEditor(SeriesEditor.Action)
			case leagueEditor(LeagueEditor.Action)
			case games(GamesList.Action)
			case sortOrder(SortOrderLibrary.SortOrder<Series.Ordering>.Action)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.leagueEditor, action: /Action.leagueEditor) {
				LeagueEditor()
			}
			Scope(state: /State.seriesEditor, action: /Action.seriesEditor) {
				SeriesEditor()
			}
			Scope(state: /State.games, action: /Action.games) {
				GamesList()
			}
			Scope(state: /State.sortOrder, action: /Action.sortOrder) {
				SortOrder()
			}
		}
	}

	public init() {}

	@Dependency(\.calendar) var calendar
	@Dependency(\.date) var date
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.featureFlags) var featureFlags
	@Dependency(\.leagues) var leagues
	@Dependency(\.series) var series
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
		}

		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			SectionResourceList(fetchSections: self.fetchResources(query:))
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case let .didTapSeries(id):
					if let series = state.list.findResource(byId: id) {
						state.destination = .games(.init(series: series.asSummary, host: state.league))
					}
					return .none

				case .didTapEditButton:
					return .run { [id = state.league.id] send in
						await send(.internal(.didLoadEditableLeague(TaskResult {
							try await leagues.edit(id)
						})))
					}

				case .didTapSortOrderButton:
					state.destination = .sortOrder(.init(initialValue: state.ordering))
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadEditableSeries(.success(series)):
					state.destination = .seriesEditor(.init(value: .edit(series), inLeague: state.league))
					return .none

				case let .didLoadEditableLeague(.success(league)):
					state.destination = .leagueEditor(.init(value: .edit(league)))
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

				case .list(.internal(.sectionsResponse)):
					if let seriesToNavigate = state.seriesToNavigate {
						if let destination = state.list.findResource(byId: seriesToNavigate) {
							state.seriesToNavigate = nil
							state.destination = .games(.init(series: destination.asSummary, host: state.league))
						} else {
							return .send(.internal(.didLoadEditableSeries(.failure(SeriesListError.seriesNotFound(seriesToNavigate)))))
						}
					}
					return .none

				case let .list(.delegate(delegateAction)):
					switch delegateAction {
					case let .didEdit(series):
						return .run { send in
							await send(.internal(.didLoadEditableSeries(TaskResult {
								try await self.series.edit(series.id)
							})))
						}

					case let .didArchive(series):
						return .run { send in
							await send(.internal(.didArchiveSeries(TaskResult {
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
							state.destination = .games(.init(series: series.asSummary, host: state.league))
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

				case .destination(.presented(.games(.delegate(.doNothing)))):
					return .none

				case let .destination(.presented(.sortOrder(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didTapOption(option):
						state.ordering = option
						return state.list.updateQuery(to: .init(league: state.league.id, ordering: state.ordering))
							.map { .internal(.list($0)) }
					}

				case .errors(.delegate(.doNothing)):
					return .none

				case .destination(.dismiss),
						.destination(.presented(.seriesEditor(.view))), .destination(.presented(.seriesEditor(.internal))),
						.destination(.presented(.leagueEditor(.view))), .destination(.presented(.leagueEditor(.internal))),
						.destination(.presented(.games(.view))), .destination(.presented(.games(.internal))),
						.destination(.presented(.sortOrder(.internal))), .destination(.presented(.sortOrder(.view))),
						.list(.view), .list(.internal),
						.errors(.view), .errors(.internal):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: /Action.internal..Action.InternalAction.destination) {
			Destination()
		}

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
	}

	private func fetchResources(
		query: Series.List.FetchRequest
	) -> AsyncThrowingStream<[SectionResourceList<Series.List, Series.List.FetchRequest>.Section], Swift.Error> {
		return .init { continuation in
			let task = Task {
				do {
					for try await series in self.series.list(bowledIn: query.league, orderedBy: query.ordering) {
						let preBowlSeries: IdentifiedArrayOf<Series.List>
						let regularSeries: IdentifiedArrayOf<Series.List>
						switch query.ordering {
						case .newestFirst:
							preBowlSeries = .init(uniqueElements: series.filter {
								switch $0.preBowl {
								case .preBowl: return true
								case .regular: return false
								}
							})

							regularSeries = .init(uniqueElements: series.filter {
								switch $0.preBowl {
								case .preBowl: return false
								case .regular: return true
								}
							})
						case .oldestFirst, .highestToLowest, .lowestToHighest:
							preBowlSeries = []
							regularSeries = .init(uniqueElements: series)
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

public enum SeriesListError: Error, LocalizedError {
	case seriesNotFound(Series.ID)

	public var errorDescription: String? {
		switch self {
		case let .seriesNotFound(id):
			return "Could not find Series with ID '\(id)'"
		}
	}
}
