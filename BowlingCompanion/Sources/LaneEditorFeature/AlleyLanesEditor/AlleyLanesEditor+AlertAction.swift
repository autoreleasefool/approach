import ComposableArchitecture
import SharedModelsLibrary
import StringsLibrary

extension AlleyLanesEditor {
	public enum AlertAction: Equatable {
		case deleteButtonTapped(Lane)
		case dismissed
	}

	static func alert(toDelete lane: Lane) -> AlertState<AlertAction> {
		.init(
			title: TextState(Strings.Lanes.Editor.Delete.title(lane.label)),
			primaryButton: .destructive(
				TextState(Strings.Lanes.Editor.Delete.action),
				action: .send(.deleteButtonTapped(lane))
			),
			secondaryButton: .cancel(
				TextState(Strings.Lanes.Editor.Delete.cancel),
				action: .send(.dismissed)
			)
		)
	}
}
