package ca.josephroque.bowlingcompanion.core.scoring

import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.model.ScoreableFrame

data class ScoreKeeperInput(
	val rolls: List<List<Roll>>,
) {
	data class Roll(
		val frameIndex: Int,
		val rollIndex: Int,
		val pinsDowned: Set<Pin>,
		val didFoul: Boolean,
	) {
		companion object {
			fun fromBitString(frameIndex: Int, rollIndex: Int, bitString: String?): Roll {
				bitString ?: return Roll(frameIndex, rollIndex, pinsDowned = setOf(), didFoul = false)

				val didFoul = bitString.first() != '0'
				val pinsDowned = bitString.drop(1).mapIndexedNotNull { index, bit ->
					if (bit == '0') null else Pin.values()[index]
				}

				return Roll(frameIndex, rollIndex, pinsDowned.toSet(), didFoul)
			}
		}
	}

	companion object {
		fun fromFrames(frames: List<ScoreableFrame>): ScoreKeeperInput = ScoreKeeperInput(
			rolls = frames.map { frame ->
				arrayOf(frame.roll0, frame.roll1, frame.roll2).mapIndexed { index, roll ->
					Roll.fromBitString(frame.index, index, roll)
				}
			}
		)
	}
}

internal data class ScoreKeeperState(
	val input: ScoreKeeperInput,
	val lastValidFrameIndex: Int,
	var accruingScore: Int,
)