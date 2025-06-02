import AnalyticsServiceInterface
import AnnouncementsFeature
import ComposableArchitecture
import FeatureActionLibrary
import GamesListFeature
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
		public var quickLaunch = QuickLaunch.State()
		public var widgets: StatisticsWidgetLayout.State?

		@Presents public var destination: Destination.State?

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
			case quickLaunch(QuickLaunch.Action)
			case widgets(StatisticsWidgetLayout.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	@Reducer(state: .equatable)
	public enum Destination {
		case games(GamesList)
		case seriesEditor(SeriesEditor)
	}

	public init() {}

	@Dependency(\.preferences) var preferences

	public var body: some ReducerOf<Self> {
		Scope(state: \.announcements, action: \.internal.announcements) {
			Announcements()
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

				case let .quickLaunch(.delegate(delegateAction)):
					switch delegateAction {
					case let .createSeries(series, league):
						state.destination = .seriesEditor(SeriesEditor.State(value: .create(series), inLeague: league))
						return .none
					}

				case let .destination(.presented(.seriesEditor(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didFinishCreating(created):
						guard let league = state.quickLaunch.source.value?.league else { return .none }
						state.destination = .games(GamesList.State(series: created.asGameHost, host: league))
						return .none

					case .didFinishArchiving, .didFinishUpdating:
						return .none
					}

				case .destination(.dismiss),
						.destination(.presented(.games(.delegate(.doNothing)))),
						.destination(.presented(.games(.view))),
						.destination(.presented(.games(.internal))),
						.destination(.presented(.seriesEditor(.internal))),
						.destination(.presented(.seriesEditor(.view))),
						.destination(.presented(.seriesEditor(.binding))),
						.announcements(.internal), .announcements(.view), .announcements(.delegate(.doNothing)),
						.quickLaunch(.view), .quickLaunch(.internal),
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
