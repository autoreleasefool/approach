import AvatarServiceInterface
import ComposableArchitecture
import FeatureActionLibrary
import FormLibrary
import ModelsLibrary
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
		let hasAvatarsEnabled: Bool
		let isEditing: Bool
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
			store: store.scope(state: \.$bowlerPicker, action: { .internal(.bowlerPicker($0)) })
		) {
			ResourcePickerView(store: $0) { bowler in
				Text(bowler.name)
			}
		}
	}
}

extension GearEditorView.ViewState {
	init(store: BindingViewStore<GearEditor.State>) {
		self._name = store.$name
		self._kind = store.$kind
		self.owner = store.owner
		self.hasAvatarsEnabled = store.hasAvatarsEnabled
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
					initialState: .init(value: .create(.default(withId: UUID()))),
					reducer: GearEditor.init
				)
			)
		}
	}
}
#endif
