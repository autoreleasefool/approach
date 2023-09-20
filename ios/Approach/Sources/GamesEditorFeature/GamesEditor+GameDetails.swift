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
			case .didRequestOpponentPicker:
				let opponent = Set([state.game?.matchPlay?.opponent?.id].compactMap { $0 })
				state.destination = .opponentPicker(.init(
					selected: opponent,
					query: .init(()),
					limit: 1,
					showsCancelHeaderButton: false
				))
				return .none

			case .didRequestGearPicker:
				let gear = Set(state.game?.gear.map(\.id) ?? [])
				state.destination = .gearPicker(.init(
					selected: gear,
					query: .init(())
				))
				return .none

			case .didRequestLanePicker:
				guard let alleyId = state.game?.series.alley?.id else { return .none }
				let lanes = Set(state.game?.lanes.map(\.id) ?? [])
				state.destination = .lanePicker(.init(
					selected: lanes,
					query: alleyId
				))
				return .none

			case let .didEditMatchPlay(.success(matchPlay)):
				state.game?.matchPlay = matchPlay
				return save(matchPlay: state.game?.matchPlay)

			case let .didEditMatchPlay(.failure(error)):
				return state.errors
					.enqueue(.failedToSaveMatchPlay, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
					.map { .internal(.errors($0)) }

			case let .didEditGame(game):
				guard let game else { return .none }
				state.game = game
				state.hideNextHeaderIfNecessary()
				return save(game: state.game)

			case .didClearManualScore:
				state.hideNextHeaderIfNecessary()
				state.game?.scoringMethod = .byFrame
				state.game?.score = state.score?.frames.gameScore() ?? 0
				return save(game: state.game)

			case .didProvokeLock:
				return state.presentLockedToast()

			case let .didMeasureMinimumSheetContentSize(size):
				state.gameDetailsMinimumContentSize = size
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

		case .internal, .view:
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
