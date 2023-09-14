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
					Picker(
						Strings.Avatar.Editor.Properties.BackgroundColor.Style.title,
						selection: viewStore.$backgroundStyle
					) {
						ForEach(AvatarEditor.AvatarBackgroundStyle.allCases, id: \.rawValue) {
							Text(String(describing: $0)).tag($0)
						}
					}
					.pickerStyle(.segmented)

					colorPicker(viewStore)

					Button { viewStore.send(.didTapRandomColorButton) } label: {
						Text(Strings.Avatar.Editor.Properties.BackgroundColor.randomColor)
							.frame(maxWidth: .infinity, alignment: .center)
					}
					.buttonStyle(.borderless)
				}
				.listRowSeparator(.hidden)
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

	@MainActor @ViewBuilder private func colorPicker(
		_ viewStore: ViewStore<AvatarEditor.State, AvatarEditor.Action.ViewAction>
	) -> some View {
		HStack(spacing: .standardSpacing) {
			ColorPicker(
				Strings.Avatar.Editor.Properties.BackgroundColor.backgroundColor,
				selection: viewStore.$backgroundColor
			)
			.labelsHidden()

			switch viewStore.backgroundStyle {
			case .solid:
				Spacer()
					.frame(height: .largeSpacing)
					.background(viewStore.backgroundColor)
					.clipShape(RoundedRectangle(cornerRadius: .standardRadius))
			case .gradient:
				Spacer()
					.frame(height: .largeSpacing)
					.background(LinearGradient(
						colors: [
							viewStore.backgroundColor,
							viewStore.secondaryBackgroundColor,
						],
						startPoint: .leading,
						endPoint: .trailing
					))
					.clipShape(RoundedRectangle(cornerRadius: .standardRadius))

				ColorPicker(
					Strings.Avatar.Editor.Properties.BackgroundColor.secondaryColor,
					selection: viewStore.$secondaryBackgroundColor
				)
				.labelsHidden()
			}
		}
	}
}

#if DEBUG
struct AvatarEditorViewPreview: PreviewProvider {
	static var previews: some View {
		NavigationStack {
			AvatarEditorView(store: .init(
				initialState: AvatarEditor.State(avatar: .init(id: UUID(), value: .text("Ye", .rgb(.default)))),
				reducer: AvatarEditor.init
			) {
				$0.avatars.render = { _ in Asset.Media.Charts.error.image }
			})
		}
	}
}
#endif
