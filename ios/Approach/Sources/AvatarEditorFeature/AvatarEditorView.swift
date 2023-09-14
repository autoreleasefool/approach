import AssetsLibrary
import AvatarServiceInterface
import ComposableArchitecture
import ModelsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct AvatarEditorView: View {
	let store: StoreOf<AvatarEditor>

	public init(store: StoreOf<AvatarEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			List {
				Section {
					VStack {
						AvatarView(viewStore.avatar, size: .extraLargeIcon)
							.shadow(radius: .standardShadowRadius)
					}
					.frame(maxWidth: .infinity)
				}
				.listRowBackground(Color.clear)

				Section(Strings.Avatar.Editor.Properties.Label.title) {
					TextField(Strings.Avatar.Editor.Properties.Label.title, text: viewStore.$label)
				}

				Section(Strings.Avatar.Editor.Properties.BackgroundColor.title) {
					ColorPicker(Strings.Avatar.Editor.Properties.BackgroundColor.chooseNewColor, selection: viewStore.$backgroundColor)
					Button(Strings.Avatar.Editor.Properties.BackgroundColor.randomColor) {
						viewStore.send(.didTapRandomColorButton)
					}
				}
			}
			.navigationTitle(Strings.Avatar.Editor.title)
			.navigationBarBackButtonHidden(true)
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

#if DEBUG
struct AvatarEditorViewPreview: PreviewProvider {
	static var previews: some View {
		NavigationStack {
			AvatarEditorView(store: .init(
				initialState: AvatarEditor.State(avatar: .init(id: UUID(), value: .text("Ye", .rgb(0, 0, 0)))),
				reducer: AvatarEditor.init
			) {
				$0.avatars.render = { _ in Asset.Media.Charts.error.image }
			})
		}
	}
}
#endif
