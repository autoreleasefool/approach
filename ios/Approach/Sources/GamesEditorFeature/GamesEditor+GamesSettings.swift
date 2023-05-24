import ComposableArchitecture

extension GamesEditor.State {
	var gamesSettings: GamesSettings.State? {
		get {
			guard let bowlers else { return nil }
			return .init(
				bowlers: bowlers,
				currentBowlerId: currentBowlerId,
				numberOfGames: numberOfGames,
				gameIndex: currentGameIndex
			)
		}
		// We aren't observing any values from this reducer, so we ignore the setter
		// swiftlint:disable:next unused_setter_value
		set { }
	}
}

extension GamesEditor {
	func reduce(into state: inout State, gamesSettingsAction: GamesSettings.Action) -> Effect<Action> {
		switch gamesSettingsAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case let .switchedGame(to):
				state.currentGameId = state.bowlerGameIds[state.currentBowlerId]![to]
				return loadGameDetails(state: &state)

			case let .switchedBowler(to):
				state.currentGameId = state.bowlerGameIds[to]![state.currentGameIndex]
				state.currentBowlerId = to
				return loadGameDetails(state: &state)

			case let .movedBowlers(source, destination):
				state.bowlers?.move(fromOffsets: source, toOffset: destination)
				state.bowlerIds.move(fromOffsets: source, toOffset: destination)
				return .none

			case .didFinish:
				state.sheet.hide(.settings)
				return .none
			}

		case .internal, .view:
			return .none
		}
	}
}
