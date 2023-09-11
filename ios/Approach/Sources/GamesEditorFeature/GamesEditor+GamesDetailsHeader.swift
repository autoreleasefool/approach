import ComposableArchitecture
import ModelsLibrary

extension GamesEditor.State {
	var gameDetailsHeader: GameDetailsHeader.State? {
		get {
			guard let _gameDetailsHeader else { return nil }

			var gameDetailsHeader = _gameDetailsHeader
			gameDetailsHeader.currentBowlerName = game?.bowler.name ?? ""
			gameDetailsHeader.currentLeagueName = game?.league.name ?? ""
			gameDetailsHeader.next = nextHeaderElement
			return gameDetailsHeader
		}
		set {
			_gameDetailsHeader = newValue
		}
	}

	var currentBowler: Bowler.Summary? { bowlers?[id: currentBowlerId] }
}

extension GamesEditor {
	func reduce(into state: inout State, gamesDetailsHeaderAction: GameDetailsHeader.Action) -> Effect<Action> {
		switch gamesDetailsHeaderAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case let .didProceed(next):
				switch next {
				case let .bowler(_, id):
					let saveGameEffect = lockGameIfFinished(in: &state)
					let gameIndex = state.currentGameIndex
					state.setCurrent(gameId: state.bowlerGameIds[id]![gameIndex], bowlerId: id)
					return .merge(
						saveGameEffect,
						loadGameDetails(state: &state)
					)
				case let .frame(frameIndex):
					state.setCurrent(rollIndex: 0, frameIndex: frameIndex)
					state.frames?[frameIndex].guaranteeRollExists(upTo: 0)
					return save(frame: state.frames?[frameIndex])
				case let .roll(rollIndex):
					state.setCurrent(rollIndex: rollIndex)
					let currentFrameIndex = state.currentFrameIndex
					state.frames?[currentFrameIndex].guaranteeRollExists(upTo: rollIndex)
					return save(frame: state.frames?[state.currentFrameIndex])
				case let .game(_, bowler, game):
					let saveGameEffect = lockGameIfFinished(in: &state)
					state.setCurrent(gameId: game, bowlerId: bowler)
					return .merge(
						saveGameEffect,
						loadGameDetails(state: &state)
					)
				}
			}

		case .view, .internal:
			return .none
		}
	}

	func lockGameIfFinished(in state: inout State) -> Effect<Action> {
		if Frame.isLast(state.currentFrameIndex) && Frame.Roll.isLast(state.currentRollIndex) {
			state.game?.locked = .locked
			return save(game: state.game)
		}

		return .none
	}
}
