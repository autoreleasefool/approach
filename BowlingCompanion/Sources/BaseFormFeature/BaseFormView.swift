import ComposableArchitecture
import SwiftUI

public struct BaseFormView<Model: BaseFormModel, FormState: BaseFormState, Content: View>: View where Model == FormState.Model {
	let store: StoreOf<BaseForm<Model, FormState>>
	let content: () -> Content

	struct ViewState: Equatable {
		let navigationTitle: String
		let isLoading: Bool
		let isSaveable: Bool
		let isDeleteable: Bool
		let isDiscardable: Bool
		let isDismissable: Bool

		init(state: BaseForm<Model, FormState>.State) {
			self.isLoading = state.isLoading
			self.isSaveable = state.isSaveable
			self.isDiscardable = state.hasChanges
			self.isDismissable = !state.hasChanges

			switch state.mode {
			case .create:
				self.navigationTitle = "Create \(Model.modelName)"
				self.isDeleteable = false
			case let .edit(model):
				self.navigationTitle = "Edit \(model.name)"
				self.isDeleteable = state.form.isDeleteable
			}
		}
	}

	enum ViewAction {
		case saveButtonTapped
		case deleteButtonTapped
		case discardButtonTapped
	}

	public init(
		store: StoreOf<BaseForm<Model, FormState>>,
		content: @escaping () -> Content
	) {
		self.store = store
		self.content = content
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: map(viewAction:)) { viewStore in
			Form {
				if viewStore.isLoading {
					ProgressView()
				}

				content()
					.disabled(viewStore.isLoading)

				if viewStore.isDeleteable {
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
						.disabled(!viewStore.isSaveable)
				}

				if viewStore.isDiscardable {
					ToolbarItem(placement: .navigationBarLeading) {
						Button("Discard") { viewStore.send(.discardButtonTapped) }
					}
				}
			}
			.alert(
				store.scope(state: \.alert, action: BaseForm.Action.alert),
				dismiss: .dismissed
			)
			.interactiveDismissDisabled(!viewStore.isDismissable)
		}
	}

	private func map(viewAction: ViewAction) -> BaseForm<Model, FormState>.Action {
		switch viewAction {
		case .saveButtonTapped:
			return .saveButtonTapped
		case .discardButtonTapped:
			return .discardButtonTapped
		case .deleteButtonTapped:
			return .deleteButtonTapped
		}
	}
}
