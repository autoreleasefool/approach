import BaseFormFeature
import ComposableArchitecture
import StringsLibrary
import SwiftUI

public struct BowlerEditorView: View {
	let store: StoreOf<BowlerEditor>

	struct ViewState: Equatable {
		@BindableState var name: String

		init(state: BowlerEditor.State) {
			self.name = state.base.form.name
		}
	}

	enum ViewAction: BindableAction {
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<BowlerEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: BowlerEditor.Action.init) { viewStore in
			BaseFormView(store: store.scope(state: \.base, action: BowlerEditor.Action.form)) {
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

extension BowlerEditor.State {
	var view: BowlerEditorView.ViewState {
		get { .init(state: self) }
		set {
			self.base.form.name = newValue.name
		}
	}
}

extension BowlerEditor.Action {
	init(action: BowlerEditorView.ViewAction) {
		switch action {
		case let .binding(action):
			self = .binding(action.pullback(\BowlerEditor.State.view))
		}
	}
}

#if DEBUG
struct BowlerEditorViewPreviews: PreviewProvider {
	static var previews: some View {
		NavigationView {
			BowlerEditorView(store:
				.init(
					initialState: .init(mode: .edit(.init(id: UUID(), name: "Joseph"))),
					reducer: BowlerEditor()
				)
			)
		}
	}
}
#endif
