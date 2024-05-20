import AccessoriesOverviewFeature
import AnalyticsServiceInterface
import BowlersListFeature
import ComposableArchitecture
import FeatureActionLibrary
import SettingsFeature
import StatisticsOverviewFeature

@Reducer
public struct TabbedContent: Reducer {

	@ObservableState
	public struct State: Equatable {
		public var selectedTab: Tab = .overview

		public var accessories = AccessoriesOverview.State()
		public var bowlersList = BowlersList.State()
		public var statistics = StatisticsOverview.State()
		public var settings = Settings.State()

		public init() {}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case didAppear
		}
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal {
			case accessories(AccessoriesOverview.Action)
			case bowlersList(BowlersList.Action)
			case settings(Settings.Action)
			case statistics(StatisticsOverview.Action)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
		case binding(BindingAction<State>)
	}

	public enum Tab {
		case overview
		case statistics
		case accessories
		case settings
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Scope(state: \.bowlersList, action: \.internal.bowlersList) {
			BowlersList()
		}

		Scope(state: \.settings, action: \.internal.settings) {
			Settings()
		}

		Scope(state: \.accessories, action: \.internal.accessories) {
			AccessoriesOverview()
		}

		Scope(state: \.statistics, action: \.internal.statistics) {
			StatisticsOverview()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didAppear:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .bowlersList(.internal(.destination(.presented(.announcement(.delegate(.openAppIconSettings)))))):
					state.selectedTab = .settings
					return state.settings.showAppIconList().map { .internal(.settings($0)) }

				case .accessories(.view), .accessories(.internal), .accessories(.delegate(.doNothing)),
						.bowlersList(.view), .bowlersList(.internal), .bowlersList(.delegate(.doNothing)),
						.settings(.view), .settings(.internal), .settings(.delegate(.doNothing)), .settings(.binding),
						.statistics(.view), .statistics(.internal), .statistics(.delegate(.doNothing)):
					return .none
				}

			case .delegate, .binding:
				return .none
			}
		}

		AnalyticsReducer<State, Action> { state, action in
			switch action {
			case .binding(\.selectedTab):
				return Analytics.App.TabSwitched(tab: String(describing: state.selectedTab))
			default:
				return nil
			}
		}
	}
}
