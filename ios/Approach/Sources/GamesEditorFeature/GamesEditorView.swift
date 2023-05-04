import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import ScoreSheetFeature
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary

public struct GamesEditorView: View {
	let store: StoreOf<GamesEditor>

	@Environment(\.safeAreaInsets) private var safeAreaInsets

	@State private var sheetContentHeight: CGFloat = .zero
	@State private var windowContentSize: CGSize = .zero

	struct ViewState: Equatable {
		@BindingState var detent: PresentationDetent = .height(.zero)
		let minimumSheetHeight: CGFloat
//		let sheetContentHeight: CGFloat
		let isGamePickerPresented: Bool
		let isGameDetailsPresented: Bool

		init(state: GamesEditor.State) {
			self.isGameDetailsPresented = state.sheet == .presenting(.gameDetails)
			self.isGamePickerPresented = state.sheet == .presenting(.gamePicker)
			self.detent = state.detent
			self.minimumSheetHeight = state.minimumSheetHeight
//			self.sheetContentHeight = state.sheetContentHeight
		}
	}

	enum ViewAction: BindableAction {
		case didAppear
//		case didMeasureSheetContentHeight(CGFloat)
		case didMeasureScoreSheetHeight(CGFloat)
		case setGamePicker(isPresented: Bool)
		case setGameDetails(isPresented: Bool)

		case didDismissGameDetails
		case didDismissGamePicker
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<GamesEditor>) {
		self.store = store
	}

	public var body: some View {
		print("\(windowContentSize.height), \(sheetContentHeight), \(windowContentSize.height - sheetContentHeight)")
		return WithViewStore(store, observe: ViewState.init, send: GamesEditor.Action.init) { viewStore in
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
			}
			.overlay(
				GeometryReader { proxy in
					Color.clear
						.preference(
							key: WindowContentSizeKey.self,
							value: proxy.size
						)
				}
			)
			.onPreferenceChange(WindowContentSizeKey.self) { newSize in
				windowContentSize = newSize
			}
			.background(alignment: .top) {
				Image(uiImage: .laneBackdrop)
					.resizable(resizingMode: .stretch)
					.fixedSize(horizontal: true, vertical: false)
					.frame(width: windowContentSize.width, height: windowContentSize.height - sheetContentHeight)
			}
			.background(Color.appPinTint)
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
					IfLetStore(
						store.scope(state: \.scoreSheet, action: /GamesEditor.Action.InternalAction.scoreSheet)
					) { scopedStore in
						ScoreSheetView(store: scopedStore)
							.overlay(
								GeometryReader { proxy in
									Color.clear
										.onAppear { viewStore.send(.didMeasureScoreSheetHeight(proxy.size.height + safeAreaInsets.top)) }
								}
							)
					}
				}
				.presentationDetents(
					[.height(viewStore.minimumSheetHeight), .medium, .large],
					selection: viewStore.binding(\.$detent)
				)
				.presentationDragIndicator(.hidden)
				.presentationBackgroundInteraction(.enabled(upThrough: .medium))
				.interactiveDismissDisabled(true)
				.edgesIgnoringSafeArea(.bottom)
				.overlay(
					GeometryReader { proxy in
						Color.clear
							.preference(
								key: SheetContentHeightKey.self,
								value: proxy.size.height
							)
					}
				)
				.onPreferenceChange(SheetContentHeightKey.self) { newHeight in
					sheetContentHeight = newHeight
				}
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
//		case let .didMeasureSheetContentHeight(height):
//			self = .view(.didMeasureSheetContentHeight(height))
		case let .didMeasureScoreSheetHeight(height):
			self = .view(.didMeasureScoreSheetHeight(height))
		case let .setGameDetails(isPresented):
			self = .view(.setGameDetails(isPresented: isPresented))
		case let .setGamePicker(isPresented):
			self = .view(.setGamePicker(isPresented: isPresented))
		case .didDismissGamePicker:
			self = .view(.didDismissOpenSheet)
		case .didDismissGameDetails:
			self = .view(.didDismissOpenSheet)
		case let .binding(action):
			self = .binding(action.pullback(\GamesEditor.State.view))
		}
	}
}

private struct SheetContentHeightKey: PreferenceKey {
	static var defaultValue: CGFloat = .zero
	static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
		value = nextValue()
	}
}

private struct WindowContentSizeKey: PreferenceKey {
	static var defaultValue: CGSize = .zero
	static func reduce(value: inout CGSize, nextValue: () -> CGSize) {
		value = nextValue()
	}
}
