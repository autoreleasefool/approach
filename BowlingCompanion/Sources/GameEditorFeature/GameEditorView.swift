import ComposableArchitecture
import SwiftUI

public struct GameEditorView: View {
	let store: StoreOf<GameEditor>

	struct ViewState: Equatable {
		let ordinal: Int

		init(state: GameEditor.State) {
			self.ordinal = state.game.ordinal
		}
	}

	enum ViewAction {
		case subscribeToFrames
	}

	public init(store: StoreOf<GameEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: GameEditor.Action.init) { viewStore in
			Text("Game \(viewStore.ordinal)")
				.task { await viewStore.send(.subscribeToFrames).finish() }
		}
	}
}

extension GameEditor.Action {
	init(action: GameEditorView.ViewAction) {
		switch action {
		case .subscribeToFrames:
			self = .subcribeToFrames
		}
	}
}
