import ComposableArchitecture
import MatchPlaysRepositoryInterface
import ModelsLibrary
import StringsLibrary

extension GamesEditor {
	// swiftlint:disable:next function_body_length
	func reduce(into state: inout State, gameDetailsAction: GameDetails.Action) -> Effect<Action> {
		switch gameDetailsAction {
		case let .delegate(delegateAction):
			switch delegateAction {
			case let .didEditMatchPlay(.success(matchPlay)):
				guard state.game?.matchPlay == nil || state.game?.matchPlay?.id == matchPlay?.id else { return .none }
				state.game?.matchPlay = matchPlay
				return save(matchPlay: state.game?.matchPlay)

			case let .didEditMatchPlay(.failure(error)):
				return state.errors
					.enqueue(.failedToSaveMatchPlay, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
					.map { .internal(.errors($0)) }

			case let .didEditGame(game):
				guard let game, state.currentGameId == game.id else { return .none }
				state.game = game
				state.hideNextHeaderIfNecessary()
				state.syncFrameEditorSharedState()
				state.syncRollEditorSharedState()
				return save(game: state.game, in: state)

			case .didClearManualScore:
				state.hideNextHeaderIfNecessary()
				let gameScore = state.score?.frames.gameScore() ?? 0
				state.game?.scoringMethod = .byFrame
				state.game?.score = gameScore
				return save(game: state.game, in: state)

			case .didSelectLanes:
				let hasOtherGames = Set(state.bowlerGameIds.flatMap { $0.value }).count > 1
				if hasOtherGames && !state.didPromptLaneDuplication {
					state.didPromptLaneDuplication = true
					state.willShowDuplicateLanesAlert = true
					state.destination = nil
				}
				return .none

			case .didProvokeLock:
				return state.presentLockedAlert()

			case let .didMeasureMinimumSheetContentSize(size):
				state.gameDetailsMinimumContentSize = size
				if state.sheetDetent == .height(.zero) {
					state.sheetDetent = .height(state.gameDetailsMinimumContentSize.height + 40)
				}
				return .none

			case let .didMeasureSectionHeaderContentSize(size):
				state.gameDetailsHeaderSize = size
				return .none

			case let .didProceed(next):
				switch next {
				case let .bowler(_, id):
					let saveGameEffect = lockGameIfFinished(in: &state)
					let gameIndex = state.currentGameIndex
					state.setCurrent(gameId: state.bowlerGameIds[id]![gameIndex], bowlerId: id)
					state.didChangeBowler = true
					state.syncFrameEditorSharedState()
					state.syncRollEditorSharedState()
					return .merge(
						saveGameEffect,
						loadGameDetails(state: &state)
					)
				case let .frame(frameIndex):
					state.setCurrent(rollIndex: 0, frameIndex: frameIndex)
					state.frames?[frameIndex].guaranteeRollExists(upTo: 0)
					state.syncFrameEditorSharedState()
					state.syncRollEditorSharedState()
					return save(frame: state.frames?[frameIndex])
				case let .roll(rollIndex):
					state.setCurrent(rollIndex: rollIndex)
					let currentFrameIndex = state.currentFrameIndex
					state.frames?[currentFrameIndex].guaranteeRollExists(upTo: rollIndex)
					state.syncFrameEditorSharedState()
					state.syncRollEditorSharedState()
					return save(frame: state.frames?[state.currentFrameIndex])
				case let .game(_, bowler, game):
					state.shouldRequestAppStoreReview = storeReview.shouldRequestReview()

					let saveGameEffect = lockGameIfFinished(in: &state)
					state.setCurrent(gameId: game, bowlerId: bowler)
					state.syncFrameEditorSharedState()
					state.syncRollEditorSharedState()
					return .merge(
						saveGameEffect,
						loadGameDetails(state: &state)
					)
				}
			}

		case .internal, .view:
			return .none
		}
	}

	func lockGameIfFinished(in state: inout State) -> Effect<Action> {
		if Frame.isLast(state.currentFrameIndex) && Frame.isLastRoll(state.currentRollIndex) {
			state.game?.locked = .locked
			return save(game: state.game, in: state)
		}

		return .none
	}
}

extension GamesEditor.State {
	// FIXME: Should not be generating indices from array of game ids, indices should be loaded
	var currentBowlerGames: IdentifiedArrayOf<Game.Indexed> {
		.init(
			uniqueElements: bowlerGameIds[currentBowlerId]?
				.enumerated()
				.map { .init(id: $0.element, index: $0.offset) }
			?? []
		)
	}
}
