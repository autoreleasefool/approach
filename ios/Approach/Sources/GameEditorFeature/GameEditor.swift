import ComposableArchitecture
import FeatureActionLibrary
import SharedModelsLibrary

public struct GameEditor: ReducerProtocol {
	public struct State: Equatable {
		public var game: Game

		public init(game: Game) {
			self.game = game
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didAppear
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case ballDetails(BallDetails.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		Scope(state: \.ballDetails, action: /Action.internal..Action.InternalAction.ballDetails) {
			BallDetails()
		}

		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didAppear:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .ballDetails(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .ballDetails(.view), .ballDetails(.internal), .ballDetails(.binding):
					return .none
				}

			case .delegate:
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
