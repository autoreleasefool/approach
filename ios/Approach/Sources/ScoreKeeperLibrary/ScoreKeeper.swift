import ScoreKeeperModelsLibrary

struct SequencedRoll: Equatable {
	let frameIndex: Int
	let rollIndex: Int
	let roll: ScoreKeeper.Roll
}

public class ScoreKeeper {
	public init() {}

	private func padRolls(_ rolls: [ScoredRoll], displayValue: String?) -> [ScoredRoll] {
		rolls + Frame.rollIndices(after: rolls.endIndex - 1)
			.map { .init(index: $0, displayValue: displayValue, didFoul: false, isSecondary: false) }
	}

	private func padFrames(_ frames: [ScoredFrame]) -> [ScoredFrame] {
		frames + Game.frameIndices(after: frames.endIndex - 1)
			.map { .init(index: $0, rolls: padRolls([], displayValue: nil), score: nil) }
	}

	public func calculateHighestScorePossible(from frames: [[Roll]]) -> Int {
		let scoredFrames = calculateScore(from: frames)
		guard let currentScore = scoredFrames.gameScore() else {
			return Game.MAXIMUM_SCORE
		}

		let lastValidFrame = frames.lastIndex { !$0.isEmpty } ?? 0
		let pinValueForFrame = pinValueRemaining(forFrame: frames[lastValidFrame], frameIndex: lastValidFrame)
		let remainingFrameIndices = (lastValidFrame + 1)..<Game.NUMBER_OF_FRAMES
		return currentScore + pinValueForFrame + remainingFrameIndices.count * 45
	}

	private func pinValueRemaining(forFrame frame: [Roll], frameIndex: Int) -> Int {
		guard frame.count < Frame.NUMBER_OF_ROLLS else { return 0 }

		var pinsDowned: Set<Pin> = []
		for roll in frame {
			pinsDowned.formUnion(roll.pinsDowned)
			if pinsDowned.count == 5 {
				if Frame.isLast(frameIndex) {
					pinsDowned = []
				} else {
					return 0
				}
			}
		}

		let standingPinValue = Pin.fullDeck.value - pinsDowned.value
		if Frame.isLast(frameIndex) {
			let framesNeededForStandingPinValue = standingPinValue > 0 ? 1 : 0
			return Pin.fullDeck.value *
				(Frame.NUMBER_OF_ROLLS - frame.count - framesNeededForStandingPinValue) +
				standingPinValue
		} else {
			return standingPinValue + (frame.count == 1 ? 15 : 0)
		}
	}

	// swiftlint:disable:next function_body_length
	public func calculateScore(from frames: [[Roll]]) -> [ScoredFrame] {
		// The output is a step indicating the score and status of the game after each frame
		var steps: [ScoredFrame] = []

		let lastValidFrame = frames.lastIndex { !$0.isEmpty } ?? 0

		var rolls: [SequencedRoll] = []
		for (frameIndex, frame) in frames.enumerated() {
			// Must be at least 1 roll or we skip the frame
			guard !frame.isEmpty else {
				if frameIndex < lastValidFrame {
					// Insert empty rolls for missing frames
					rolls.append(contentsOf: Frame.ROLL_INDICES.map {
						.init(frameIndex: frameIndex, rollIndex: $0, roll: .init(pinsDowned: [], didFoul: false))
					})
				}
				continue
			}

			var pinsDowned: Set<Pin> = []
			for (rollIndex, roll) in frame.enumerated() {
				pinsDowned.formUnion(roll.pinsDowned)
				rolls.append(.init(frameIndex: frameIndex, rollIndex: rollIndex, roll: roll))
				if pinsDowned.count == 5 && !Frame.isLast(frameIndex) {
					break
				}
			}

			if frameIndex < lastValidFrame && frame.count < Frame.NUMBER_OF_ROLLS && !pinsDowned.arePinsCleared {
				// Insert empty rolls if unrecorded
				rolls.append(contentsOf: (frame.count...Frame.ROLL_INDICES.upperBound - 1).map {
					.init(frameIndex: frameIndex, rollIndex: $0, roll: .init(pinsDowned: [], didFoul: false))
				})
			}
		}

		// Ensure there is at least one roll in the game, or return a nil score
		guard !rolls.isEmpty else {
			return frames.indices.map { index in
					.init(
						index: index,
						rolls: Frame.ROLL_INDICES.map { rollIndex in
								.init(index: rollIndex, displayValue: nil, didFoul: false, isSecondary: false)
						},
						score: nil
					)
			}
		}

		// Cumulative set of pins downed in the frame
		var pinsDown: Set<Pin> = []
		// Each roll to be displayed in the final output
		var rollSteps: [ScoredRoll] = []
		var accruedScore = 0

		// Calculate all except the final frame
		for (index, roll) in rolls.enumerated() where !Frame.isLast(roll.frameIndex) {
			accruedScore -= (roll.roll.didFoul ? 1 : 0) * Game.FOUL_PENALTY

			// Accumulate the downed pins. Assume this is reset appropriately between frames below
			pinsDown.formUnion(roll.roll.pinsDowned)

			// When all the pins have been cleared
			if pinsDown.count == 5 && !Frame.isLastRoll(roll.rollIndex) {
				// Append a roll with the full deck cleared
				rollSteps.append(.init(
					index: rollSteps.count,
					displayValue: pinsDown.displayValue(rollIndex: roll.rollIndex),
					didFoul: roll.roll.didFoul,
					isSecondary: false
				))

				var stepScore = pinsDown.value
				let rollsToAdd = 2 - roll.rollIndex
				var acceptableFrameIndexForNextRoll = roll.frameIndex + 1

				// If the roll was a spare or a strike, add the scores of the subsequent rolls to this roll
				if rollsToAdd > 0 {
					for addedRollIndex in 1...rollsToAdd where index + addedRollIndex < rolls.endIndex {
						let nextRoll = rolls[index + addedRollIndex]

						// We should only consider rolls from the correct frame, not any frame
						// Handles the bug where there may not be any rolls recorded for an empty frame
						guard nextRoll.frameIndex == acceptableFrameIndexForNextRoll || Frame.isLast(nextRoll.frameIndex) else {
							break
						}

						let pinsToAdd = nextRoll.roll.pinsDowned

						// If the frame is cleared, we should get rolls from the subsequent frame
						if pinsToAdd.arePinsCleared {
							acceptableFrameIndexForNextRoll += 1
						}

						stepScore += pinsToAdd.value
						rollSteps.append(.init(
							index: rollSteps.count,
							displayValue: pinsToAdd.displayValue(rollIndex: -1),
							didFoul: false,
							isSecondary: true
						))
					}
				}

				accruedScore += stepScore
				steps.append(.init(
					index: steps.count,
					rolls: padRolls(rollSteps, displayValue: index < rolls.endIndex - 1 ? "-" : nil),
					score: max(accruedScore, 0)
				))
				pinsDown = []
				rollSteps = []
			} else {
				// Append the value of pins downed this roll
				rollSteps.append(.init(
					index: rollSteps.count,
					displayValue: roll.roll.pinsDowned.displayValue(rollIndex: roll.rollIndex),
					didFoul: roll.roll.didFoul,
					isSecondary: false
				))

				// For the last roll of a frame, add the total value of pins downed this frame to the score
				if index == rolls.endIndex - 1 || rolls[index + 1].frameIndex != roll.frameIndex {
					accruedScore += pinsDown.value
					steps.append(.init(
						index: steps.count,
						rolls: padRolls(rollSteps, displayValue: index < rolls.endIndex - 1 ? "-" : nil),
						score: max(accruedScore, 0)
					))
					pinsDown = []
					rollSteps = []
				}
			}
		}

		var stepScore = 0
		var initialRollIndex = 0

		// Calculate the final frame separately
		let ballsRolledInFinalFrame = rolls.filter { Frame.isLast($0.frameIndex) }.count
		var pinsDownedOnce = false
		for roll in rolls where Frame.isLast(roll.frameIndex) {
			accruedScore -= (roll.roll.didFoul ? 1 : 0) * Game.FOUL_PENALTY
			pinsDown.formUnion(roll.roll.pinsDowned)

			// When all the pins have been cleared
			if pinsDown.count == 5 && !(!pinsDownedOnce && ballsRolledInFinalFrame == 3 && Frame.isLastRoll(roll.rollIndex)) {
				// Append a roll with the full deck cleared
				pinsDownedOnce = true
				rollSteps.append(.init(
					index: rollSteps.count,
					displayValue: pinsDown.displayValue(rollIndex: roll.rollIndex - initialRollIndex),
					didFoul: roll.roll.didFoul,
					isSecondary: false
				))

				stepScore += pinsDown.value
				pinsDown = []
				initialRollIndex = roll.rollIndex + 1
			} else {
				// Append the value of pins downed this roll
				rollSteps.append(.init(
					index: rollSteps.count,
					displayValue: roll.roll.pinsDowned.displayValue(rollIndex: roll.rollIndex - initialRollIndex),
					didFoul: roll.roll.didFoul,
					isSecondary: false
				))

				// Update the score after the last roll in the game
				if roll == rolls.last {
					stepScore += pinsDown.value
				}
			}
		}

		// Append the final frame steps to the output if it exists
		if !rollSteps.isEmpty {
			accruedScore += stepScore
			steps.append(.init(
				index: steps.count,
				rolls: padRolls(rollSteps, displayValue: nil),
				score: max(accruedScore, 0)
			))
		}

		return padFrames(steps)
	}
}
