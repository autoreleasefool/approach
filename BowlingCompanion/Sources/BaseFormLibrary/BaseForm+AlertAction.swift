import ComposableArchitecture
import StringsLibrary

extension BaseForm {
	public enum AlertAction: Equatable {
		case didTapDismissButton
		case didTapDiscardButton
		case didTapDeleteButton
	}
}

extension BaseForm.State {
	var deleteAlert: AlertState<BaseForm<Model, FormState>.AlertAction>? {
		guard case let .edit(model) = self.mode else { return nil }

		return .init(
			title: TextState(Strings.Form.Prompt.delete(model.name)),
			primaryButton: .destructive(
				TextState(Strings.Action.delete),
				action: .send(.didTapDeleteButton)
			),
			secondaryButton: .cancel(TextState(Strings.Action.cancel), action: .send(.didTapDismissButton))
		)
	}

	var discardAlert: AlertState<BaseForm<Model, FormState>.AlertAction> {
		.init(
			title: TextState(Strings.Form.Prompt.discardChanges),
			primaryButton: .destructive(
				TextState(Strings.Action.discard),
				action: .send(.didTapDiscardButton)
			),
			secondaryButton: .cancel(TextState(Strings.Action.cancel), action: .send(.didTapDismissButton))
		)
	}
}
