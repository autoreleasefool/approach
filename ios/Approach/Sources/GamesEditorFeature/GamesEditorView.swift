import ComposableArchitecture
import FeatureActionLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import AssetsLibrary

public struct GamesEditorView: View {
	let store: StoreOf<GamesEditor>

	@Environment(\.safeAreaInsets) private var safeAreaInsets

	@State private var sheetHeight: CGFloat = .zero

	struct ViewState: Equatable {
		let ordinal: Int
		let currentFrame: Int
		let currentBall: Int

		init(state: GamesEditor.State) {
			self.ordinal = state.game.ordinal
			self.currentFrame = 1
			self.currentBall = 1
		}
	}

	enum ViewAction {
		case didAppear
	}

	public init(store: StoreOf<GamesEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: GamesEditor.Action.init) { viewStore in
			VStack {
				GamePickerView(store: store.scope(state: \.gamePicker, action: /GamesEditor.Action.InternalAction.gamePicker))
				Spacer()
			}
			.sheet(isPresented: .constant(true)) {
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
			}
			.navigationBarBackButtonHidden(true)
		}
	}
}

extension GamesEditor.Action {
	init(action: GamesEditorView.ViewAction) {
		switch action {
		case .didAppear:
			self = .view(.didAppear)
		}
	}
}

private struct HeightPreferenceKey: PreferenceKey {
	static var defaultValue: CGFloat = .zero
	static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
		value = nextValue()
	}
}
