import ComposableArchitecture

extension GamesEditor.State {
	var gamesHeader: GamesHeader.State {
		get {
			.init(currentGameIndex: currentGameIndex, isSharingGameEnabled: isSharingGameEnabled)
		}
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
				return .run { _ in await dismiss() }

			case .didOpenSettings:
				guard let bowlers = state.bowlers else { return .none }
				state.destination = .settings(.init(
					bowlers: bowlers,
					currentBowlerId: state.currentBowlerId,
					numberOfGames: state.numberOfGames,
					gameIndex: state.currentGameIndex
				))
				return .none

			case .didShareGame:
				state.destination = .sharing(.init(dataSource: .games([state.currentGameId])))
				return .none
			}

		case .view, .internal:
			return .none
		}
	}
}
