import AnalyticsServiceInterface
import AnnouncementsFeature
import BowlerDetailsFeature
import BowlerEditorFeature
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import FeatureFlagsLibrary
import GamesListFeature
import LeaguesListFeature
import ModelsLibrary
import PreferenceServiceInterface
import QuickLaunchRepositoryInterface
import SeriesEditorFeature
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
		case bowlerDetails(BowlerDetails)
		case bowlerEditor(BowlerEditor)
		case leaguesList(LeaguesList)
		case gamesList(GamesList)
		case seriesEditor(SeriesEditor)
	}

	public enum ErrorID: Hashable {
		case bowlers(BowlersSection.ErrorID)
		case teams(TeamsSection.ErrorID)
	}

	public init() {}

	@Dependency(\.errors) var errors
	@Dependency(\.featureFlags) var featureFlags
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
					return .send(.internal(.showingWidgetsPreferenceDidChange))

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

				case let .bowlers(.delegate(delegateAction)):
					switch delegateAction {
					case let .createBowler(bowler):
						state.destination = .bowlerEditor(BowlerEditor.State(value: .create(bowler)))
						return .none

					case let .editBowler(bowler):
						state.destination = .bowlerEditor(BowlerEditor.State(value: .edit(bowler)))
						return .none

					case let .showBowlerDetails(bowler):
						state.destination = if featureFlags.isFlagEnabled(.bowlerDetails) {
							.bowlerDetails(BowlerDetails.State(bowler: bowler))
						} else {
							.leaguesList(LeaguesList.State(bowler: bowler))
						}
						return .none

					case let .didReceiveError(id, error, message):
						return state.errors
							.enqueue(.bowlers(id), thrownError: error, toastMessage: message)
							.map { .internal(.errors($0)) }
					}

				case let .quickLaunch(.delegate(delegateAction)):
					switch delegateAction {
					case let .createSeries(series, league):
						state.destination = .seriesEditor(SeriesEditor.State(value: .create(series), inLeague: league))
						return .none
					}

				case let .teams(.delegate(delegateAction)):
					switch delegateAction {
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
						.destination(.presented(.bowlerDetails(.delegate(.doNothing)))),
						.destination(.presented(.bowlerDetails(.view))),
						.destination(.presented(.bowlerDetails(.internal))),
						.destination(.presented(.bowlerEditor(.delegate(.doNothing)))),
						.destination(.presented(.bowlerEditor(.view))),
						.destination(.presented(.bowlerEditor(.internal))),
						.destination(.presented(.bowlerEditor(.binding))),
						.destination(.presented(.gamesList(.delegate(.doNothing)))),
						.destination(.presented(.gamesList(.view))),
						.destination(.presented(.gamesList(.internal))),
						.destination(.presented(.leaguesList(.delegate(.doNothing)))),
						.destination(.presented(.leaguesList(.view))),
						.destination(.presented(.leaguesList(.internal))),
						.destination(.presented(.seriesEditor(.internal))),
						.destination(.presented(.seriesEditor(.view))),
						.destination(.presented(.seriesEditor(.binding))),
						.announcements(.internal), .announcements(.view), .announcements(.delegate(.doNothing)),
						.bowlers(.internal), .bowlers(.view),
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

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.bowlers(.delegate(.didReceiveError(_, error, _)))):
				return error
			default:
				return nil
			}
		}
	}
}
