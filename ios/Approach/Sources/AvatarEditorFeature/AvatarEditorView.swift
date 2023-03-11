import AvatarServiceInterface
import ComposableArchitecture
import SharedModelsLibrary
import SharedModelsViewsLibrary
import StringsLibrary
import SwiftUI

public struct AvatarEditorView: View {
	let store: StoreOf<AvatarEditor>

	struct ViewState: Equatable {
		let avatar: Avatar
		let hasChanges: Bool

		init(state: AvatarEditor.State) {
			self.avatar = state.avatar
			self.hasChanges = state.avatar != state.initialAvatar
		}
	}

	enum ViewAction {
		case didTapCancel
		case didTapDone
	}

	public init(store: StoreOf<AvatarEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: AvatarEditor.Action.init) { viewStore in
			VStack {
				AvatarView(viewStore.avatar, size: .extraLargeIcon)
				LazyVGrid(columns: [.init(), .init(), .init()]) {
					Image(systemName: "camera")
						.resizable()
						.aspectRatio(contentMode: .fit)
						.frame(width: .smallIcon, height: .smallIcon)
						.background {
							Circle()
								.fill(.gray)
								.frame(width: .largeIcon, height: .largeIcon)
						}

					Image(systemName: "photo.on.rectangle")
						.resizable()
						.frame(width: .standardIcon, height: .standardIcon)
				}
			}
			.toolbar {
				ToolbarItem(placement: .navigationBarLeading) {
					Button(Strings.Action.cancel) { viewStore.send(.didTapCancel) }
				}

				ToolbarItem(placement: .navigationBarTrailing) {
					Button(Strings.Action.done) { viewStore.send(.didTapDone) }
						.disabled(!viewStore.hasChanges)
				}
			}
		}
	}
}

extension AvatarEditor.Action {
	init(action: AvatarEditorView.ViewAction) {
		switch action {
		case .didTapCancel:
			self = .view(.didTapCancel)
		case .didTapDone:
			self = .view(.didTapDone)
		}
	}
}

#if DEBUG
struct AvatarEditorViewPreviews: PreviewProvider {
	static var previews: some View {
		AvatarView(.text("J", .red()), size: .largeIcon)
	}
}
#endif
