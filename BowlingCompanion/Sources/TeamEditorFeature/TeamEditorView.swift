import BaseFormFeature
import ComposableArchitecture
import ResourcePickerFeature
import SharedModelsViewsLibrary
import StringsLibrary
import SwiftUI

public struct TeamEditorView: View {
	let store: StoreOf<TeamEditor>

	struct ViewState: Equatable {
		@BindableState var name: String
		let isBowlerPickerPresented: Bool
		let numberOfBowlersSelected: Int

		init(state: TeamEditor.State) {
			self.name = state.base.form.name
			self.isBowlerPickerPresented = state.isBowlerPickerPresented
			self.numberOfBowlersSelected = state.teamMembers.bowlers.count
		}
	}

	enum ViewAction: BindableAction {
		case setBowlerPickerPresented(isPresented: Bool)
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<TeamEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: TeamEditor.Action.init) { viewStore in
			BaseFormView(store: store.scope(state: \.base, action: TeamEditor.Action.form)) {
				Section(Strings.Editor.Fields.Details.title) {
					TextField(
						Strings.Editor.Fields.Details.name,
						text: viewStore.binding(\.$name)
					)
					.textContentType(.name)
				}
				.listRowBackground(Color(uiColor: .secondarySystemBackground))

				Section {
					TeamMembersView(store: store.scope(state: \.teamMembers, action: TeamEditor.Action.teamMembers))
						.listRowBackground(Color(uiColor: .secondarySystemBackground))
				} header: {
					HStack(alignment: .bottom) {
						Text(Strings.Team.Properties.Bowlers.title)
						Spacer()
						NavigationLink(
							destination: ResourcePickerView(
								store: store.scope(
									state: \.base.form.bowlers,
									action: TeamEditor.Action.bowlers
								)
							) { bowler in
								BowlerRow(bowler: bowler)
							},
							isActive: viewStore.binding(
								get: \.isBowlerPickerPresented,
								send: ViewAction.setBowlerPickerPresented(isPresented:)
							)
						) {
							Text(Strings.Action.manage)
								.font(.caption)
						}
					}
				} footer: {
					if viewStore.numberOfBowlersSelected == 1 {
						Text("You can't create a team with only 1 bowler!")
							.font(.caption)
							.foregroundColor(.appDestructive)
					}
				}
			}
			.interactiveDismissDisabled(viewStore.isBowlerPickerPresented)
		}
	}
}

extension TeamEditor.State {
	var view: TeamEditorView.ViewState {
		get { .init(state: self) }
		set {
			self.base.form.name = newValue.name
		}
	}
}

extension TeamEditor.Action {
	init(action: TeamEditorView.ViewAction) {
		switch action {
		case let .setBowlerPickerPresented(isPresented):
			self = .setBowlerPicker(isPresented: isPresented)
		case let .binding(action):
			self = .binding(action.pullback(\TeamEditor.State.view))
		}
	}
}
