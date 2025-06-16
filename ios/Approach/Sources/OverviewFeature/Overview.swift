import AnalyticsServiceInterface
import AnnouncementsFeature
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import GamesListFeature
import GamesRepositoryInterface
import ModelsLibrary
import PreferenceServiceInterface
import QuickLaunchRepositoryInterface
import SeriesEditorFeature
import SortOrderLibrary
import StatisticsWidgetsLayoutFeature

@Reducer
public struct Overview: Reducer, Sendable {
	public static let widgetContext = "bowlersList"

	@ObservableState
	public struct State: Equatable {
		public var announcements = Announcements.State()
		public var bowlers = BowlersSection.State()
		public var quickLaunch = QuickLaunch.State()
		public var teams = TeamsSection.State()
		public var widgets: StatisticsWidgetLayout.State?

		@Presents public var destination: Destination.State?
		public var errors: Errors<ErrorID>.State = .init()

		public init() {}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case onAppear
			case didStartTask
		}

		@CasePathable
		public enum Delegate { case doNothing }

		@CasePathable
		public enum Internal {
			case showingWidgetsPreferenceDidChange

			case announcements(Announcements.Action)
			case bowlers(BowlersSection.Action)
			case quickLaunch(QuickLaunch.Action)
			case teams(TeamsSection.Action)
			case widgets(StatisticsWidgetLayout.Action)
			case errors(Errors<ErrorID>.Action)

			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	@Reducer(state: .equatable)
	public enum Destination {
		case gamesList(GamesList)
		case seriesEditor(SeriesEditor)
		case teamSortOrder(SortOrderLibrary.SortOrder<Team.List.FetchRequest>)
	}

	public enum ErrorID: Hashable, Sendable {
		case teams(TeamsSection.ErrorID)
	}

	public init() {}

	@Dependency(\.errors) var errors
	@Dependency(GamesRepository.self) var games
	@Dependency(\.preferences) var preferences

	public var body: some ReducerOf<Self> {
		Scope(state: \.announcements, action: \.internal.announcements) {
			Announcements()
		}

		Scope(state: \.bowlers, action: \.internal.bowlers) {
			BowlersSection()
		}

		Scope(state: \.teams, action: \.internal.teams) {
			TeamsSection()
		}

		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Scope(state: \.quickLaunch, action: \.internal.quickLaunch) {
			QuickLaunch()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .merge(
						.run { _ in
							try await games.lockStaleGames()
						} catch: { error, _ in
							errors.captureError(error)
						},
						.send(.internal(.showingWidgetsPreferenceDidChange))
					)

				case .didStartTask:
					return .run { send in
						for await _ in preferences.observe(keys: [.statisticsWidgetHideInBowlerList]) {
							await send(.internal(.showingWidgetsPreferenceDidChange))
						}
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case .showingWidgetsPreferenceDidChange:
					let isShowingWidgets = preferences.bool(forKey: .statisticsWidgetHideInBowlerList) != true
					state.widgets = isShowingWidgets ? .init(context: Overview.widgetContext, newWidgetSource: nil) : nil
					return .none

				case let .quickLaunch(.delegate(delegateAction)):
					switch delegateAction {
					case let .createSeries(series, league):
						state.destination = .seriesEditor(SeriesEditor.State(value: .create(series), inLeague: league))
						return .none
					}

				case let .teams(.delegate(delegateAction)):
					switch delegateAction {
					case .showSortOrder:
						state.destination = .teamSortOrder(.init(initialValue: state.teams.$fetchRequest))
						return .none

					case let .didReceiveError(id, error, message):
						return state.errors
							.enqueue(.teams(id), thrownError: error, toastMessage: message)
							.map { .internal(.errors($0)) }
					}

				case let .destination(.presented(.seriesEditor(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didFinishCreating(created):
						guard let league = state.quickLaunch.source.value??.league else { return .none }
						state.destination = .gamesList(GamesList.State(series: created.asGameHost, host: league))
						return .none

					case .didFinishArchiving, .didFinishUpdating:
						return .none
					}

				case .destination(.dismiss),
						.destination(.presented(.gamesList(.delegate(.doNothing)))),
						.destination(.presented(.gamesList(.view))),
						.destination(.presented(.gamesList(.internal))),
						.destination(.presented(.seriesEditor(.internal))),
						.destination(.presented(.seriesEditor(.view))),
						.destination(.presented(.seriesEditor(.binding))),
						.destination(.presented(.teamSortOrder(.internal))),
						.destination(.presented(.teamSortOrder(.view))),
						.destination(.presented(.teamSortOrder(.delegate(.doNothing)))),
						.announcements(.internal), .announcements(.view), .announcements(.delegate(.doNothing)),
						.bowlers(.internal), .bowlers(.view), .bowlers(.delegate(.doNothing)),
						.errors(.view), .errors(.internal), .errors(.delegate(.doNothing)),
						.quickLaunch(.view), .quickLaunch(.internal),
						.teams(.internal), .teams(.view),
						.widgets(.delegate(.doNothing)), .widgets(.view), .widgets(.internal):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination)
		.ifLet(\.widgets, action: \.internal.widgets) {
			StatisticsWidgetLayout()
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}
	}
}
