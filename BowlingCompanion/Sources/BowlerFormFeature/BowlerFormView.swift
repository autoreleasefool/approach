import ComposableArchitecture
import SwiftUI

public struct BowlerFormView: View {
	let store: StoreOf<BowlerForm>

	struct ViewState: Equatable {
		let name: String
		let isSaving: Bool
		let navigationTitle: String

		var saveButtonDisabled: Bool {
			name.isEmpty
		}

		init(state: BowlerForm.State) {
			self.name = state.name
			self.isSaving = state.isSaving
			self.navigationTitle = state.mode == .create ? "Create Bowler" : "Edit Bowler"
		}
	}

	enum ViewAction {
		case nameChange(String)
		case saveButtonTapped
	}

	public init(store: StoreOf<BowlerForm>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: BowlerForm.Action.init) { viewStore in
			Form {
				if viewStore.isSaving {
					ProgressView()
				}

				Section("Details") {
					TextField("Name", text: viewStore.binding(get: \.name, send: ViewAction.nameChange))
						.disabled(viewStore.isSaving)
				}
			}
			.navigationTitle(viewStore.navigationTitle)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Button("Save") { viewStore.send(.saveButtonTapped) }
						.disabled(viewStore.saveButtonDisabled)
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
		case .saveButtonTapped:
			self = .saveButtonTapped
		}
	}
}
