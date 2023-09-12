import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import GearRepositoryInterface
import LeagueEditorFeature
import LeaguesRepositoryInterface
import ModelsLibrary
import PreferenceServiceInterface
import RecentlyUsedServiceInterface
import ResourceListLibrary
import SeriesListFeature
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

// swiftlint:disable:next type_body_length
public struct LeaguesList: Reducer {
	public struct State: Equatable {
		public let bowler: Bowler.Summary

		public var list: ResourceList<League.List, League.List.FetchRequest>.State
		public var preferredGear: PreferredGear.State
		public var widgets: StatisticsWidgetLayout.State

		public var ordering: League.Ordering = .byRecentlyUsed
		public var filter: League.List.FetchRequest.Filter

		public var isShowingWidgets: Bool

		public var errors: Errors<ErrorID>.State = .init()

		@PresentationState public var destination: Destination.State?

		public init(bowler: Bowler.Summary) {
			self.bowler = bowler
			self.filter = .init(bowler: bowler.id)
			self.widgets = .init(context: LeaguesList.widgetContext(forBowler: bowler.id), newWidgetSource: .bowler(bowler.id))
			self.preferredGear = .init(bowler: bowler.id)
			self.list = .init(
				features: [
					.add,
					.swipeToEdit,
					.swipeToDelete,
				],
				query: .init(
					filter: filter,
					ordering: ordering
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

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didStartObserving
			case didTapLeague(id: League.ID)
			case didTapFilterButton
			case didTapSortOrderButton
		}

		public enum DelegateAction: Equatable {}

		public enum InternalAction: Equatable {
			case didLoadEditableLeague(TaskResult<League.Edit>)
			case didDeleteLeague(TaskResult<League.List>)
			case didLoadSeriesLeague(TaskResult<League.SeriesHost>)
			case didSetIsShowingWidgets(Bool)

			case errors(Errors<ErrorID>.Action)
			case preferredGear(PreferredGear.Action)
			case list(ResourceList<League.List, League.List.FetchRequest>.Action)
			case widgets(StatisticsWidgetLayout.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case editor(LeagueEditor.State)
			case filters(LeaguesFilter.State)
			case series(SeriesList.State)
			case sortOrder(SortOrder<League.Ordering>.State)
		}

		public enum Action: Equatable {
			case editor(LeagueEditor.Action)
			case filters(LeaguesFilter.Action)
			case series(SeriesList.Action)
			case sortOrder(SortOrder<League.Ordering>.Action)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.editor, action: /Action.editor) {
				LeagueEditor()
			}
			Scope(state: /State.filters, action: /Action.filters) {
				LeaguesFilter()
			}
			Scope(state: /State.series, action: /Action.series) {
				SeriesList()
			}
			Scope(state: /State.sortOrder, action: /Action.sortOrder) {
				SortOrder()
			}
		}
	}

	public enum ErrorID: Hashable {
		case leagueNotFound
		case failedToDeleteLeague
		case failedToLoadPreferredGear
		case failedToSavePreferredGear
	}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.featureFlags) var featureFlags
	@Dependency(\.gear) var gear
	@Dependency(\.leagues) var leagues
	@Dependency(\.preferences) var preferences
	@Dependency(\.recentlyUsed) var recentlyUsed
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
		}

		Scope(state: \.list, action: /Action.internal..Action.InternalAction.list) {
			ResourceList { request in
				leagues.list(
					bowledBy: request.filter.bowler,
					withRecurrence: request.filter.recurrence,
					ordering: request.ordering
				)
			}
		}

		Scope(state: \.preferredGear, action: /Action.internal..Action.InternalAction.preferredGear) {
			PreferredGear()
		}

		Scope(state: \.widgets, action: /Action.internal..Action.InternalAction.widgets) {
			StatisticsWidgetLayout()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didStartObserving:
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
						await send(.internal(.didLoadSeriesLeague(TaskResult {
							try await leagues.seriesHost(id)
						})))
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
					return .run { _ in
						try await clock.sleep(for: .seconds(1))
						recentlyUsed.didRecentlyUseResource(.leagues, league.id)
					}

				case .didDeleteLeague(.success):
					return .none

				case let .didLoadSeriesLeague(.failure(error)), let .didLoadEditableLeague(.failure(error)):
					return state.errors
						.enqueue(.leagueNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case let .didDeleteLeague(.failure(error)):
					return state.errors
						.enqueue(.failedToDeleteLeague, thrownError: error, toastMessage: Strings.Error.Toast.failedToDelete)
						.map { .internal(.errors($0)) }

				case let .widgets(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

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
							await send(.internal(.didLoadEditableLeague(TaskResult {
								try await leagues.edit(league.id)
							})))
						}

					case let .didDelete(league):
						return .run { send in
							await send(.internal(.didDeleteLeague(TaskResult {
								try await leagues.delete(league.id)
								return league
							})))
						}

					case .didAddNew, .didTapEmptyStateButton:
						state.destination = .editor(.init(value: .create(.default(withId: uuid(), forBowler: state.bowler.id))))
						return .none

					case .didTap:
						return .none
					}

				case let .destination(.presented(.sortOrder(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didTapOption(option):
						state.ordering = option
						return state.list.updateQuery(to: .init(filter: state.filter, ordering: state.ordering))
							.map { .internal(.list($0)) }
					}

				case let .destination(.presented(.filters(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didChangeFilters(recurrence):
						state.filter.recurrence = recurrence
						return state.list.updateQuery(to: .init(filter: state.filter, ordering: state.ordering))
							.map { .internal(.list($0)) }
					}

				case let .destination(.presented(.editor(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .destination(.presented(.series(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .errors(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .destination(.dismiss),
						.destination(.presented(.series(.internal))),
						.destination(.presented(.series(.view))),
						.destination(.presented(.filters(.internal))),
						.destination(.presented(.filters(.view))),
						.destination(.presented(.editor(.internal))),
						.destination(.presented(.editor(.view))),
						.destination(.presented(.sortOrder(.internal))),
						.destination(.presented(.sortOrder(.view))),
						.preferredGear(.internal), .preferredGear(.view),
						.list(.internal), .list(.view),
						.widgets(.internal), .widgets(.view),
						.errors(.internal), .errors(.view):
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
			case .view(.didTapLeague):
				return Analytics.League.Viewed()
			case .internal(.list(.delegate(.didDelete))):
				return Analytics.League.Deleted()
			default:
				return nil
			}
		}
	}
}

extension LeaguesList {
	public static func widgetContext(forBowler: Bowler.ID) -> String {
		"leaguesList-\(forBowler)"
	}
}

extension LeaguesList.State {
	var filters: LeaguesFilter.State {
		get { .init(recurrence: filter.recurrence) }
		set { filter.recurrence = newValue.recurrence }
	}
}
