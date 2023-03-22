import Dependencies
import PersistenceServiceInterface
import ScoringServiceInterface
import SharedModelsLibrary

struct SequencedRoll {
	let frameIndex: Int
	let rollIndex: Int
	let roll: Frame.Roll
}

extension ScoringService: DependencyKey {
	public static var liveValue: Self = {
		@Dependency(\.persistenceService) var persistenceService: PersistenceService

		@Sendable func calculateScore(for frames: [Frame]) async throws -> [ScoreStep] {
			// The output is a step indicating the score and status of the game after each frame
			var steps: [ScoreStep] = []

			let rolls: [SequencedRoll] = frames.reduce(into: []) { rolls, frame in
				// Must be at least 1 roll or we skip the frame
				guard !frame.rolls.isEmpty else { return }

				// Last roll must be from previous frame (previous index is ordinal minus 2)
				if let lastRoll = rolls.last, lastRoll.frameIndex != frame.ordinal - 2 { return }

				rolls.append(contentsOf: frame.rolls.enumerated().map {
					.init(frameIndex: frame.ordinal - 1, rollIndex: $0.offset, roll: $0.element)
				})
			}

			// Ensure there is at least one roll in the game, or return a nil score
			guard !rolls.isEmpty else {
				return frames.map { _ in .init(rolls: [], score: nil) }
			}

			// Cumulative set of pins downed in the frame
			var pinsDown: Set<Pin> = []
			var rollSteps: [ScoreStep.RollStep] = []

			for (index, roll) in rolls.enumerated() {
				// Accumulate the downed pins. Assume this is reset appropriately between frames below
				pinsDown.formUnion(roll.roll.pinsDowned)

				// When all the pins have been cleared
				if pinsDown.count == 5 {
					rollSteps.append(.init(display: pinsDown.displayValue(rollIndex: roll.rollIndex), didFoul: roll.roll.didFoul))
					var stepScore = pinsDown.value
					let rollsToAdd = 2 - roll.rollIndex

					if rollsToAdd > 0 {
						for addedRollIndex in 0..<rollsToAdd where index + addedRollIndex < rolls.endIndex {
							let pinsToAdd = rolls[index + addedRollIndex].roll.pinsDowned
							stepScore += pinsToAdd.value
							rollSteps.append(.init(display: pinsToAdd.displayValue(rollIndex: -1), didFoul: false))
						}
					}

					steps.append(.init(rolls: rollSteps, score: (steps.last?.score ?? 0) + stepScore))
					pinsDown = []
					rollSteps = []
				} else {
					// Append the value of pins downed this roll
					rollSteps.append(.init(
						display: roll.roll.pinsDowned.displayValue(rollIndex: roll.rollIndex),
						didFoul: roll.roll.didFoul
					))

					// For the last roll of a frame, add the total value of pins downed this frame to the score
					if index == rolls.endIndex || rolls[index + 1].frameIndex != roll.frameIndex {
						steps.append(.init(rolls: rollSteps, score: (steps.last?.score ?? 0) + pinsDown.value))
						pinsDown = []
						rollSteps = []
					}
				}
			}

			return steps
		}

		return Self(
			calculateScoreForGame: { game in
				let frames = try await persistenceService.fetchFrames(.init(filter: .game(game), ordering: .byOrdinal))
				let steps = try await calculateScore(for: frames)
				return steps.last?.score
			},
			calculateScoreForFrames: { frames in
				let steps = try await calculateScore(for: frames)
				return steps.last?.score
			},
			calculateScoreForFramesWithSteps: calculateScore(for:)
		)
	}()
}
