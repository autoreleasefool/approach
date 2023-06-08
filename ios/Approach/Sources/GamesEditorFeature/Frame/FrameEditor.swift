import AssetsLibrary
import ComposableArchitecture
import FramesRepositoryInterface
import ModelsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

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

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didDragOverPin(pin):
					let existingState = state
					if let newState = state.draggedPinNewState {
						state.frame.toggle(pin, rollIndex: state.currentRollIndex, newValue: newState)
					} else {
						let oldState = state.frame.roll(at: state.currentRollIndex).isPinDown(pin)
						let newState = !oldState
						state.draggedPinNewState = newState
						state.frame.toggle(pin, rollIndex: state.currentRollIndex, newValue: newState)
					}

					return existingState != state ? .send(.delegate(.didEditFrame)) : .none

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

// MARK: - View

public struct FrameEditorView: View {
	let store: StoreOf<FrameEditor>

	@GestureState private var dragLocation: CGPoint = .zero
	@State private var initialContentSize: CGSize = .zero
	@State private var contentSize: CGSize = .zero

	struct ViewState: Equatable {
		let rollIndex: Int
		let downPins: Set<Pin>
		let inaccessiblePins: Set<Pin>

		init(state: FrameEditor.State) {
			self.rollIndex = state.currentRollIndex
			self.downPins = state.frame.deck(forRoll: state.currentRollIndex)

			let pinsDownLastFrame = state.currentRollIndex > 0 ? state.frame.deck(forRoll: state.currentRollIndex - 1) : []
			if Frame.isLast(state.frame.index) {
				self.inaccessiblePins = pinsDownLastFrame.isFullDeck ? [] : pinsDownLastFrame
			} else {
				self.inaccessiblePins = pinsDownLastFrame
			}
		}
	}

	enum ViewAction {
		case didDragOverPin(Pin)
		case didStopDraggingPins
	}

	init(store: StoreOf<FrameEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: FrameEditor.Action.init) { viewStore in
			HStack(alignment: .center, spacing: .smallSpacing) {
				Spacer(minLength: .standardSpacing)
				ForEach(Pin.fullDeck) { pin in
					ZStack {
						Image(uiImage: viewStore.downPins.contains(pin) ? .pinDown : .pin)
							.resizable()
							.aspectRatio(contentMode: .fit)
							.shadow(color: .black, radius: 2)
					}
					.frame(width: getWidth(for: pin), height: getHeight(for: pin))
					.background(dragReader(for: pin, withViewStore: viewStore))
					.opacity(viewStore.inaccessiblePins.contains(pin) ? 0.25 : 1)
				}
				Spacer(minLength: .standardSpacing)
			}
			.measure(key: InitialContentSizeKey.self, to: $initialContentSize, exactlyOnce: true)
			.measure(key: ContentSizeKey.self, to: $contentSize)
			.gesture(
				DragGesture(minimumDistance: 0, coordinateSpace: .global)
					.updating($dragLocation) { (value, state, _) in state = value.location }
					.onEnded { _ in viewStore.send(.didStopDraggingPins) }
			)
		}
	}

	private func dragReader(for pin: Pin, withViewStore viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		GeometryReader { proxy in
			if !viewStore.inaccessiblePins.contains(pin) && proxy.frame(in: .global).contains(dragLocation) {
				Task.detached { @MainActor in viewStore.send(.didDragOverPin(pin)) }
			}

			return Color.clear
		}
	}

	private func getHeight(for pin: Pin) -> CGFloat? {
		return nil
	}

	private func getWidth(for pin: Pin) -> CGFloat {
		let spacing = CGFloat.unitSpacing * 16 // 2x4 sides, 4x2 spaces between pins
		guard initialContentSize.width > spacing else { return 0 }
		let availableWidth = initialContentSize.width - spacing
		switch pin {
		case .leftTwoPin, .rightTwoPin:
			return availableWidth * 100 / 530
		case .leftThreePin, .rightThreePin:
			return availableWidth * 108 / 530
		case .headPin:
			return availableWidth * 114 / 530
		}
	}
}

extension FrameEditor.Action {
	init(action: FrameEditorView.ViewAction) {
		switch action {
		case let .didDragOverPin(pin):
			self = .view(.didDragOverPin(pin))
		case .didStopDraggingPins:
			self = .view(.didStopDraggingPins)
		}
	}
}

private struct InitialContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct ContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
