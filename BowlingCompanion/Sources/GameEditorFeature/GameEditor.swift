import ComposableArchitecture
import SharedModelsLibrary

public struct GameEditor: ReducerProtocol {
	public struct State: Equatable {
		public var game: Game

		public init(game: Game) {
			self.game = game
		}
	}

	public enum Action: Equatable {
		case subcribeToFrames
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .subcribeToFrames:
				return .none
			}
		}
	}
}
