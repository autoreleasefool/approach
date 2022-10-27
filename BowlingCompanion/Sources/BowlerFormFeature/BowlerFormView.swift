import ComposableArchitecture
import BaseFormFeature
import SwiftUI

public struct BowlerFormView: View {
	let store: StoreOf<BowlerForm>

	struct ViewState: Equatable {
		let name: String

		init(state: BowlerForm.State) {
			self.name = state.base.form.name
		}
	}

	enum ViewAction {
		case nameChange(String)
	}

	public init(store: StoreOf<BowlerForm>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: BowlerForm.Action.init) { viewStore in
			BaseFormView(store: store.scope(state: \.base, action: BowlerForm.Action.form)) {
				Section {
					TextField("Name", text: viewStore.binding(get: \.name, send: ViewAction.nameChange))
				}
			}
		}
	}
}

extension BowlerForm.Action {
	init(action: BowlerFormView.ViewAction) {
		switch action {
		case let .nameChange(name):
			self = .nameChange(name)
		}
	}
}
