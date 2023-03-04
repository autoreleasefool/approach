import ComposableArchitecture
import SharedModelsLibrary
import StringsLibrary

extension AlleyLanesEditor {
	public enum AlertAction: Equatable {
		case didTapDeleteButton(Lane)
		case didTapDismissButton
	}

	static func alert(toDelete lane: Lane) -> AlertState<AlertAction> {
		.init(
			title: TextState(Strings.Form.Prompt.delete(lane.label)),
			primaryButton: .destructive(
				TextState(Strings.Action.delete),
				action: .send(.didTapDeleteButton(lane))
			),
			secondaryButton: .cancel(
				TextState(Strings.Action.cancel),
				action: .send(.didTapDismissButton)
			)
		)
	}
}
