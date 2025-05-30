import AvatarEditorFeature
import AvatarServiceInterface
import ComposableArchitecture
import EquatablePackageLibrary
import ExtensionsPackageLibrary
import FeatureActionLibrary
import FormFeature
import ModelsLibrary
import ModelsViewsLibrary
import ResourcePickerLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

@ViewAction(for: GearEditor.self)
public struct GearEditorView: View {
	@Bindable public var store: StoreOf<GearEditor>

	public init(store: StoreOf<GearEditor>) {
		self.store = store
	}

	public var body: some View {
		FormView(store: store.scope(state: \.form, action: \.internal.form)) {
			Section(Strings.Editor.Fields.Details.title) {
				TextField(
					Strings.Editor.Fields.Details.name,
					text: $store.name
				)
				.textContentType(.name)

				Picker(
					Strings.Gear.Properties.kind,
					selection: $store.kind
				) {
					ForEach(Gear.Kind.allCases) {
						Text(String(describing: $0)).tag($0)
					}
				}
				.disabled(store.isEditing)
			}

			Section {
				Button { send(.didTapAvatar) } label: {
					HStack {
						AvatarView(store.avatar, size: .standardIcon)
						Text(Strings.Gear.Properties.Avatar.customize)
					}
				}
				.buttonStyle(.navigation)
			} header: {
				Text(Strings.Gear.Properties.Avatar.title)
			} footer: {
				Text(Strings.Gear.Properties.Avatar.description)
			}

			Section(Strings.Gear.Properties.owner) {
				Button { send(.didTapOwner) } label: {
					LabeledContent(
						Strings.Bowler.title,
						value: store.owner?.name ?? Strings.none
					)
				}
				.buttonStyle(.navigation)
			}
		}
		.onAppear { send(.onAppear) }
		.bowlerPicker($store.scope(state: \.destination?.bowlerPicker, action: \.internal.destination.bowlerPicker))
		.avatar($store.scope(state: \.destination?.avatar, action: \.internal.destination.avatar))
	}
}

extension View {
	fileprivate func bowlerPicker(
		_ store: Binding<StoreOf<ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>>?>
	) -> some View {
		navigationDestination(item: store) {
			ResourcePickerView(store: $0) { bowler in
				Bowler.View(bowler)
			}
		}
	}

	fileprivate func avatar(_ store: Binding<StoreOf<AvatarEditor>?>) -> some View {
		navigationDestination(item: store) {
			AvatarEditorView(store: $0)
		}
	}
}

extension Gear.Kind: CustomStringConvertible {
	public var description: String {
		switch self {
		case .shoes: Strings.Gear.Properties.Kind.shoes
		case .bowlingBall: Strings.Gear.Properties.Kind.bowlingBall
		case .towel: Strings.Gear.Properties.Kind.towel
		case .other: Strings.other
		}
	}
}

#if DEBUG
#Preview {
	NavigationStack {
		GearEditorView(
			store: .init(
				initialState: .init(value: .create(.default(
					withId: UUID(),
					avatar: .init(id: UUID(), value: .text("", .default))
				))),
				reducer: GearEditor.init
			)
		)
	}
}
#endif
