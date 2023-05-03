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
				.map { .init(index: rolls.count + $0, display: "", didFoul: false) }
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
							.init(index: rollIndex, display: "", didFoul: false)
						},
						score: nil
					)
				}
			}

			// Cumulative set of pins downed in the frame
			var pinsDown: Set<Pin> = []
			var rollSteps: [ScoreStep.RollStep] = []

			for (index, roll) in rolls.enumerated() {
				// Accumulate the downed pins. Assume this is reset appropriately between frames below
				pinsDown.formUnion(roll.roll.pinsDowned)

				// When all the pins have been cleared
				if pinsDown.count == 5 {
					rollSteps.append(.init(
						index: rollSteps.count,
						display: pinsDown.displayValue(rollIndex: roll.rollIndex),
						didFoul: roll.roll.didFoul
					))
					var stepScore = pinsDown.value
					let rollsToAdd = 2 - roll.rollIndex

					if rollsToAdd > 0 {
						for addedRollIndex in 0..<rollsToAdd where index + addedRollIndex < rolls.endIndex {
							let pinsToAdd = rolls[index + addedRollIndex].roll.pinsDowned
							stepScore += pinsToAdd.value
							rollSteps.append(.init(index: rollSteps.count, display: pinsToAdd.displayValue(rollIndex: -1), didFoul: false))
						}
					}

					steps.append(.init(index: steps.count, rolls: padRolls(rollSteps), score: (steps.last?.score ?? 0) + stepScore))
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
							score: (steps.last?.score ?? 0) + pinsDown.value)
						)
						pinsDown = []
						rollSteps = []
					}
				}
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
