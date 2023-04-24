import ComposableArchitecture
import ModelsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

struct FrameEditorView: View {
	let store: StoreOf<FrameEditor>

	struct ViewState: Equatable {
		let rollIndex: Int
		let roll: Frame.Roll
		let renderWidth: CGFloat

		init(state: FrameEditor.State) {
			self.rollIndex = state.currentRollIndex
			self.roll = state.frame.rolls[state.currentRollIndex]
			self.renderWidth = state.renderWidth
		}
	}

	enum ViewAction {
		case didMeasureViewWidth(CGFloat)
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
				Spacer(minLength: .smallSpacing)
				ForEach(Pin.fullDeck) { pin in
					ZStack {
						Image(uiImage: .pin)
							.resizable()
							.aspectRatio(contentMode: .fit)
							.shadow(color: .black, radius: 2)

						if viewStore.roll.isPinDown(pin) {
							Image(uiImage: .pin)
								.resizable()
								.renderingMode(.template)
								.aspectRatio(contentMode: .fit)
								.tint(.black)
								.opacity(0.5)
						}
					}
					.frame(width: getWidth(for: pin, renderWidth: viewStore.renderWidth))
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
				Spacer(minLength: .smallSpacing)
			}
			.overlay(
				GeometryReader { proxy in
					Color.clear
						.onAppear { viewStore.send(.didMeasureViewWidth(proxy.size.width)) }
				}
			)
		}
	}

	private func getWidth(for pin: Pin, renderWidth: CGFloat) -> CGFloat {
		let spacing = CGFloat.smallSpacing * 6 // 2 sides, 4 spaces between pins
		guard renderWidth > spacing else { return 0 }
		let availableWidth = renderWidth - spacing
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
		case let .didMeasureViewWidth(width):
			self = .view(.didMeasureViewWidth(width))
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
