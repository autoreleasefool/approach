import AccessoriesOverviewFeature
import AnalyticsServiceInterface
import BowlersListFeature
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import SettingsFeature

public struct TabbedContent: Reducer {
	public struct State: Equatable {
		public var tabs: [Tab] = []
		public var selectedTab: Tab = .overview
		public var accessories = AccessoriesOverview.State()
		public var bowlersList = BowlersList.State()
		public var settings = Settings.State()

		public init() {}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didAppear
			case didSelectTab(Tab)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case didChangeTabs([Tab])
			case accessories(AccessoriesOverview.Action)
			case bowlersList(BowlersList.Action)
			case settings(Settings.Action)
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
		public var featureFlag: FeatureFlag {
			switch self {
			case .overview: return .overviewTab
			case .statistics: return .statisticsTab
			case .accessories: return .accessoriesTab
			case .settings: return .settingsTab
			}
		}
	}

	public init() {}

	@Dependency(\.analytics) var analytics
	@Dependency(\.featureFlags) var featureFlags

	public var body: some Reducer<State, Action> {
		Scope(state: \.bowlersList, action: /Action.internal..Action.InternalAction.bowlersList) {
			BowlersList()
		}

		Scope(state: \.settings, action: /Action.internal..Action.InternalAction.settings) {
			Settings()
		}

		Scope(state: \.accessories, action: /Action.internal..Action.InternalAction.accessories) {
			AccessoriesOverview()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didAppear:
					return .run { send in
						let expectedFlags = TabbedContent.Tab.allCases.map { $0.featureFlag }
						for await flags in featureFlags.observeAll(expectedFlags) {
							await send(.internal(.didChangeTabs(zip(TabbedContent.Tab.allCases, flags).filter(\.1).map(\.0))))
						}
					}

				case let .didSelectTab(tab):
					state.selectedTab = tab
					return .run { _ in await analytics.trackEvent(Analytics.App.TabSwitched(tab: String(describing: tab))) }
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didChangeTabs(tabs):
					state.tabs = tabs
					return .none

				case let .accessories(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .bowlersList(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .settings(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .accessories(.view), .accessories(.internal):
					return .none

				case .bowlersList(.view), .bowlersList(.internal):
					return .none

				case .settings(.view), .settings(.internal):
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}
