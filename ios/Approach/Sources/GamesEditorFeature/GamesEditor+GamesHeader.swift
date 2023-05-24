import ComposableArchitecture

extension GamesEditor.State {
	var gamesHeader: GamesHeader.State {
		get { .init(currentGameIndex: currentGameIndex) }
		// We aren't observing any values from this reducer, so we ignore the setter
		// swiftlint:disable:next unused_setter_value
		set {}
	}
}

extension GamesEditor {
	func reduce(into state: inout State, gamesHeaderAction: GamesHeader.Action) -> Effect<Action> {
		switch gamesHeaderAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case .didCloseEditor:
				// TODO: close the games editor
				return .none

			case .didOpenSettings:
				state.sheet.transition(to: .settings)
				return .none
			}

		case .view, .internal:
			return .none
		}
	}
}
