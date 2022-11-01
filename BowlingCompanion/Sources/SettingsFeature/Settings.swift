import ComposableArchitecture

public struct Settings: ReducerProtocol {
	public struct State: Equatable {
		public init() {}
	}

	public enum Action: Equatable {
		case placeholder
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .placeholder:
				return .none
			}
		}
	}
}
