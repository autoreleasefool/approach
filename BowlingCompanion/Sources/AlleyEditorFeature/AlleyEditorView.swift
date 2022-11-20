import BaseFormFeature
import ComposableArchitecture
import SwiftUI

public struct AlleyEditorView: View {
	let store: StoreOf<AlleyEditor>

	struct ViewState: Equatable {
		@BindableState var name: String

		init(state: AlleyEditor.State) {
			self.name = state.base.form.name
		}
	}

	enum ViewAction: BindableAction {
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<AlleyEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: AlleyEditor.Action.init) { viewStore in
			BaseFormView(store: store.scope(state: \.base, action: AlleyEditor.Action.form)) {
				Section {
					TextField("Name", text: viewStore.binding(\.$name))
				}
			}
		}
	}
}

extension AlleyEditor.State {
	var view: AlleyEditorView.ViewState {
		get { .init(state: self) }
		set {
			self.base.form.name = newValue.name
		}
	}
}

extension AlleyEditor.Action {
	init(action: AlleyEditorView.ViewAction) {
		switch action {
		case let .binding(action):
			self = .binding(action.pullback(\AlleyEditor.State.view))
		}
	}
}
