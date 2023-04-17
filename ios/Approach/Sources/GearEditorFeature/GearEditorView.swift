import AvatarServiceInterface
import ComposableArchitecture
import FeatureActionLibrary
import FormLibrary
import ModelsLibrary
import ResourcePickerLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct GearEditorView: View {
	let store: StoreOf<GearEditor>

	struct ViewState: Equatable {
		@BindingState var name: String
		@BindingState var kind: Gear.Kind
		let owner: Bowler.Summary?
		let isBowlerPickerPresented: Bool
		let hasAvatarsEnabled: Bool
		let isEditing: Bool

		init(state: GearEditor.State) {
			self.name = state.name
			self.kind = state.kind
			self.owner = state.owner
			self.isBowlerPickerPresented = state.isBowlerPickerPresented
			self.hasAvatarsEnabled = state.hasAvatarsEnabled
			switch state._form.value {
			case .create: self.isEditing = false
			case .edit: self.isEditing = true
			}
		}
	}

	enum ViewAction: BindableAction {
		case setBowlerPicker(isPresented: Bool)
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<GearEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: GearEditor.Action.init) { viewStore in
			FormView(store: store.scope(state: \.form, action: /GearEditor.Action.InternalAction.form)) {
				Section(Strings.Editor.Fields.Details.title) {
					TextField(
						Strings.Editor.Fields.Details.name,
						text: viewStore.binding(\.$name)
					)
					.textContentType(.name)

					Picker(
						Strings.Gear.Properties.kind,
						selection: viewStore.binding(\.$kind)
					) {
						ForEach(Gear.Kind.allCases) {
							Text(String(describing: $0)).tag($0)
						}
					}
					.disabled(viewStore.isEditing)
				}
				.listRowBackground(Color(uiColor: .secondarySystemBackground))

				Section(Strings.Gear.Properties.owner) {
					NavigationLink(
						destination: ResourcePickerView(
							store: store.scope(
								state: \.bowlerPicker,
								action: /GearEditor.Action.InternalAction.bowlerPicker
							)
						) { bowler in
							Text(bowler.name)
//							if viewStore.hasAvatarsEnabled {
//								AvatarLabelView(bowler.avatar, size: .standardIcon, title: bowler.name)
//							} else {
//								Text(bowler.name)
//							}
						},
						isActive: viewStore.binding(
							get: \.isBowlerPickerPresented,
							send: ViewAction.setBowlerPicker(isPresented:)
						)
					) {
						LabeledContent(
							Strings.Bowler.title,
							value: viewStore.owner?.name ?? Strings.none
						)
					}
				}
				.listRowBackground(Color(uiColor: .secondarySystemBackground))
			}
			.interactiveDismissDisabled(viewStore.isBowlerPickerPresented)
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

extension GearEditor.State {
	var view: GearEditorView.ViewState {
		get { .init(state: self) }
		set {
			self.name = newValue.name
			self.kind = newValue.kind
		}
	}
}

extension GearEditor.Action {
	init(action: GearEditorView.ViewAction) {
		switch action {
		case let .setBowlerPicker(isPresented):
			self = .view(.setBowlerPicker(isPresented: isPresented))
		case let .binding(action):
			self = .binding(action.pullback(\GearEditor.State.view))
		}
	}
}

#if DEBUG
struct GearEditorViewPreviews: PreviewProvider {
	static var previews: some View {
		NavigationView {
			GearEditorView(store:
				.init(
					initialState: .init(value: .edit(.init(id: UUID(), name: "Yellow", owner: .init(id: UUID(), name: "Joseph")))),
					reducer: GearEditor()
				)
			)
		}
	}
}
#endif
