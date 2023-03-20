import AvatarServiceInterface
import BaseFormLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ResourcePickerLibrary
import SharedModelsLibrary
import SharedModelsViewsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct GearEditorView: View {
	let store: StoreOf<GearEditor>

	struct ViewState: Equatable {
		@BindingState var name: String
		@BindingState var kind: Gear.Kind
		let selectedBowler: Bowler?
		let isBowlerPickerPresented: Bool
		let hasAvatarsEnabled: Bool

		init(state: GearEditor.State) {
			self.name = state.base.form.name
			self.kind = state.base.form.kind
			self.isBowlerPickerPresented = state.isBowlerPickerPresented
			self.hasAvatarsEnabled = state.hasAvatarsEnabled
			if let id = state.base.form.bowlerPicker.selected.first {
				if let bowler = state.base.form.bowlerPicker.resources?[id: id] {
					self.selectedBowler = bowler
				} else if let bowler = state.initialBowler, bowler.id == id {
					self.selectedBowler = bowler
				} else {
					self.selectedBowler = nil
				}
			} else {
				self.selectedBowler = nil
			}
		}
	}

	enum ViewAction: BindableAction {
		case didAppear
		case setBowlerPicker(isPresented: Bool)
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<GearEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: GearEditor.Action.init) { viewStore in
			BaseFormView(store: store.scope(state: \.base, action: /GearEditor.Action.InternalAction.form)) {
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
				}
				.listRowBackground(Color(uiColor: .secondarySystemBackground))

				Section(Strings.Gear.Properties.owner) {
					NavigationLink(
						destination: ResourcePickerView(
							store: store.scope(
								state: \.base.form.bowlerPicker,
								action: /GearEditor.Action.InternalAction.bowlerPicker
							)
						) { bowler in
							if viewStore.hasAvatarsEnabled {
								AvatarLabelView(bowler.avatar, size: .standardIcon, title: bowler.name)
							} else {
								Text(bowler.name)
							}
						},
						isActive: viewStore.binding(
							get: \.isBowlerPickerPresented,
							send: ViewAction.setBowlerPicker(isPresented:)
						)
					) {
						LabeledContent(
							Strings.Bowler.title,
							value: viewStore.selectedBowler?.name ?? Strings.none
						)
					}
				}
				.listRowBackground(Color(uiColor: .secondarySystemBackground))
			}
			.interactiveDismissDisabled(viewStore.isBowlerPickerPresented)
			.onAppear { viewStore.send(.didAppear) }
		}
	}
}

extension GearEditor.State {
	var view: GearEditorView.ViewState {
		get { .init(state: self) }
		set {
			self.base.form.name = newValue.name
			self.base.form.kind = newValue.kind
		}
	}
}

extension GearEditor.Action {
	init(action: GearEditorView.ViewAction) {
		switch action {
		case .didAppear:
			self = .view(.didAppear)
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
					initialState: .init(mode: .edit(.init(bowler: UUID(), id: UUID(), name: "Joseph", kind: .bowlingBall))),
					reducer: GearEditor()
				)
			)
		}
	}
}
#endif
