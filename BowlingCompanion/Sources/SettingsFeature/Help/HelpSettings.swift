import ComposableArchitecture

public struct HelpSettings: ReducerProtocol {
	public struct State: Equatable {
		init() {}
	}

	public enum Action: Equatable {
		case reportBugButtonTapped
		case sendFeedbackButtonTapped
	}

	public var body: some ReducerProtocol<State, Action> {
		Reduce { _, action in
			switch action {
			case .reportBugButtonTapped:
				// TODO: send bug report email
				return .none

			case .sendFeedbackButtonTapped:
				// TODO: send feedback email
				return .none
			}
		}
	}
}
