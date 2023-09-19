import AvatarEditorFeature
import AvatarServiceInterface
import ComposableArchitecture
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
		let isEditing: Bool

		let isAvatarsEnabled: Bool
		let avatar: Avatar.Summary?
	}

	public init(store: StoreOf<GearEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			FormView(store: store.scope(state: \.form, action: /GearEditor.Action.InternalAction.form)) {
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

				if viewStore.isAvatarsEnabled {
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
		})
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /GearEditor.Destination.State.bowlerPicker,
			action: GearEditor.Destination.Action.bowlerPicker
		) {
			ResourcePickerView(store: $0) { bowler in
				Bowler.View(bowler)
			}
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /GearEditor.Destination.State.avatar,
			action: GearEditor.Destination.Action.avatar
		) {
			AvatarEditorView(store: $0)
		}
	}
}

extension GearEditorView.ViewState {
	init(store: BindingViewStore<GearEditor.State>) {
		self._name = store.$name
		self._kind = store.$kind
		self.owner = store.owner
		self.isAvatarsEnabled = store.isAvatarsEnabled
		self.avatar = isAvatarsEnabled ? store.avatar : nil
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
				) {
					$0.featureFlags.isEnabled = { _ in true }
				}
			)
		}
	}
}
#endif
