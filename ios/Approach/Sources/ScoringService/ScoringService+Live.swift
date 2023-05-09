import Dependencies
import ModelsLibrary
import ScoringServiceInterface

struct SequencedRoll {
	let frameIndex: Int
	let rollIndex: Int
	let roll: Frame.Roll
}

extension ScoringService: DependencyKey {
	public static var liveValue: Self = {
		@Sendable func padRolls(_ rolls: [ScoreStep.RollStep]) -> [ScoreStep.RollStep] {
			rolls + (0..<(Frame.NUMBER_OF_ROLLS - rolls.count))
				.map { .init(index: rolls.count + $0, display: nil, didFoul: false) }
		}

		@Sendable func padSteps(_ steps: [ScoreStep]) -> [ScoreStep] {
			steps + (0..<(Game.NUMBER_OF_FRAMES - steps.count))
				.map { .init(index: steps.count + $0, rolls: padRolls([]), score: nil)}
		}

		@Sendable func calculateScore(for frames: [[Frame.OrderedRoll]]) async -> [ScoreStep] {
			// The output is a step indicating the score and status of the game after each frame
			var steps: [ScoreStep] = []

			let rolls: [SequencedRoll] = frames.enumerated().reduce(into: []) { rolls, indexedFrame in
				let (index, frame) = indexedFrame
				// Must be at least 1 roll or we skip the frame
				guard !frame.isEmpty else { return }

				// Last roll must be from previous frame
				if let lastRoll = rolls.last, lastRoll.frameIndex != index - 1 { return }

				rolls.append(contentsOf: frame.enumerated().map {
					.init(frameIndex: index, rollIndex: $0.offset, roll: $0.element.roll)
				})
			}

			// Ensure there is at least one roll in the game, or return a nil score
			guard !rolls.isEmpty else {
				return frames.enumerated().map { index, _ in
					.init(
						index: index,
						rolls: (0..<Frame.NUMBER_OF_ROLLS).map { rollIndex in
							.init(index: rollIndex, display: nil, didFoul: false)
						},
						score: nil
					)
				}
			}

			// Cumulative set of pins downed in the frame
			var pinsDown: Set<Pin> = []
			// Each roll to be displayed in the final output
			var rollSteps: [ScoreStep.RollStep] = []
			// Fouls accumulated in a single frame
			var penalties = 0

			// Calculate all except the final frame
			for (index, roll) in rolls.enumerated() where roll.frameIndex < Game.NUMBER_OF_FRAMES - 1 {
				penalties += roll.roll.didFoul ? 1 : 0

				// Accumulate the downed pins. Assume this is reset appropriately between frames below
				pinsDown.formUnion(roll.roll.pinsDowned)

				// When all the pins have been cleared
				if pinsDown.count == 5 && roll.rollIndex < Frame.NUMBER_OF_ROLLS - 1 {
					// Append a roll with the full deck cleared
					rollSteps.append(.init(
						index: rollSteps.count,
						display: pinsDown.displayValue(rollIndex: roll.rollIndex),
						didFoul: roll.roll.didFoul
					))

					var stepScore = pinsDown.value
					let rollsToAdd = 2 - roll.rollIndex

					// If the roll was a spare or a strike, add the scores of the subsequent rolls to this roll
					if rollsToAdd > 0 {
						for addedRollIndex in 1...rollsToAdd where index + addedRollIndex < rolls.endIndex {
							let pinsToAdd = rolls[index + addedRollIndex].roll.pinsDowned
							stepScore += pinsToAdd.value
							rollSteps.append(.init(index: rollSteps.count, display: pinsToAdd.displayValue(rollIndex: -1), didFoul: false))
						}
					}

					steps.append(.init(
						index: steps.count,
						rolls: padRolls(rollSteps),
						score: max((steps.last?.score ?? 0) + stepScore - (penalties * Frame.Roll.FOUL_PENALTY), 0)
					))
					penalties = 0
					pinsDown = []
					rollSteps = []
				} else {
					// Append the value of pins downed this roll
					rollSteps.append(.init(
						index: rollSteps.count,
						display: roll.roll.pinsDowned.displayValue(rollIndex: roll.rollIndex),
						didFoul: roll.roll.didFoul
					))

					// For the last roll of a frame, add the total value of pins downed this frame to the score
					if index == rolls.endIndex - 1 || rolls[index + 1].frameIndex != roll.frameIndex {
						steps.append(.init(
							index: steps.count,
							rolls: padRolls(rollSteps),
							score: max((steps.last?.score ?? 0) + pinsDown.value - (penalties * Frame.Roll.FOUL_PENALTY), 0)
						))
						penalties = 0
						pinsDown = []
						rollSteps = []
					}
				}
			}

			var stepScore = 0
			var initialRollIndex = 0

			// Calculate the final frame separately
			for (index, roll) in rolls.enumerated() where roll.frameIndex == Game.NUMBER_OF_FRAMES - 1 {
				penalties += roll.roll.didFoul ? 1 : 0
				pinsDown.formUnion(roll.roll.pinsDowned)

				// When all the pins have been cleared
				if pinsDown.count == 5 {
					// Append a roll with the full deck cleared
					rollSteps.append(.init(
						index: rollSteps.count,
						display: pinsDown.displayValue(rollIndex: roll.rollIndex - initialRollIndex),
						didFoul: roll.roll.didFoul
					))

					stepScore += pinsDown.value
					pinsDown = []
					initialRollIndex = roll.rollIndex + 1
				} else {
					// Append the value of pins downed this roll
					rollSteps.append(.init(
						index: rollSteps.count,
						display: roll.roll.pinsDowned.displayValue(rollIndex: roll.rollIndex - initialRollIndex),
						didFoul: roll.roll.didFoul
					))

					if roll.rollIndex == Frame.NUMBER_OF_ROLLS - 1 {
						stepScore += pinsDown.value
					}
				}
			}

			// Append the final frame steps to the output if it exists
			if !rollSteps.isEmpty {
				steps.append(.init(
					index: steps.count,
					rolls: padRolls(rollSteps),
					score: max((steps.last?.score ?? 0) + stepScore - (penalties * Frame.Roll.FOUL_PENALTY), 0)
				))
			}

			return padSteps(steps)
		}

		return Self(
			calculateScoreForFrames: { frames in
				let steps = await calculateScore(for: frames)
				return steps.reversed().first(where: { $0.score != nil })?.score
			},
			calculateScoreForFramesWithSteps: calculateScore(for:)
		)
	}()
}
