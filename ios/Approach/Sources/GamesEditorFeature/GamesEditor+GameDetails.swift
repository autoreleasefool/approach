import ComposableArchitecture

extension GamesEditor.State {
	var gameDetails: GameDetails.State? {
		get {
			guard let _gameDetails, let game else { return nil }
			var gameDetails = _gameDetails
			gameDetails.game = game
			return gameDetails
		}
		set {
			guard isEditable, let newValue, currentGameId == newValue.game.id else { return }
			_gameDetails = newValue
			game = newValue.game
		}
	}
}

extension GamesEditor {
	func reduce(into state: inout State, gameDetailsAction: GameDetails.Action) -> Effect<Action> {
		switch gameDetailsAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case .didRequestOpponentPicker:
				state.sheet.transition(to: .opponentPicker)
				return .none

			case .didEditGame:
				return save(game: state.game)

			case .didClearManualScore:
				return updateScoreSheet(from: state)
			}

		case .internal, .view:
			return .none
		}
	}
}
