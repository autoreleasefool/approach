import AssetsLibrary
import ComposableArchitecture
import ModelsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

struct FrameEditorView: View {
	let store: StoreOf<FrameEditor>

	@GestureState private var dragLocation: CGPoint = .zero
	@State private var initialContentSize: CGSize = .zero
	@State private var contentSize: CGSize = .zero

	struct ViewState: Equatable {
		let rollIndex: Int
		let roll: Frame.Roll

		init(state: FrameEditor.State) {
			self.rollIndex = state.currentRollIndex
			self.roll = state.frame.rolls[state.currentRollIndex].roll
		}
	}

	enum ViewAction {
		case didDragOverPin(Pin)
		case didStopDraggingPins
	}

	init(store: StoreOf<FrameEditor>) {
		self.store = store
	}

	var body: some View {
		WithViewStore(store, observe: ViewState.init, send: FrameEditor.Action.init) { viewStore in
			HStack(alignment: .center, spacing: .smallSpacing) {
				Spacer(minLength: .standardSpacing)
				ForEach(Pin.fullDeck) { pin in
					ZStack {
						Image(uiImage: viewStore.roll.isPinDown(pin) ? .pinDown : .pin)
							.resizable()
							.aspectRatio(contentMode: .fit)
							.shadow(color: .black, radius: 2)
					}
					.frame(width: getWidth(for: pin), height: getHeight(for: pin))
					.background(dragReader(for: pin, withViewStore: viewStore))
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
			if proxy.frame(in: .global).contains(dragLocation) {
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
