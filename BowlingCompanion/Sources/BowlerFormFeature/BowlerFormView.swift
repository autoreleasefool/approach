import BaseFormFeature
import ComposableArchitecture
import SwiftUI

public struct BowlerFormView: View {
	let store: StoreOf<BowlerForm>

	struct ViewState: Equatable {
		@BindableState var name: String

		init(state: BowlerForm.State) {
			self.name = state.base.form.name
		}
	}

	enum ViewAction: BindableAction {
		case binding(BindingAction<ViewState>)
	}

	public init(store: StoreOf<BowlerForm>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: BowlerForm.Action.init) { viewStore in
			BaseFormView(store: store.scope(state: \.base, action: BowlerForm.Action.form)) {
				Section {
					TextField("Name", text: viewStore.binding(\.$name))
				}
			}
		}
	}
}

extension BowlerForm.State {
	var view: BowlerFormView.ViewState {
		get { .init(state: self) }
		set {
			self.base.form.name = newValue.name
		}
	}
}

extension BowlerForm.Action {
	init(action: BowlerFormView.ViewAction) {
		switch action {
		case let .binding(action):
			self = .binding(action.pullback(\BowlerForm.State.view))
		}
	}
}
