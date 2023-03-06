import BaseFormLibrary
import ComposableArchitecture
import FeatureActionLibrary
import StringsLibrary
import SwiftUI

public struct OpponentEditorView: View {
	let store: StoreOf<OpponentEditor>

	struct ViewState: Equatable {
		@BindableState var name: String

		init(state: OpponentEditor.State) {
			self.name = state.base.form.name
		}
	}

	enum ViewAction: BindableAction {
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<OpponentEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: OpponentEditor.Action.init) { viewStore in
			BaseFormView(store: store.scope(state: \.base, action: /OpponentEditor.Action.InternalAction.form)) {
				Section(Strings.Editor.Fields.Details.title) {
					TextField(
						Strings.Editor.Fields.Details.name,
						text: viewStore.binding(\.$name)
					)
					.textContentType(.name)
				}
				.listRowBackground(Color(uiColor: .secondarySystemBackground))
			}
		}
	}
}

extension OpponentEditor.State {
	var view: OpponentEditorView.ViewState {
		get { .init(state: self) }
		set {
			self.base.form.name = newValue.name
		}
	}
}

extension OpponentEditor.Action {
	init(action: OpponentEditorView.ViewAction) {
		switch action {
		case let .binding(action):
			self = .binding(action.pullback(\OpponentEditor.State.view))
		}
	}
}

#if DEBUG
struct OpponentEditorViewPreviews: PreviewProvider {
	static var previews: some View {
		NavigationView {
			OpponentEditorView(store:
				.init(
					initialState: .init(mode: .edit(.init(id: UUID(), name: "Joseph"))),
					reducer: OpponentEditor()
				)
			)
		}
	}
}
#endif
