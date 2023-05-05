import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
import ModelsLibrary
import StringsLibrary

public struct RollEditor: Reducer {
	public struct State: Equatable {
		public var ballRolled: Gear.Summary?
		public var didFoul: Bool


		init(ballRolled: Gear.Summary?, didFoul: Bool) {
			self.ballRolled = ballRolled
			self.didFoul = didFoul
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didToggleFoul
		}
		public enum InternalAction: Equatable {}
		public enum DelegateAction: Equatable {
			case didTapBall
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init() {}

	public var body: some Reducer<State, Action> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didToggleFoul:
					state.didFoul.toggle()
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .never:
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}
