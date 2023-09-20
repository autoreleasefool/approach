import ScoreKeeperModelsLibrary

struct SequencedRoll {
	let frameIndex: Int
	let rollIndex: Int
	let roll: ScoreKeeper.Roll
}

public class ScoreKeeper {
	public init() {}

	private func padRolls(_ rolls: [ScoredRoll], displayValue: String?) -> [ScoredRoll] {
		rolls + Frame.rollIndices(after: rolls.endIndex - 1)
			.map { .init(index: $0, displayValue: displayValue, didFoul: false) }
	}

	private func padFrames(_ frames: [ScoredFrame]) -> [ScoredFrame] {
		frames + Game.frameIndices(after: frames.endIndex - 1)
			.map { .init(index: $0, rolls: padRolls([], displayValue: nil), score: nil)}
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
					rolls.append(.init(frameIndex: frameIndex, rollIndex: 0, roll: .init(pinsDowned: [], didFoul: false)))
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
		}

		// Ensure there is at least one roll in the game, or return a nil score
		guard !rolls.isEmpty else {
			return frames.enumerated().map { index, _ in
					.init(
						index: index,
						rolls: Frame.ROLL_INDICES.map { rollIndex in
								.init(index: rollIndex, displayValue: nil, didFoul: false)
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
					didFoul: roll.roll.didFoul
				))

				var stepScore = pinsDown.value
				let rollsToAdd = 2 - roll.rollIndex

				// If the roll was a spare or a strike, add the scores of the subsequent rolls to this roll
				if rollsToAdd > 0 {
					for addedRollIndex in 1...rollsToAdd where index + addedRollIndex < rolls.endIndex {
						let pinsToAdd = rolls[index + addedRollIndex].roll.pinsDowned
						stepScore += pinsToAdd.value
						rollSteps.append(.init(
							index: rollSteps.count,
							displayValue: pinsToAdd.displayValue(rollIndex: -1),
							didFoul: false
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
					didFoul: roll.roll.didFoul
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
					didFoul: roll.roll.didFoul
				))

				stepScore += pinsDown.value
				pinsDown = []
				initialRollIndex = roll.rollIndex + 1
			} else {
				// Append the value of pins downed this roll
				rollSteps.append(.init(
					index: rollSteps.count,
					displayValue: roll.roll.pinsDowned.displayValue(rollIndex: roll.rollIndex - initialRollIndex),
					didFoul: roll.roll.didFoul
				))

				if Frame.isLastRoll(roll.rollIndex) {
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
