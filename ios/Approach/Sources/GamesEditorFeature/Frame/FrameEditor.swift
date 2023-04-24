import ComposableArchitecture
import FramesRepositoryInterface
import ModelsLibrary
import SwiftUI

public struct FrameEditor: Reducer {
	public struct State: Equatable {
		public var currentRollIndex: Int
		public var frame: Frame.Edit
		public var draggedPinNewState: Bool?
		public var renderWidth: CGFloat = .zero

		public init(currentRollIndex: Int, frame: Frame.Edit) {
			self.currentRollIndex = currentRollIndex
			self.frame = frame
		}
	}

	public enum Action: Equatable {
		public enum ViewAction: Equatable {
			case didMeasureViewWidth(CGFloat)
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

	public var body: some Reducer<State, Action> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didMeasureViewWidth(width):
					state.renderWidth = width
					return .none

				case .didTapNextBallButton:
					return .none

				case let .didTapPin(pin):
					state.frame.toggle(pin, rollIndex: state.currentRollIndex)
					return .none

				case let .didStartDraggingPin(pin):
					// TODO: dragging is not working
					guard state.draggedPinNewState == nil else { return .none }
					let oldState = state.frame.roll(at: state.currentRollIndex).isPinDown(pin)
					let newState = !oldState
					state.draggedPinNewState = newState
					state.frame.toggle(pin, rollIndex: state.currentRollIndex, newValue: newState)
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
