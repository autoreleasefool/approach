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
			title: TextState(Strings.Alleys.List.Delete.title(alley.name)),
			primaryButton: .destructive(
				TextState(Strings.Alleys.List.Delete.action),
				action: .send(.deleteButtonTapped(alley))
			),
			secondaryButton: .cancel(
				TextState(Strings.Alleys.List.Delete.cancel),
				action: .send(.dismissed)
			)
		)
	}
}
