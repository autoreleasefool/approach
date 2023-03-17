import ComposableArchitecture
import FeatureActionLibrary
import SharedModelsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import AssetsLibrary

public struct GamesEditorView: View {
	let store: StoreOf<GamesEditor>

	@Environment(\.safeAreaInsets) private var safeAreaInsets

	struct ViewState: Equatable {
		@BindableState var detent: PresentationDetent = .height(.zero)
		let isGamePickerPresented: Bool
		let isGameDetailsPresented: Bool
		let isShieldVisible: Bool

		init(state: GamesEditor.State) {
			self.isGameDetailsPresented = state.sheet == .presenting(.gameDetails)
			self.isGamePickerPresented = state.sheet == .presenting(.gamePicker)
			self.detent = state.detent
			self.isShieldVisible = state.isShieldVisible
		}
	}

	enum ViewAction: BindableAction {
		case didAppear
		case didMeasureSheetHeight(CGFloat)
		case setGamePicker(isPresented: Bool)
		case setGameDetails(isPresented: Bool)
		case setShield(isVisible: Bool)
		case didDismissGameDetails
		case didDismissGamePicker
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<GamesEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: GamesEditor.Action.init) { viewStore in
			ZStack {
				VStack {
					GameIndicatorView(
						store: store.scope(state: \.gameIndicator, action: /GamesEditor.Action.InternalAction.gameIndicator)
					)
					IfLetStore(
						store.scope(state: \.frameEditor, action: /GamesEditor.Action.InternalAction.frameEditor)
					) { scopedStore in
						FrameEditorView(store: scopedStore)
							.padding(.top)
					}
					Spacer()
				}

				if viewStore.isShieldVisible {
					Shield()
						.transition(.move(edge: .top))
						.zIndex(1)
				}
			}
			.onChange(of: viewStore.detent) { detent in
				guard viewStore.isGameDetailsPresented else { return }
				switch detent {
				case .medium, .large:
					viewStore.send(.setShield(isVisible: true), animation: .easeInOut)
				default:
					viewStore.send(.setShield(isVisible: false), animation: .easeInOut)
				}
			}
			.onChange(of: viewStore.isGamePickerPresented) { isGamePickerPresented in
				viewStore.send(.setShield(isVisible: isGamePickerPresented), animation: .easeInOut)
			}
			.toolbar(.hidden, for: .tabBar, .navigationBar)
			.sheet(isPresented: viewStore.binding(
				get: \.isGamePickerPresented,
				send: ViewAction.setGamePicker(isPresented:)
			), onDismiss: {
				viewStore.send(.didDismissGamePicker)
			}, content: {
				NavigationView {
					GamePickerView(store: store.scope(state: \.gamePicker, action: /GamesEditor.Action.InternalAction.gamePicker))
				}
				.presentationDetents([.medium])
			})
			.sheet(isPresented: viewStore.binding(
				get: \.isGameDetailsPresented,
				send: ViewAction.setGameDetails(isPresented:)
			), onDismiss: {
				viewStore.send(.didDismissGameDetails)
			}, content: {
				ScrollView {
					EmptyView()
				}
				.padding(.vertical, .largeSpacing)
				.padding(.horizontal, .standardSpacing)
				.presentationDetents(
					undimmed: [.height(50), .medium, .large],
					selection: viewStore.binding(\.$detent)
				)
				.presentationDragIndicator(.hidden)
				.interactiveDismissDisabled(true)
				.edgesIgnoringSafeArea(.bottom)
			})
			.onAppear { viewStore.send(.didAppear) }
		}
	}
}

extension GamesEditor.State {
	var view: GamesEditorView.ViewState {
		get { .init(state: self) }
		set {
			self.detent = newValue.detent
		}
	}
}

extension GamesEditor.Action {
	init(action: GamesEditorView.ViewAction) {
		switch action {
		case .didAppear:
			self = .view(.didAppear)
		case let .didMeasureSheetHeight(height):
			self = .view(.didMeasureSheetHeight(height))
		case let .setGameDetails(isPresented):
			self = .view(.setGameDetails(isPresented: isPresented))
		case let .setGamePicker(isPresented):
			self = .view(.setGamePicker(isPresented: isPresented))
		case .didDismissGamePicker:
			self = .view(.didDismissOpenSheet)
		case .didDismissGameDetails:
			self = .view(.didDismissOpenSheet)
		case let .setShield(isVisible):
			self = .view(.setShield(isVisible: isVisible))
		case let .binding(action):
			self = .binding(action.pullback(\GamesEditor.State.view))
		}
	}
}
