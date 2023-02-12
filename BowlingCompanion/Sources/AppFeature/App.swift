import AlleysListFeature
import BowlersListFeature
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import GearListFeature
import SettingsFeature

public struct App: ReducerProtocol {
	public struct State: Equatable {
		public var tabs: [Tab] = []
		public var selectedTab: Tab = .bowlers
		public var bowlersList = BowlersList.State()
		public var alleysList = AlleysList.State()
		public var gearList = GearList.State()
		public var settings: Settings.State

		public init(hasDeveloperFeature: Bool, hasOpponentsFeature: Bool) {
			self.settings = .init(hasDeveloperFeature: hasDeveloperFeature, hasOpponentsEnabled: hasOpponentsFeature)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didAppear
			case didSelectTab(Tab)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case didChangeTabs([Tab])
			case alleysList(AlleysList.Action)
			case gearList(GearList.Action)
			case bowlersList(BowlersList.Action)
			case settings(Settings.Action)
		}
		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public enum Tab: String, Identifiable, CaseIterable {
		case bowlers
		case alleys
		case gear
		case settings

		public var id: String { rawValue }
		public var featureFlag: FeatureFlag {
			switch self {
			case .alleys: return .alleys
			case .bowlers: return .scoreSheetTab
			case .gear: return .gear
			case .settings: return .settingsTab
			}
		}
	}

	public init() {}

	@Dependency(\.featureFlags) var featureFlags

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.bowlersList, action: /Action.internal..Action.InternalAction.bowlersList) {
			BowlersList()
		}
		Scope(state: \.alleysList, action: /Action.internal..Action.InternalAction.alleysList) {
			AlleysList()
		}
		Scope(state: \.gearList, action: /Action.internal..Action.InternalAction.gearList) {
			GearList()
		}
		Scope(state: \.settings, action: /Action.internal..Action.InternalAction.settings) {
			Settings()
		}

		Reduce { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didAppear:
					return .run { send in
						let expectedFlags = App.Tab.allCases.map { $0.featureFlag }
						for await flags in featureFlags.observeAll(expectedFlags) {
							await send(.internal(.didChangeTabs(zip(App.Tab.allCases, flags).filter(\.1).map(\.0))))
						}
					}

				case let .didSelectTab(tab):
					state.selectedTab = tab
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didChangeTabs(tabs):
					state.tabs = tabs
					return .none

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

				case let .alleysList(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .gearList(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .bowlersList(.view), .bowlersList(.internal):
					return .none

				case .settings(.view), .settings(.internal):
					return .none

				case .alleysList(.view), .alleysList(.internal):
					return .none

				case .gearList(.view), .gearList(.internal):
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}
