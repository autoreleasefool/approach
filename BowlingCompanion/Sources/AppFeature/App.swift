import BowlersListFeature
import ComposableArchitecture
import SettingsFeature

public struct App: ReducerProtocol {
	public struct State: Equatable {
		public var selectedTab: Tab = .bowlers
		public var bowlersList = BowlersList.State()
		public var settings = Settings.State()

		public init() {}
	}

	public enum Action: Equatable {
		case selectedTab(Tab)
		case bowlersList(BowlersList.Action)
		case settings(Settings.Action)
	}

	public enum Tab: String, Identifiable, CaseIterable {
		case bowlers
		case settings

		public var id: String { rawValue }
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.bowlersList, action: /Action.bowlersList) {
			BowlersList()
		}
		Scope(state: \.settings, action: /Action.settings) {
			Settings()
		}

		Reduce { state, action in
			switch action {
			case let .selectedTab(tab):
				state.selectedTab = tab
				return .none

			case .bowlersList, .settings:
				return .none
			}
		}
	}
}
