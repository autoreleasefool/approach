import ComposableArchitecture

extension BowlerForm {

	public enum AlertAction: Equatable {
		case deleteButtonTapped
		case discardButtonTapped
		case dismissed
	}

	func buildDeleteAlert(state: State) -> AlertState<AlertAction>? {
		guard case let .edit(bowler) = state.mode else { return nil }
		return .init(
			title: TextState("Are you sure you want to delete \(bowler.name)"),
			primaryButton: .destructive(
				TextState("Delete"),
				action: .send(.deleteButtonTapped)
			),
			secondaryButton: self.dismissButton
		)
	}

	var discardAlert: AlertState<AlertAction> {
		.init(
			title: TextState("Discard your changes?"),
			primaryButton: .destructive(
				TextState("Discard"),
				action: .send(.discardButtonTapped)
			),
			secondaryButton: self.dismissButton
		)
	}

	private var dismissButton: AlertState<AlertAction>.Button {
		.cancel(TextState("Cancel"), action: .send(.dismissed))
	}
}
