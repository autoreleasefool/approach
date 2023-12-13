import AvatarEditorFeature
import AvatarServiceInterface
import ComposableArchitecture
import EquatableLibrary
import ExtensionsLibrary
import FeatureActionLibrary
import FormFeature
import ModelsLibrary
import ModelsViewsLibrary
import ResourcePickerLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct GearEditorView: View {
	let store: StoreOf<GearEditor>

	struct ViewState: Equatable {
		@BindingViewState var name: String
		@BindingViewState var kind: Gear.Kind
		let owner: Bowler.Summary?
		let avatar: Avatar.Summary
		let isEditing: Bool
	}

	public init(store: StoreOf<GearEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			FormView(store: store.scope(state: \.form, action: \.internal.form)) {
				Section(Strings.Editor.Fields.Details.title) {
					TextField(
						Strings.Editor.Fields.Details.name,
						text: viewStore.$name
					)
					.textContentType(.name)

					Picker(
						Strings.Gear.Properties.kind,
						selection: viewStore.$kind
					) {
						ForEach(Gear.Kind.allCases) {
							Text(String(describing: $0)).tag($0)
						}
					}
					.disabled(viewStore.isEditing)
				}

				Section {
					Button { viewStore.send(.didTapAvatar) } label: {
						HStack {
							AvatarView(viewStore.avatar, size: .standardIcon)
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
					Button { viewStore.send(.didTapOwner) } label: {
						LabeledContent(
							Strings.Bowler.title,
							value: viewStore.owner?.name ?? Strings.none
						)
					}
					.buttonStyle(.navigation)
				}
			}
			.onAppear { viewStore.send(.onAppear) }
		})
		.bowlerPicker(store.scope(state: \.$destination.bowlerPicker, action: \.internal.destination.bowlerPicker))
		.avatar(store.scope(state: \.$destination.avatar, action: \.internal.destination.avatar))
	}
}

@MainActor extension View {
	fileprivate func bowlerPicker(
		_ store: PresentationStoreOf<ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>>
	) -> some View {
		navigationDestination(store: store) {
			ResourcePickerView(store: $0) { bowler in
				Bowler.View(bowler)
			}
		}
	}

	fileprivate func avatar(_ store: PresentationStoreOf<AvatarEditor>) -> some View {
		navigationDestination(store: store) {
			AvatarEditorView(store: $0)
		}
	}
}

extension GearEditorView.ViewState {
	init(store: BindingViewStore<GearEditor.State>) {
		self._name = store.$name
		self._kind = store.$kind
		self.owner = store.owner
		self.avatar = store.avatar
		switch store._form.value {
		case .create: self.isEditing = false
		case .edit: self.isEditing = true
		}
	}
}

extension Gear.Kind: CustomStringConvertible {
	public var description: String {
		switch self {
		case .shoes: return Strings.Gear.Properties.Kind.shoes
		case .bowlingBall: return Strings.Gear.Properties.Kind.bowlingBall
		case .towel: return Strings.Gear.Properties.Kind.towel
		case .other: return Strings.other
		}
	}
}

#if DEBUG
struct GearEditorViewPreviews: PreviewProvider {
	static var previews: some View {
		NavigationStack {
			GearEditorView(store:
				.init(
					initialState: .init(value: .create(.default(
						withId: UUID(),
						avatar: .init(id: UUID(), value: .text("", .default))
					))),
					reducer: GearEditor.init
				)
			)
		}
	}
}
#endif
