import AssetsLibrary
import ComposableArchitecture
import FramesRepositoryInterface
import ModelsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

@Reducer
public struct FrameEditor: Reducer {
	public struct State: Equatable {
		public var isDragging = false

		public var downedPinsBeforeDrag: Set<Pin>?
		public var downedPins: Set<Pin>
		public var lockedPins: Set<Pin>
		public var nextPinState: NextPinState?
		public var isEditable: Bool

		public init(downedPins: Set<Pin> = [], lockedPins: Set<Pin> = [], isEditable: Bool = true) {
			self.downedPins = downedPins
			self.lockedPins = lockedPins
			self.isEditable = isEditable
		}
	}

	public enum Action {
		@CasePathable public enum ViewAction {
			case didStartDragging
			case didDragOverPin(Pin)
			case didStopDragging
			case didTapPin(Pin)
		}
		@CasePathable public enum DelegateAction {
			case didProvokeLock
			case didEditFrame
		}
		@CasePathable public enum InternalAction { case doNothing }

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public enum NextPinState {
		case downed
		case up
	}

	init() {}

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapPin(pin):
					guard !state.lockedPins.contains(pin) else { return .none }
					guard state.isEditable else { return .send(.delegate(.didProvokeLock)) }
					state.downedPins.toggle(pin)
					return .send(.delegate(.didEditFrame))

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
						return .send(.delegate(.didEditFrame))
					} else {
						state.downedPinsBeforeDrag = nil
						return .none
					}
				}

			case .internal(.doNothing):
				return .none

			case .delegate:
				return .none
			}
		}
	}
}

// MARK: - View

public struct FrameEditorView: View {
	let store: StoreOf<FrameEditor>

	struct PinContainer: Equatable {
		let pin: Pin
		let bounds: CGRect
	}

	struct PinContainerPreferenceKey: PreferenceKey {
		static var defaultValue: [PinContainer] = []
		static func reduce(value: inout [FrameEditorView.PinContainer], nextValue: () -> [FrameEditorView.PinContainer]) {
			value.append(contentsOf: nextValue())
		}
	}

	@State private var initialContentSize: CGSize = .zero
	@State private var contentSize: CGSize = .zero

	@State private var pinContainers: [PinContainer] = []
	@State private var touchablePins: [PinContainer] = []

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			HStack(alignment: .center, spacing: .smallSpacing) {
				Spacer(minLength: .standardSpacing)
				ForEach(Pin.allCases) { pin in
					ZStack {
						(viewStore.downedPins.contains(pin) ? Asset.Media.Frame.pinDown.swiftUIImage : Asset.Media.Frame.pin.swiftUIImage)
							.resizable()
							.aspectRatio(contentMode: .fit)
							.shadow(color: .black, radius: 2)
					}
					.frame(width: getWidth(for: pin), height: getHeight(for: pin))
					.background(
						GeometryReader { proxy in
							Color.clear
								.preference(
									key: PinContainerPreferenceKey.self,
									value: [PinContainer(pin: pin, bounds: proxy.frame(in: .named("FrameEditor")))]
								)
						}
					)
					.opacity(viewStore.lockedPins.contains(pin) ? 0.25 : 1)
					.onTapGesture { viewStore.send(.didTapPin(pin)) }
				}
				Spacer(minLength: .standardSpacing)
			}
			.measure(key: InitialContentSizeKey.self, to: $initialContentSize, exactlyOnce: true)
			.measure(key: ContentSizeKey.self, to: $contentSize)
			.onPreferenceChange(PinContainerPreferenceKey.self) { pinContainers = $0 }
			.simultaneousGesture(
				DragGesture()
					.onChanged { drag in
						if !viewStore.isDragging {
							touchablePins = pinContainers
							viewStore.send(.didStartDragging)
						}

						if let index = touchablePins.firstIndex(where: { $0.bounds.contains(drag.location) }) {
							viewStore.send(.didDragOverPin(touchablePins[index].pin))
							touchablePins.remove(at: index)
						}
					}
					.onEnded { _ in viewStore.send(.didStopDragging) }
			)
			.coordinateSpace(name: "FrameEditor")
		})
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

private struct InitialContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct ContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
