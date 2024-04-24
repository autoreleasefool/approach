import ComposableArchitecture

extension GamesEditor.State {
	mutating func syncGameHeader() {
		gamesHeader.currentGameIndex = currentGameIndex
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
				state.destination = .sheets(.settings(.init(
					bowlers: bowlers,
					currentBowlerId: state.currentBowlerId,
					numberOfGames: state.numberOfGames,
					gameIndex: state.currentGameIndex
				)))
				return .none

			case .didShareGame:
				// TODO: Show share sheet
				return .none
			}

		case .view, .internal:
			return .none
		}
	}
}
