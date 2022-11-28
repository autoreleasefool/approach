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
			title: TextState(Strings.Form.Delete.title(model.name)),
			primaryButton: .destructive(
				TextState(Strings.Form.Delete.action),
				action: .send(.deleteButtonTapped)
			),
			secondaryButton: self.dismissButton
		)
	}

	var discardAlert: AlertState<BaseForm<Model, FormState>.AlertAction> {
		.init(
			title: TextState(Strings.Form.Discard.title),
			primaryButton: .destructive(
				TextState(Strings.Form.Discard.action),
				action: .send(.discardButtonTapped)
			),
			secondaryButton: self.dismissButton
		)
	}

	private var dismissButton: AlertState<BaseForm<Model, FormState>.AlertAction>.Button {
		.cancel(TextState(Strings.Form.cancel), action: .send(.dismissed))
	}
}
