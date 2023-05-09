import ComposableArchitecture
import FramesRepositoryInterface
import ModelsLibrary
import SwiftUI

public struct FrameEditor: Reducer {
	public struct State: Equatable {
		public var currentRollIndex: Int
		public var frame: Frame.Edit
		public var draggedPinNewState: Bool?

		public init(currentRollIndex: Int, frame: Frame.Edit) {
			self.currentRollIndex = currentRollIndex
			self.frame = frame
		}
	}

	public enum Action: Equatable {
		public enum ViewAction: Equatable {
			case didDragOverPin(Pin)
			case didStopDraggingPins
		}
		public enum DelegateAction: Equatable {
			case didEditFrame
		}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	init() {}

	public var body: some Reducer<State, Action> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didDragOverPin(pin):
					if let newState = state.draggedPinNewState {
						state.frame.toggle(pin, rollIndex: state.currentRollIndex, newValue: newState)
					} else {
						let oldState = state.frame.roll(at: state.currentRollIndex).isPinDown(pin)
						let newState = !oldState
						state.draggedPinNewState = newState
						state.frame.toggle(pin, rollIndex: state.currentRollIndex, newValue: newState)
					}
					return .task { .delegate(.didEditFrame) }

				case .didStopDraggingPins:
					state.draggedPinNewState = nil
					return .task { .delegate(.didEditFrame) }
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
