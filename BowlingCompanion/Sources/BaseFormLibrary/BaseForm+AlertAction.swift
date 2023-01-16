import ComposableArchitecture
import StringsLibrary

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
			title: TextState(Strings.Form.Prompt.delete(model.name)),
			primaryButton: .destructive(
				TextState(Strings.Action.delete),
				action: .send(.deleteButtonTapped)
			),
			secondaryButton: .cancel(TextState(Strings.Action.cancel), action: .send(.dismissed))
		)
	}

	var discardAlert: AlertState<BaseForm<Model, FormState>.AlertAction> {
		.init(
			title: TextState(Strings.Form.Prompt.discardChanges),
			primaryButton: .destructive(
				TextState(Strings.Action.discard),
				action: .send(.discardButtonTapped)
			),
			secondaryButton: .cancel(TextState(Strings.Action.cancel), action: .send(.dismissed))
		)
	}
}
