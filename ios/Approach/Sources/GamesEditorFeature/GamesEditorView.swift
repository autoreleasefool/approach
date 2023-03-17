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

	@State private var sheetHeight: CGFloat = .zero
	@State private var frameSize: CGSize = .zero

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
							.overlay(
								GeometryReader { proxy in
									Color.clear
										.preference(
											key: FrameSizePreferenceKey.self,
											value: CGSize(width: proxy.size.width, height: proxy.frame(in: .global).maxY)
										)
								}
							)
					}
					Spacer()
				}
				if viewStore.isShieldVisible {
					Shield()
//						.edgesIgnoringSafeArea(.top)
						.position(x: frameSize.width / 2, y: frameSize.height / 2 + safeAreaInsets.top / 2 + 30)
//							.frame(height: height + safeAreaInsets.top)
						.transition(.move(edge: .top))
						.zIndex(1)
				}
			}
			.onPreferenceChange(FrameSizePreferenceKey.self) { newSize in
				frameSize = newSize
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
//					BallDetailsView(store: store.scope(state: \.ballDetails, action: /GamesEditor.Action.InternalAction.ballDetails))
//						.overlay {
//							GeometryReader { proxy in
//								Color.clear
//									.preference(
//										key: HeightPreferenceKey.self,
//										value: proxy.size.height + safeAreaInsets.bottom
//									)
//							}
//						}
				}
				.padding(.vertical, .largeSpacing)
				.padding(.horizontal, .standardSpacing)
				.onPreferenceChange(HeightPreferenceKey.self) { newHeight in
					sheetHeight = newHeight
					viewStore.send(.didMeasureSheetHeight(newHeight))
				}
				.presentationDetents(
					undimmed: [.height(sheetHeight), .medium, .large],
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

private struct HeightPreferenceKey: PreferenceKey {
	static var defaultValue: CGFloat = .zero
	static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
		value = nextValue()
	}
}

private struct FrameSizePreferenceKey: PreferenceKey {
	static var defaultValue: CGSize = .zero
	static func reduce(value: inout CGSize, nextValue: () -> CGSize) {
		value = nextValue()
	}
}
