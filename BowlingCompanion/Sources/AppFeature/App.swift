import BowlersListFeature
import ComposableArchitecture

public struct App: ReducerProtocol {
	public struct State: Sendable, Equatable {
		public var bowlersList = BowlersList.State()

		public init() {}
	}

	public enum Action: Equatable, Sendable {
		case onAppear
		case bowlersList(BowlersList.Action)
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.bowlersList, action: /Action.bowlersList) {
			BowlersList()
		}

		Reduce { _, action in
			switch action {
			case .onAppear:
				return .none

			case .bowlersList:
				return .none
			}
		}
	}
}
