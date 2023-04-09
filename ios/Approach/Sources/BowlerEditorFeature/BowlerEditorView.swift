import BaseFormLibrary
import ComposableArchitecture
import FeatureActionLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct BowlerEditorView: View {
	let store: StoreOf<BowlerEditor>

	struct ViewState: Equatable {
		@BindingState var name: String

		init(state: BowlerEditor.State) {
			self.name = state.base.form.name
		}
	}

	enum ViewAction: BindableAction {
		case onAppear
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<BowlerEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: BowlerEditor.Action.init) { viewStore in
			BaseFormView(store: store.scope(state: \.base, action: /BowlerEditor.Action.InternalAction.form)) {
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
		case .onAppear:
			self = .view(.onAppear)
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
					initialState: .init(mode: .edit(.init(id: UUID(), name: "Joseph", status: .playable))),
					reducer: BowlerEditor()
				)
			)
		}
	}
}
#endif
