import ComposableArchitecture

extension GamesEditor {
	func reduce(into state: inout State, gamesSettingsAction: GamesSettings.Action) -> Effect<Action> {
		switch gamesSettingsAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case let .switchedGame(to):
				let newGameId = state.bowlerGameIds[state.currentBowlerId]![to]
				state.setCurrent(gameId: newGameId)
				return loadGameDetails(state: &state)

			case let .switchedBowler(to):
				let newGameId = state.bowlerGameIds[to]![state.currentGameIndex]
				state.setCurrent(gameId: newGameId, bowlerId: to)
				return loadGameDetails(state: &state)

			case let .movedBowlers(source, destination):
				state.bowlers?.move(fromOffsets: source, toOffset: destination)
				state.bowlerIds.move(fromOffsets: source, toOffset: destination)
				return .none
			}

		case .internal, .view:
			return .none
		}
	}
}
