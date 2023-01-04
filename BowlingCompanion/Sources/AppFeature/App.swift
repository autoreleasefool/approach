import AlleysListFeature
import BowlersListFeature
import ComposableArchitecture
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import GearListFeature
import SettingsFeature
import TeamsAndBowlersListFeature

public struct App: ReducerProtocol {
	public struct State: Equatable {
		public var tabs: [Tab] = []
		public var selectedTab: Tab = .bowlers
		public var bowlersList = BowlersList.State()
		public var alleysList = AlleysList.State()
		public var gearList = GearList.State()
		public var teamsAndBowlersList = TeamsAndBowlersList.State()
		public var settings: Settings.State
		public let hasTeamsFeature: Bool

		public init(hasDeveloperFeature: Bool, hasTeamsFeature: Bool) {
			self.settings = .init(hasDeveloperFeature: hasDeveloperFeature)
			self.hasTeamsFeature = hasTeamsFeature
		}
	}

	public enum Action: Equatable {
		case subscribeToTabs
		case tabsResponse([Tab])
		case selectedTab(Tab)
		case alleysList(AlleysList.Action)
		case gearList(GearList.Action)
		case bowlersList(BowlersList.Action)
		case teamsAndBowlersList(TeamsAndBowlersList.Action)
		case settings(Settings.Action)
	}

	public enum Tab: String, Identifiable, CaseIterable {
		case bowlers
		case alleys
		case gear
		case settings

		public var id: String { rawValue }
		public var featureFlag: FeatureFlag {
			switch self {
			case .alleys: return .alleyTracking
			case .bowlers: return .scoreSheetTab
			case .gear: return .gearTracking
			case .settings: return .settingsTab
			}
		}
	}

	public init() {}

	@Dependency(\.featureFlags) var featureFlags

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.bowlersList, action: /Action.bowlersList) {
			BowlersList()
		}
		Scope(state: \.alleysList, action: /Action.alleysList) {
			AlleysList()
		}
		Scope(state: \.gearList, action: /Action.gearList) {
			GearList()
		}
		Scope(state: \.settings, action: /Action.settings) {
			Settings()
		}
		Scope(state: \.teamsAndBowlersList, action: /Action.teamsAndBowlersList) {
			TeamsAndBowlersList()
		}

		Reduce { state, action in
			switch action {
			case .subscribeToTabs:
				return .run { send in
					let expectedFlags = App.Tab.allCases.map { $0.featureFlag }
					for await flags in featureFlags.observeAll(expectedFlags) {
						await send(.tabsResponse(zip(App.Tab.allCases, flags).filter(\.1).map(\.0)))
					}
				}

			case let .tabsResponse(tabs):
				state.tabs = tabs
				return .none

			case let .selectedTab(tab):
				state.selectedTab = tab
				return .none

			case .bowlersList, .settings, .alleysList, .gearList, .teamsAndBowlersList:
				return .none
			}
		}
	}
}
