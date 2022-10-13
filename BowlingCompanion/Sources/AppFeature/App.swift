import ComposableArchitecture

public struct App: ReducerProtocol {
	public struct State: Equatable {
		public init() {}
	}

	public enum Action: Equatable, Sendable {
		case onAppear
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .onAppear:
				return .none
			}
		}
	}
}
