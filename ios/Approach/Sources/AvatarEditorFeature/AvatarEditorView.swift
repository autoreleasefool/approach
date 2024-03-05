import AssetsLibrary
import AvatarServiceInterface
import ComposableArchitecture
import ModelsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

@ViewAction(for: AvatarEditor.self)
public struct AvatarEditorView: View {
	@Perception.Bindable public var store: StoreOf<AvatarEditor>

	public init(store: StoreOf<AvatarEditor>) {
		self.store = store
	}

	public var body: some View {
		WithPerceptionTracking {
			List {
				Section {
					VStack {
						AvatarView(store.avatar, size: .extraLargeIcon)
							.shadow(radius: .standardShadowRadius)
					}
					.frame(maxWidth: .infinity)
				}
				.listRowBackground(Color.clear)

				Section(Strings.Avatar.Editor.Properties.Label.title) {
					TextField(Strings.Avatar.Editor.Properties.Label.title, text: $store.label)
				}

				Section(Strings.Avatar.Editor.Properties.BackgroundColor.title) {
					Picker(
						Strings.Avatar.Editor.Properties.BackgroundColor.Style.title,
						selection: $store.backgroundStyle
					) {
						ForEach(AvatarEditor.AvatarBackgroundStyle.allCases, id: \.rawValue) {
							Text(String(describing: $0)).tag($0)
						}
					}
					.pickerStyle(.segmented)

					colorPicker

					Button { send(.didTapRandomColorButton) } label: {
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
					Button(Strings.Action.cancel) { send(.didTapCancel) }
				}

				ToolbarItem(placement: .navigationBarTrailing) {
					Button(Strings.Action.done) { send(.didTapDone) }
						.disabled(!store.hasChanges)
				}
			}
			.onAppear { send(.onAppear) }
		}
	}

	@ViewBuilder private var colorPicker: some View {
		HStack(spacing: .standardSpacing) {
			ColorPicker(
				Strings.Avatar.Editor.Properties.BackgroundColor.backgroundColor,
				selection: $store.backgroundColor
			)
			.labelsHidden()

			switch store.backgroundStyle {
			case .solid:
				Spacer()
					.frame(height: .largeSpacing)
					.background(store.backgroundColor)
					.clipShape(RoundedRectangle(cornerRadius: .standardRadius))
			case .gradient:
				Spacer()
					.frame(height: .largeSpacing)
					.background(LinearGradient(
						colors: [
							store.backgroundColor,
							store.secondaryBackgroundColor,
						],
						startPoint: .leading,
						endPoint: .trailing
					))
					.clipShape(RoundedRectangle(cornerRadius: .standardRadius))

				ColorPicker(
					Strings.Avatar.Editor.Properties.BackgroundColor.secondaryColor,
					selection: $store.secondaryBackgroundColor
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
				$0[AvatarService.self].render = { @Sendable _ in Asset.Media.Charts.error.image }
			})
		}
	}
}
#endif
