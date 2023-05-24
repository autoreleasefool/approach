import ComposableArchitecture
import ModelsLibrary

extension GamesEditor.State {
	var gameDetailsHeader: GameDetailsHeader.State? {
		get {
			guard let game, let frames else { return nil }
			let next: GameDetailsHeader.State.NextElement?
			if !Frame.Roll.isLast(currentRollIndex) &&
					(Frame.isLast(currentFrameIndex) || !frames[currentFrameIndex].deck(forRoll: currentRollIndex).isFullDeck) {
				next = .roll(rollIndex: currentRollIndex + 1)
			} else if bowlerIds.count > 1 {
				let nextBowlerId = bowlerIds[(currentBowlerIndex + 1) % bowlerIds.count]
				next = .bowler(name: bowlers![id: nextBowlerId]!.name, id: nextBowlerId)
			} else if !Frame.isLast(currentFrameIndex) {
				next = .frame(frameIndex: currentFrameIndex + 1)
			} else {
				next = nil
				// TODO: figure out when to show next game
			}

			return .init(game: game, next: next)
		}
		// We aren't observing any values from this reducer, so we ignore the setter
		// swiftlint:disable:next unused_setter_value
		set { }
	}
}

extension GamesEditor {
	func reduce(into state: inout State, gamesDetailsHeaderAction: GameDetailsHeader.Action) -> Effect<Action> {
		switch gamesDetailsHeaderAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case let .didProceed(next):
				switch next {
				case let .bowler(_, id):
					let gameIndex = state.currentGameIndex
					state.currentBowlerId = id
					state.currentGameId = state.bowlerGameIds[id]![gameIndex]
					return loadGameDetails(state: &state)
				case let .frame(frameIndex):
					state.currentFrameIndex = frameIndex
					state.currentRollIndex = 0
					state.frames?[frameIndex].guaranteeRollExists(upTo: 0)
					return save(frame: state.frames?[frameIndex])
				case let .roll(rollIndex):
					state.currentRollIndex = rollIndex
					state.frames?[state.currentFrameIndex].guaranteeRollExists(upTo: rollIndex)
					return save(frame: state.frames?[state.currentFrameIndex])
				case let .game(_, bowler, game):
					state.currentBowlerId = bowler
					state.currentGameId = game
					return loadGameDetails(state: &state)
				}
			}

		case .view, .internal:
			return .none
		}
	}
}
