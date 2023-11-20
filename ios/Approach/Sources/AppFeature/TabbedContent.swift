import AccessoriesOverviewFeature
import AnalyticsServiceInterface
import BowlersListFeature
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import SettingsFeature
import StatisticsOverviewFeature

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

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case didAppear
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
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
		BindingReducer(action: /Action.view)

		Scope(state: \.bowlersList, action: /Action.internal..Action.InternalAction.bowlersList) {
			BowlersList()
		}

		Scope(state: \.settings, action: /Action.internal..Action.InternalAction.settings) {
			Settings()
		}

		Scope(state: \.accessories, action: /Action.internal..Action.InternalAction.accessories) {
			AccessoriesOverview()
		}

		Scope(state: \.statistics, action: /Action.internal..Action.InternalAction.statistics) {
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

				case let .statistics(.delegate(delegateAction)):
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

				case .statistics(.view), .statistics(.internal):
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
