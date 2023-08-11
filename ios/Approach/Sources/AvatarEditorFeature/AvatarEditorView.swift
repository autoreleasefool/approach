import AssetsLibrary
import AvatarServiceInterface
import ComposableArchitecture
import ModelsLibrary
import StringsLibrary
import SwiftUI

public struct AvatarEditorView: View {
	let store: StoreOf<AvatarEditor>

	struct ViewState: Equatable {
		let avatar: Avatar.Summary
		let hasChanges: Bool

		init(state: AvatarEditor.State) {
			self.avatar = state.avatar
			self.hasChanges = state.avatar != state.initialAvatar
		}
	}

	public init(store: StoreOf<AvatarEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			VStack {
				AvatarView(viewStore.avatar, size: .extraLargeIcon)
				LazyVGrid(columns: [.init(), .init(), .init()]) {
					Image(systemSymbol: .camera)
						.resizable()
						.aspectRatio(contentMode: .fit)
						.frame(width: .smallIcon, height: .smallIcon)
						.background {
							Circle()
								.fill(.gray)
								.frame(width: .largeIcon, height: .largeIcon)
						}

					Image(systemSymbol: .photoOnRectangle)
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
		})
	}
}
