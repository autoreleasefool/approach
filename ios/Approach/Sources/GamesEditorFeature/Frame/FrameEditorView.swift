import AssetsLibrary
import ComposableArchitecture
import ModelsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

struct FrameEditorView: View {
	let store: StoreOf<FrameEditor>

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
		case didTapNextBallButton
		case didTapPin(Pin)
		case didStartDraggingPin(Pin)
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
					.gesture(
						TapGesture()
							.onEnded { viewStore.send(.didTapPin(pin)) }
					)
					.gesture(
						DragGesture()
							.onChanged { _ in viewStore.send(.didStartDraggingPin(pin)) }
							.onEnded { _ in viewStore.send(.didStopDraggingPins) }
					)
				}
				Spacer(minLength: .standardSpacing)
			}
			.measure(key: InitialContentSizeKey.self, to: $initialContentSize, exactlyOnce: true)
			.measure(key: ContentSizeKey.self, to: $contentSize)
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
		case .didTapNextBallButton:
			self = .view(.didTapNextBallButton)
		case let .didTapPin(pin):
			self = .view(.didTapPin(pin))
		case let .didStartDraggingPin(pin):
			self = .view(.didStartDraggingPin(pin))
		case .didStopDraggingPins:
			self = .view(.didStopDraggingPins)
		}
	}
}

private struct InitialContentSizeKey: PreferenceKey {
	static var defaultValue: CGSize = .zero
	static func reduce(value: inout CGSize, nextValue: () -> CGSize) {
		value = nextValue()
	}
}


private struct ContentSizeKey: PreferenceKey {
	static var defaultValue: CGSize = .zero
	static func reduce(value: inout CGSize, nextValue: () -> CGSize) {
		value = nextValue()
	}
}
