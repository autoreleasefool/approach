import ComposableArchitecture
import SharedModelsLibrary

public struct FrameEditor: ReducerProtocol {
	public struct State: Equatable {
		public var rollIndex: Int
		public var frame: MutableFrame
		public var draggedPinNewState: Bool?

		public init(rollIndex: Int, frame: MutableFrame, draggedPinNewState: Bool?) {
			self.rollIndex = rollIndex
			self.frame = frame
			self.draggedPinNewState = draggedPinNewState
		}
	}

	public enum Action: Equatable {
		public enum ViewAction: Equatable {
			case didTapNextBallButton
			case didTapPin(Pin)
			case didStartDraggingPin(Pin)
			case didStopDraggingPins
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	init() {}

	public var body: some ReducerProtocol<State, Action> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapNextBallButton:
					return .none

				case let .didTapPin(pin):
					state.frame.toggle(pin, rollIndex: state.rollIndex)
					return .none

				case let .didStartDraggingPin(pin):
					// TODO: dragging is not working
					guard state.draggedPinNewState == nil else { return .none }
					let oldState = state.frame.roll(at: state.rollIndex).isPinDown(pin)
					let newState = !oldState
					state.draggedPinNewState = newState
					state.frame.toggle(pin, rollIndex: state.rollIndex, newValue: newState)
					return .none

				case .didStopDraggingPins:
					state.draggedPinNewState = nil
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
