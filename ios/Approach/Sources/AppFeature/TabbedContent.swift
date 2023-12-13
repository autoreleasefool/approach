import AccessoriesOverviewFeature
import AnalyticsServiceInterface
import BowlersListFeature
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import SettingsFeature
import StatisticsOverviewFeature

@Reducer
public struct TabbedContent: Reducer {
	public struct State: Equatable {
		public var tabs: [Tab] = [.overview]
		@BindingState public var selectedTab: Tab = .overview
		public var accessories = AccessoriesOverview.State()
		public var bowlersList = BowlersList.State()
		public var statistics = StatisticsOverview.State()
		public var settings = Settings.State()

		public init() {}
	}

	public enum Action: FeatureAction {
		@CasePathable public enum ViewAction: BindableAction {
			case didAppear
			case binding(BindingAction<State>)
		}
		@CasePathable public enum DelegateAction { case doNothing }
		@CasePathable public enum InternalAction {
			case didChangeTabs([Tab])
			case accessories(AccessoriesOverview.Action)
			case bowlersList(BowlersList.Action)
			case settings(Settings.Action)
			case statistics(StatisticsOverview.Action)
		}
		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public enum Tab: String, Identifiable, CaseIterable, CustomStringConvertible {
		case overview
		case statistics
		case accessories
		case settings

		public var id: String { rawValue }
		public var description: String { rawValue }
		public var featureFlag: FeatureFlag? {
			switch self {
			case .overview: return nil
			case .statistics: return nil
			case .accessories: return nil
			case .settings: return nil
			}
		}
	}

	public init() {}

	@Dependency(\.featureFlags) var featureFlags

	public var body: some ReducerOf<Self> {
		BindingReducer(action: \.view)

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
					return .run { send in
						let expectedFlags = TabbedContent.Tab.allCases.compactMap { $0.featureFlag }
						for await flags in featureFlags.observeAll(expectedFlags) {
							await send(.internal(.didChangeTabs(TabbedContent.Tab.allCases.filter {
								guard let tabFlag = $0.featureFlag else { return true }
								return flags[tabFlag] ?? true
							})))
						}
					}

				case .binding:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .bowlersList(.internal(.announcements(.internal(.destination(.presented(.delegate(.openAppIconSettings))))))):
					state.selectedTab = .settings
					return state.settings.showAppIconList().map { .internal(.settings($0)) }

				case let .didChangeTabs(tabs):
					state.tabs = tabs
					return .none

				case .accessories(.view), .accessories(.internal), .accessories(.delegate(.doNothing)),
						.bowlersList(.view), .bowlersList(.internal), .bowlersList(.delegate(.doNothing)),
						.settings(.view), .settings(.internal), .settings(.delegate(.doNothing)),
						.statistics(.view), .statistics(.internal), .statistics(.delegate(.doNothing)):
					return .none
				}

			case .delegate:
				return .none
			}
		}

		AnalyticsReducer<State, Action> { state, action in
			switch action {
			case .view(.binding(\.$selectedTab)):
				return Analytics.App.TabSwitched(tab: String(describing: state.selectedTab))
			default:
				return nil
			}
		}
	}
}
