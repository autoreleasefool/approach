import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import FramesRepositoryInterface
import ModelsLibrary
import PreferenceServiceInterface
import SwiftUI
import SwiftUIExtensionsPackageLibrary

@Reducer
public struct FrameEditorNext: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		@Shared(.frames) public var frames: [Frame.Edit]?
		@Shared(.currentFrame) public var currentFrame: Frame.Selection
		@Shared(.isEditable) public var isEditable: Bool

		public var downedPins: Set<Pin> = []
		public var lockedPins: Set<Pin> = []

		public var isDragging = false
		public var downedPinsBeforeDrag: Set<Pin>?
		public var nextPinState: NextPinState?

		public var isFrameDragHintVisible = false

		fileprivate mutating func updateFrameDownedPins() {
			let (frameIndex, rollIndex) = currentFrame.indices
			$frames.withLock {
				$0?[frameIndex].setDownedPins(rollIndex: rollIndex, to: downedPins)
			}
		}
	}

	public enum NextPinState {
		case downed
		case up
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case task
			case onAppear
			case didStartDragging
			case didDragOverPin(Pin)
			case didStopDragging
			case didTapPin(Pin)
			case didDismissFrameDragHint
		}
		@CasePathable
		public enum Delegate {
			case didProvokeLock
		}
		@CasePathable
		public enum Internal {
			case framesDidChange([Frame.Edit]?)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	@Dependency(\.preferences) var preferences

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					state.isFrameDragHintVisible = !(preferences.bool(forKey: .gameDidDismissDragHint) ?? false)
					return .none

				case .task:
					return .publisher {
						state.$frames.publisher
							.map { .internal(.framesDidChange($0)) }
					}

				case let .didTapPin(pin):
					guard !state.lockedPins.contains(pin) else { return .none }
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					state.downedPins.toggle(pin)
					state.updateFrameDownedPins()
					return .none

				case .didStartDragging:
					state.downedPinsBeforeDrag = state.downedPins
					return .none

				case let .didDragOverPin(pin):
					guard !state.lockedPins.contains(pin) else { return .none }

					guard state.isEditable else {
						if state.isDragging {
							return .none
						} else {
							state.isDragging = true
							return .send(.delegate(.didProvokeLock))
						}
					}

					state.isDragging = true
					if let nextPinState = state.nextPinState {
						state.downedPins.toggle(pin, toContain: nextPinState == .downed)
					} else {
						let newState: NextPinState = state.downedPins.contains(pin) ? .up : .downed
						state.nextPinState = newState
						state.downedPins.toggle(pin, toContain: newState == .downed)
					}

					return .none

				case .didStopDragging:
					state.isDragging = false
					state.nextPinState = nil
					if state.downedPins != state.downedPinsBeforeDrag {
						state.downedPinsBeforeDrag = nil
						state.updateFrameDownedPins()
					} else {
						state.downedPinsBeforeDrag = nil
					}
					return .none

				case .didDismissFrameDragHint:
					state.isFrameDragHintVisible = false
					return .run { _ in preferences.setBool(forKey: .gameDidDismissDragHint, to: true) }
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .framesDidChange(frames):
					let (frameIndex, rollIndex) = state.currentFrame.indices
					guard let frame = frames?[frameIndex] else { return .none }
					let pinsDownLastRoll = rollIndex > 0 ? frame.deck(forRoll: rollIndex - 1) : []
					if Frame.isLast(frame.index) {
						state.lockedPins = pinsDownLastRoll.arePinsCleared ? [] : pinsDownLastRoll
					} else {
						state.lockedPins = pinsDownLastRoll
					}
					state.downedPins = frame.deck(forRoll: rollIndex).subtracting(state.lockedPins)
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

// MARK: - View

@ViewAction(for: FrameEditorNext.self)
public struct FrameEditorNextView: View {
	public let store: StoreOf<FrameEditorNext>

	struct PinContainer: Equatable {
		let pin: Pin
		let bounds: CGRect
	}

	struct PinContainerPreferenceKey: SwiftUI.PreferenceKey {
		static let defaultValue: [PinContainer] = []
		static func reduce(value: inout [FrameEditorNextView.PinContainer], nextValue: () -> [FrameEditorNextView.PinContainer]) {
			value.append(contentsOf: nextValue())
		}
	}

	@State private var pinContainers: [PinContainer] = []
	@State private var touchablePins: [PinContainer] = []

	public var body: some View {
		VStack {
			Spacer()

			GeometryReader { parentProxy in
				HStack(alignment: .center, spacing: .smallSpacing) {
					Spacer(minLength: .standardSpacing)
					ForEach(Pin.allCases) { pin in
						ZStack {
							(store.downedPins.contains(pin) ? Asset.Media.Frame.pinDown.swiftUIImage : Asset.Media.Frame.pin.swiftUIImage)
								.resizable()
								.aspectRatio(contentMode: .fit)
								.shadow(color: .black, radius: 2)
						}
						.frame(width: getWidth(for: pin, parentWidth: parentProxy.size.width))
						.background(
							GeometryReader { proxy in
								Color.clear
									.preference(
										key: PinContainerPreferenceKey.self,
										value: [PinContainer(pin: pin, bounds: proxy.frame(in: .named("FrameEditor")))]
									)
							}
						)
						.opacity(store.lockedPins.contains(pin) ? 0.25 : 1)
						.onTapGesture { send(.didTapPin(pin)) }
					}
					Spacer(minLength: .standardSpacing)
				}
				.frame(maxHeight: .infinity)
			}
			.onPreferenceChange(PinContainerPreferenceKey.self) { [$pinContainers] in $pinContainers.wrappedValue = $0 }
			.simultaneousGesture(
				DragGesture()
					.onChanged { drag in
						if !store.isDragging {
							touchablePins = pinContainers
							send(.didStartDragging)
						}

						if let index = touchablePins.firstIndex(where: { $0.bounds.contains(drag.location) }) {
							send(.didDragOverPin(touchablePins[index].pin))
							touchablePins.remove(at: index)
						}
					}
					.onEnded { _ in send(.didStopDragging) }
			)
			.coordinateSpace(name: "FrameEditor")

			Spacer()
		}
		.overlay(alignment: .topLeading) {
			if store.isFrameDragHintVisible {
				FrameDragHint {
					send(.didDismissFrameDragHint, animation: .default)
				}
			}
		}
		.task { await send(.task).finish() }
	}

	private func getWidth(for pin: Pin, parentWidth: CGFloat) -> CGFloat {
		let spacing = CGFloat.unitSpacing * 16 // 2x4 sides, 4x2 spaces between pins
		guard parentWidth > spacing else { return 0 }
		let availableWidth = parentWidth - spacing
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
