import ComposableArchitecture
import FeatureActionLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import AssetsLibrary

public struct GameEditorView: View {
	let store: StoreOf<GameEditor>

	@Environment(\.safeAreaInsets) private var safeAreaInsets

	@State private var sheetHeight: CGFloat = .zero

	struct ViewState: Equatable {
		let ordinal: Int
		let currentFrame: Int
		let currentBall: Int

		init(state: GameEditor.State) {
			self.ordinal = state.game.ordinal
			self.currentFrame = 1
			self.currentBall = 1
		}
	}

	enum ViewAction {
		case didAppear
	}

	public init(store: StoreOf<GameEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: GameEditor.Action.init) { viewStore in
			Text(Strings.Game.title(viewStore.ordinal))
				.sheet(isPresented: .constant(true)) {
					ScrollView {
						BallDetailsView(store: store.scope(state: \.ballDetails, action: /GameEditor.Action.InternalAction.ballDetails))
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
				.onAppear { viewStore.send(.didAppear) }
		}
	}
}

extension GameEditor.Action {
	init(action: GameEditorView.ViewAction) {
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
