import ComposableArchitecture
import ModelsLibrary

//extension GamesEditorNext {
//	struct ActiveIndices {
//		@Shared var bowlerIds: [Bowler.ID]
//		@Shared var bowlerGameIds: [Bowler.ID: [Game.ID]]
//
//		@Shared var currentBowlerId: Bowler.ID
//		@Shared var currentGameId: Game.ID
//		@Shared var currentFrame: Frame.Selection
//
//		@Shared var bowlers: IdentifiedArrayOf<Bowler.Summary>?
//		@Shared var game: Game.Edit?
//		@Shared var frames: [Frame.Edit]?
//		@Shared var nextHeaderElement: GameDetailsHeaderNext.State.NextElement?
//
//		var currentGameIndex: Int {
//			bowlerGameIds[currentBowlerId]?.firstIndex(of: currentGameId) ?? 0
//		}
//
//		var currentBowlerIndex: Int {
//			bowlerIds.firstIndex(of: currentBowlerId)!
//		}
//	}
//}

//func updateActiveIndices(
//	rollIndex newRollIndex: Int? = nil,
//	frameIndex newFrameIndex: Int? = nil,
//	gameId newGameId: Game.ID? = nil,
//	bowlerId newBowlerId: Bowler.ID? = nil,
//	state: inout GamesEditorNext.ActiveIndices
//) {
//	guard let frames = state.frames else { return }
//
//	let isGameChanging = (newGameId != nil && newGameId != state.currentGameId) ||
//		(newBowlerId != nil && newBowlerId != state.currentBowlerId)
//	state.$currentFrame.withLock { $0.update(frameIndex: newFrameIndex, rollIndex: newRollIndex) }
//	state.$currentGameId.withLock { $0 = newGameId ?? $0 }
//	state.$currentBowlerId.withLock { $0 = newBowlerId ?? $0 }
//
//	if !isGameChanging {
//		let lastAccessibleRollInFrame = frames[state.currentFrame.frameIndex].lastAccessibleRollIndex
//		if lastAccessibleRollInFrame < state.currentFrame.rollIndex {
//			state.$currentFrame.withLock { $0.update(rollIndex: lastAccessibleRollInFrame) }
//		}
//	}
//
//	let forceNextHeaderElementOrNil = state.game?.scoringMethod == .manual
//		|| frames.nextFrameToRecord().hasUntouchedRoll != true
//	guard !forceNextHeaderElementOrNil else {
//		let numberOfBowlers = state.bowlerIds.count
//		let numberOfGames = state.bowlerGameIds.first?.value.count ?? 1
//		let nextGameIndex = state.currentGameIndex + 1
//
//		if numberOfBowlers == 1 {
//			state.$nextHeaderElement.withLock {
//				$0 = numberOfGames > nextGameIndex ? .game(
//					gameIndex: nextGameIndex,
//					bowler: state.currentBowlerId,
//					game: state.bowlerGameIds[state.currentBowlerId]![nextGameIndex]
//				) : nil
//			}
//		} else {
//			let nextBowlerIndex = (state.currentBowlerIndex + 1) % numberOfBowlers
//			let nextBowlerId = state.bowlerIds[nextBowlerIndex]
//
//			if nextBowlerIndex == 0 {
//				state.$nextHeaderElement.withLock {
//					$0 = numberOfGames > nextGameIndex ? .game(
//						gameIndex: nextGameIndex,
//						bowler: nextBowlerId,
//						game: state.bowlerGameIds[nextBowlerId]![nextGameIndex]
//					) : nil
//				}
//			} else {
//				state.$nextHeaderElement.withLock {
//					$0 = .bowler(name: state.bowlers![id: nextBowlerId]!.name, id: nextBowlerId)
//				}
//			}
//		}
//
//		return
//	}
//
//	let isLastRoll = Frame.isLastRoll(state.currentFrame.rollIndex)
//	let isLastFrame = Frame.isLast(state.currentFrame.frameIndex)
//	let arePinsClearedForRoll = frames[state.currentFrame.frameIndex]
//		.deck(forRoll: state.currentFrame.rollIndex)
//		.arePinsCleared
//
//	// If the current roll isn't the last, and there are still pins standing or it's the last frame,
//	// just show the next ball to be thrown
//	if !isLastRoll && (isLastFrame || !arePinsClearedForRoll) {
//		state.$nextHeaderElement.withLock { $0 = .roll(rollIndex: state.currentFrame.rollIndex + 1) }
//	} else {
//		// In this case, the frame is finished
//
//		let numberOfBowlers = state.bowlerIds.count
//		let numberOfGames = state.bowlerGameIds.first?.value.count ?? 1
//		let nextGameIndex = state.currentGameIndex + 1
//		let nextFrameIndex = state.currentFrame.frameIndex + 1
//
//		// If there's only one bowler, we only need to show either the next frame or next game
//		if numberOfBowlers == 1 {
//			// If it's the last frame, check if there's another game and go to it next, or show nothing
//			if Frame.isLast(state.currentFrame.frameIndex) {
//				state.$nextHeaderElement.withLock {
//					$0 = numberOfGames > nextGameIndex ? .game(
//						gameIndex: nextGameIndex,
//						bowler: state.currentBowlerId,
//						game: state.bowlerGameIds[state.currentBowlerId]![nextGameIndex]
//					) : nil
//				}
//			} else {
//				// If it's not the last frame, just show the next frame
//				state.$nextHeaderElement.withLock { $0 = .frame(frameIndex: nextFrameIndex) }
//			}
//		} else {
//			let nextBowlerIndex = (state.currentBowlerIndex + 1) % numberOfBowlers
//			let nextBowlerId = state.bowlerIds[nextBowlerIndex]
//
//			// If it's the last frame, we should show either the next bowler or, if there are no more bowlers,
//			// the next game, or nothing
//			if Frame.isLast(state.currentFrame.frameIndex) {
//				// When `nextBowlerIndex` is 0, there are no bowlers following the current one.
//				// Go to the next game, or show nothing
//				if nextBowlerIndex == 0 {
//					state.$nextHeaderElement.withLock {
//						$0 = numberOfGames > nextGameIndex ? .game(
//							gameIndex: nextGameIndex,
//							bowler: nextBowlerId,
//							game: state.bowlerGameIds[nextBowlerId]![nextGameIndex]
//						) : nil
//					}
//				} else {
//					// Otherwise, there's another bowler to show
//					state.$nextHeaderElement.withLock {
//						$0 = .bowler(name: state.bowlers![id: nextBowlerId]!.name, id: nextBowlerId)
//					}
//				}
//			} else {
//				// If it's not the last frame, we should show the next bowler
//				// When the next bowler's game is loaded, we'll load the correct frame at that time
//				state.$nextHeaderElement.withLock {
//					$0 = .bowler(name: state.bowlers![id: nextBowlerId]!.name, id: nextBowlerId)
//				}
//			}
//		}
//	}
//}
