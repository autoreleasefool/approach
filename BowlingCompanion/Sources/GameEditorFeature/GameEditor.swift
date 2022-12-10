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
		case refreshData
		case ballDetails(BallDetails.Action)
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.ballDetails, action: /Action.ballDetails) {
			BallDetails()
		}

		Reduce { _, action in
			switch action {
			case .refreshData, .ballDetails:
				return .none
			}
		}
	}
}

extension GameEditor.State {
	var ballDetails: BallDetails.State {
		get {
			.init(
				frame: 1,
				ball: 1
			)
		}
		set {}
	}
}
