import ComposableArchitecture
import SharedModelsLibrary
import StringsLibrary

extension AlleysList {
	public enum AlertAction: Equatable {
		case deleteButtonTapped(Alley)
		case dismissed
	}

	static func alert(toDelete alley: Alley) -> AlertState<AlertAction> {
		.init(
			title: TextState(Strings.Form.Prompt.delete(alley.name)),
			primaryButton: .destructive(
				TextState(Strings.Action.delete),
				action: .send(.deleteButtonTapped(alley))
			),
			secondaryButton: .cancel(
				TextState(Strings.Action.cancel),
				action: .send(.dismissed)
			)
		)
	}
}
