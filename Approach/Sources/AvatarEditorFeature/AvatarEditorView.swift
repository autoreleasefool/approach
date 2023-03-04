import ComposableArchitecture
import SharedModelsLibrary
import SharedModelsViewsLibrary
import SwiftUI

public struct AvatarEditorView: View {
	let store: StoreOf<AvatarEditor>

	struct ViewState: Equatable {
		let avatar: Avatar

		init(state: AvatarEditor.State) {
			self.avatar = state.avatar
		}
	}

	enum ViewAction {
		case didTap
	}

	public init(store: StoreOf<AvatarEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: AvatarEditor.Action.init) { viewStore in
			AvatarView(viewStore.avatar, size: .large)
		}
	}
}

extension AvatarEditor.Action {
	init(action: AvatarEditorView.ViewAction) {
		switch action {
		case .didTap:
			self = .view(.didTap)
		}
	}
}

#if DEBUG
struct AvatarEditorViewPreviews: PreviewProvider {
	static var previews: some View {
		AvatarView(.text("J", .random()), size: .large)
	}
}
#endif
