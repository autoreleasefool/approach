package ca.josephroque.bowlingcompanion.core.scoring

import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.model.ScoreableFrame
import ca.josephroque.bowlingcompanion.core.model.arePinsCleared
import ca.josephroque.bowlingcompanion.core.model.pinCount

data class ScoreKeeperInput(
	val rolls: List<List<Roll>>,
) {
	data class Roll(
		val frameIndex: Int,
		val rollIndex: Int,
		val pinsDowned: Set<Pin>,
		val didFoul: Boolean,
	)

	companion object {
		fun fromFrames(frames: List<ScoreableFrame>): ScoreKeeperInput = ScoreKeeperInput(
			rolls = frames.map { frame ->
				frame.rolls.mapIndexed { index, roll ->
					Roll(
						frame.index,
						index,
						roll.pinsDowned,
						roll.didFoul,
					)
				}
			},
		)
	}
}

fun List<ScoreKeeperInput.Roll>.pinCount() = fold(setOf<Pin>()) { acc, roll -> acc + roll.pinsDowned }
	.pinCount()
fun List<ScoreKeeperInput.Roll>.arePinsCleared() = fold(setOf<Pin>()) { acc, roll -> acc + roll.pinsDowned }
	.arePinsCleared()

internal data class ScoreKeeperState(
	val input: ScoreKeeperInput,
	val lastValidFrameIndex: Int,
	var accruingScore: Int,
)
