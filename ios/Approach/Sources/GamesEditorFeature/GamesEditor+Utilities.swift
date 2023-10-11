import ModelsLibrary

extension GamesEditor.State {
	// swiftlint:disable:next function_body_length
	mutating func setCurrent(
		rollIndex newRollIndex: Int? = nil,
		frameIndex newFrameIndex: Int? = nil,
		gameId newGameId: Game.ID? = nil,
		bowlerId newBowlerId: Bowler.ID? = nil
	) {
		guard let frames else { return }

		let isGameChanging = (newGameId != nil && _currentGameId != newGameId) ||
			(newBowlerId != nil && _currentBowlerId != newBowlerId)
		currentFrame.rollIndex = newRollIndex ?? currentFrame.rollIndex
		currentFrame.frameIndex = newFrameIndex ?? currentFrame.frameIndex
		_currentGameId = newGameId ?? _currentGameId
		_currentBowlerId = newBowlerId ?? _currentBowlerId

		if !isGameChanging {
			let lastAccessibleRollInFrame = frames[currentFrame.frameIndex].lastAccessibleRollIndex
			if lastAccessibleRollInFrame < currentFrame.rollIndex {
				currentFrame.rollIndex = lastAccessibleRollInFrame
			}
		}

		guard !forceNextHeaderElementNil else {
			_nextHeaderElement = nil
			return
		}

		// If the current roll isn't the last, and there are still pins standing or it's the last frame,
		// just show the next ball to be thrown
		if !Frame.isLastRoll(currentRollIndex) &&
				(Frame.isLast(currentFrameIndex) || !frames[currentFrameIndex].deck(forRoll: currentRollIndex).arePinsCleared) {
			_nextHeaderElement = .roll(rollIndex: currentRollIndex + 1)
		} else {
			// In this case, the frame is finished

			let numberOfBowlers = bowlerIds.count
			let numberOfGames = bowlerGameIds.first?.value.count ?? 1
			let nextGameIndex = currentGameIndex + 1
			let nextFrameIndex = currentFrameIndex + 1

			// If there's only one bowler, we only need to show either the next frame or next game
			if numberOfBowlers == 1 {
				// If it's the last frame, check if there's another game and go to it next, or show nothing
				if Frame.isLast(currentFrameIndex) {
					_nextHeaderElement = numberOfGames > nextGameIndex ? .game(
						gameIndex: nextGameIndex,
						bowler: currentBowlerId,
						game: bowlerGameIds[currentBowlerId]![nextGameIndex]
					) : nil
				} else {
					// If it's not the last frame, just show the next frame
					_nextHeaderElement = .frame(frameIndex: nextFrameIndex)
				}
			} else {
				let nextBowlerIndex = (currentBowlerIndex + 1) % bowlerIds.count
				let nextBowlerId = bowlerIds[nextBowlerIndex]

				// If it's the last frame, we should show either the next bowler or, if there are no more bowlers,
				// the next game, or nothing
				if Frame.isLast(currentFrameIndex) {
					// When `nextBowlerIndex` is 0, there are no bowlers following the current one.
					// Go to the next game, or show nothing
					if nextBowlerIndex == 0 {
						_nextHeaderElement = numberOfGames > nextGameIndex ? .game(
							gameIndex: nextGameIndex,
							bowler: nextBowlerId,
							game: bowlerGameIds[nextBowlerId]![nextGameIndex]
						) : nil
					} else {
						// Otherwise, there's another bowler to show
						_nextHeaderElement = .bowler(name: bowlers![id: nextBowlerId]!.name, id: nextBowlerId)
					}
				} else {
					// If it's not the last frame, we should show the next bowler
					// When the next bowler's game is loaded, we'll automatically set the correct frame
					_nextHeaderElement = .bowler(name: bowlers![id: nextBowlerId]!.name, id: nextBowlerId)
				}
			}
		}

		// Keep _nextHeaderElement in sync with GameDetails
		switch destination {
		case var .gameDetails(details):
			details.nextHeaderElement = _nextHeaderElement
			destination = .gameDetails(details)
		case .duplicateLanesAlert, .sheets, .none:
			break
		}
	}

	mutating func hideNextHeaderIfNecessary(
		updatingRollIndexTo: Int? = nil,
		frameIndex: Int? = nil
	) {
		forceNextHeaderElementNilOrNextGame = game?.scoringMethod == .manual
			|| frames?.nextFrameToRecord().hasUntouchedRoll != true
		setCurrent(rollIndex: updatingRollIndexTo, frameIndex: frameIndex)
	}

	mutating func populateFrames(upTo: Int) {
		for frameIndex in 0..<upTo {
			guard let rollsCount = frames?[frameIndex].rolls.count, rollsCount < Frame.NUMBER_OF_ROLLS else { continue }
			let lastRollIndex = rollsCount - 1
			if frames?[frameIndex].deck(forRoll: lastRollIndex).arePinsCleared == false {
				frames?[frameIndex].guaranteeRollExists(upTo: Frame.NUMBER_OF_ROLLS - 1)
			}
		}
	}
}
