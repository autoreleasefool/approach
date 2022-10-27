import ComposableArchitecture

extension BaseForm {
	public enum AlertAction: Equatable {
		case dismissed
		case discardButtonTapped
		case deleteButtonTapped
	}
}

extension BaseForm.State {
	var deleteAlert: AlertState<BaseForm<Model, FormState>.AlertAction>? {
		guard case let .edit(model) = self.mode else { return nil }

		return .init(
			title: TextState("Are you sure you want to delete \(model.name)"),
			primaryButton: .destructive(
				TextState("Delete"),
				action: .send(.deleteButtonTapped)
			),
			secondaryButton: self.dismissButton
		)
	}

	var discardAlert: AlertState<BaseForm<Model, FormState>.AlertAction> {
		.init(
			title: TextState("Discard your changes?"),
			primaryButton: .destructive(
				TextState("Discard"),
				action: .send(.discardButtonTapped)
			),
			secondaryButton: self.dismissButton
		)
	}

	private var dismissButton: AlertState<BaseForm<Model, FormState>.AlertAction>.Button {
		.cancel(TextState("Cancel"), action: .send(.dismissed))
	}
}
