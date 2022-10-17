import ComposableArchitecture
import SwiftUI

public struct BowlerFormView: View {
	let store: StoreOf<BowlerForm>

	struct ViewState: Equatable {
		let name: String
		let isLoading: Bool
		let navigationTitle: String
		let showDeleteButton: Bool
		let saveButtonDisabled: Bool
		let dismissDisabled: Bool
		let discardButtonEnabled: Bool

		init(state: BowlerForm.State) {
			self.name = state.name
			self.isLoading = state.isLoading
			self.navigationTitle = state.mode == .create ? "Create Bowler" : "Edit Bowler"
			self.saveButtonDisabled = !state.canSave
			self.showDeleteButton = state.mode == .create ? false : true
			self.dismissDisabled = state.hasChanges || state.isLoading
			self.discardButtonEnabled = state.hasChanges && !state.isLoading
		}
	}

	enum ViewAction {
		case nameChange(String)
		case saveButtonTapped
		case deleteButtonTapped
		case discardButtonTapped
	}

	public init(store: StoreOf<BowlerForm>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: BowlerForm.Action.init) { viewStore in
			Form {
				if viewStore.isLoading {
					ProgressView()
				}

				Section("Details") {
					TextField("Name", text: viewStore.binding(get: \.name, send: ViewAction.nameChange))
						.disabled(viewStore.isLoading)
				}

				if viewStore.showDeleteButton {
					Button(role: .destructive) {
						viewStore.send(.deleteButtonTapped)
					} label: {
						Text("Delete")
					}
				}
			}
			.navigationTitle(viewStore.navigationTitle)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Button("Save") { viewStore.send(.saveButtonTapped) }
						.disabled(viewStore.saveButtonDisabled)
				}

				if viewStore.discardButtonEnabled {
					ToolbarItem(placement: .navigationBarLeading) {
						Button("Discard") { viewStore.send(.discardButtonTapped) }
					}
				}
			}
			.alert(
				self.store.scope(state: \.alert, action: BowlerForm.Action.alert),
				dismiss: .dismissed
			)
			.interactiveDismissDisabled(viewStore.dismissDisabled)
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
		case .discardButtonTapped:
			self = .discardButtonTapped
		case .deleteButtonTapped:
			self = .deleteButtonTapped
		}
	}
}
