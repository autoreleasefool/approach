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

	struct ViewState: Equatable {
		let isGamePickerPresented: Bool
		let isGameDetailsPresented: Bool

		init(state: GamesEditor.State) {
			self.isGameDetailsPresented = state.sheet == .presenting(.gameDetails)
			self.isGamePickerPresented = state.sheet == .presenting(.gamePicker)
		}
	}

	enum ViewAction {
		case setGamePicker(isPresented: Bool)
		case setGameDetails(isPresented: Bool)
		case didDismissGameDetails
		case didDismissGamePicker
	}

	public init(store: StoreOf<GamesEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: GamesEditor.Action.init) { viewStore in
			VStack {
				GameIndicatorView(store: store.scope(state: \.gameIndicator, action: /GamesEditor.Action.InternalAction.gameIndicator))
				Spacer()
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
					BallDetailsView(store: store.scope(state: \.ballDetails, action: /GamesEditor.Action.InternalAction.ballDetails))
						.overlay {
							GeometryReader { geometryProxy in
								Color.clear
									.preference(
										key: HeightPreferenceKey.self,
										value: geometryProxy.size.height + safeAreaInsets.bottom
									)
							}
						}
				}
				.padding(.vertical, .largeSpacing)
				.padding(.horizontal, .standardSpacing)
				.onPreferenceChange(HeightPreferenceKey.self) { newHeight in
					sheetHeight = newHeight
				}
				.presentationDetents(undimmed: [.height(sheetHeight), .medium, .large])
				.presentationDragIndicator(.hidden)
				.interactiveDismissDisabled(true)
				.edgesIgnoringSafeArea(.bottom)
			})
		}
	}
}

extension GamesEditor.Action {
	init(action: GamesEditorView.ViewAction) {
		switch action {
		case let .setGameDetails(isPresented):
			self = .view(.setGameDetails(isPresented: isPresented))
		case let .setGamePicker(isPresented):
			self = .view(.setGamePicker(isPresented: isPresented))
		case .didDismissGamePicker:
			self = .view(.didDismissOpenSheet)
		case .didDismissGameDetails:
			self = .view(.didDismissOpenSheet)
		}
	}
}

private struct HeightPreferenceKey: PreferenceKey {
	static var defaultValue: CGFloat = .zero
	static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
		value = nextValue()
	}
}
