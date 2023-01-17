import ComposableArchitecture
import StringsLibrary

extension ResourceList {
	public enum AlertAction: Equatable {
		case deleteButtonTapped(R)
		case dismissed
	}
}

extension ResourceList {
	static func alert(toDelete resource: R) -> AlertState<AlertAction> {
		.init(
			title: TextState(Strings.Form.Prompt.delete(resource.name)),
			primaryButton: .destructive(
				TextState(Strings.Action.delete),
				action: .send(.deleteButtonTapped(resource))
			),
			secondaryButton: .cancel(
				TextState(Strings.Action.cancel),
				action: .send(.dismissed)
			)
		)
	}
}
