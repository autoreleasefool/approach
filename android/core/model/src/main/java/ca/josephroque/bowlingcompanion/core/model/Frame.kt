package ca.josephroque.bowlingcompanion.core.model

import java.util.UUID
import kotlinx.datetime.LocalDate

object Frame {
	const val NUMBER_OF_ROLLS = 3
	val RollIndices = 0..<NUMBER_OF_ROLLS

	fun isLastFrame(index: Int): Boolean = index == Game.NUMBER_OF_FRAMES - 1

	fun rollIndicesAfter(after: Int): IntRange = (after + 1)..<NUMBER_OF_ROLLS
}

data class ScoreableFrame(
	val index: Int,
	val rolls: List<Roll>,
) {
	data class Roll(
		val index: Int,
		val pinsDowned: Set<Pin>,
		val didFoul: Boolean,
	)
}

data class TrackableFrame(
	val seriesId: UUID,
	val gameId: UUID,
	val gameIndex: Int,
	val index: Int,
	val rolls: List<Roll>,
	val date: LocalDate,
) {
	data class Roll(
		val index: Int,
		val pinsDowned: Set<Pin>,
		val didFoul: Boolean,
	)

	val totalRolls: Int
		get() {
			return if (Frame.isLastFrame(index)) {
				rolls.size
			} else {
				val deck = mutableSetOf<Pin>()
				rolls.takeWhile {
					if (deck.arePinsCleared()) return@takeWhile false
					deck += it.pinsDowned
					true
				}.size
			}
		}
}

data class FrameCreate(
	val gameId: UUID,
	val index: Int,
)

data class FrameEdit(
	val properties: Properties,
	val rolls: List<Roll>,
) {
	data class Properties(
		val gameId: UUID,
		val index: Int,
	)

	data class Roll(
		val index: Int,
		val pinsDowned: Set<Pin>,
		val didFoul: Boolean,
		val bowlingBall: Gear?,
	)

	data class Gear(
		val id: UUID,
		val name: String,
		val kind: GearKind,
		val avatar: Avatar,
	)

	val hasUntouchedRoll: Boolean =
		firstUntouchedRoll != null

	private val paddedRolls: List<Roll>
		get() {
			val rolls = rolls.toMutableList()
			if (rolls.size < Frame.NUMBER_OF_ROLLS) {
				rolls.add(
					Roll(index = rolls.size, pinsDowned = emptySet(), didFoul = false, bowlingBall = null),
				)
			}
			return rolls
		}

	val firstUntouchedRoll: Int?
		get() {
			if (rolls.size >= Frame.NUMBER_OF_ROLLS) return null
			return if (Frame.isLastFrame(properties.index)) {
				rolls.lastIndex + 1
			} else {
				if (deckForRoll(rolls.lastIndex).arePinsCleared()) null else rolls.lastIndex + 1
			}
		}

	val lastAccessibleRollIndex: Int
		get() {
			if (!Frame.isLastFrame(properties.index)) {
				val deck = mutableSetOf<Pin>()
				for (roll in rolls) {
					deck += roll.pinsDowned
					if (deck.arePinsCleared()) {
						return roll.index
					}
				}
			}

			return Frame.RollIndices.last
		}

	fun deckForRoll(rollIndex: Int): Set<Pin> {
		val rolls = if (Frame.isLastFrame(properties.index)) paddedRolls else this.rolls
		return rolls.takeWhile { it.index <= rollIndex }.fold(emptySet()) { acc, roll ->
			val baseAcc = if (Frame.isLastFrame(properties.index) && acc.size == 5) emptySet() else acc
			baseAcc + roll.pinsDowned
		}
	}
}

fun List<FrameEdit>.nextIndexToRecord(): Int {
	val lastFrameWithRolls = withIndex().lastOrNull { it.value.rolls.isNotEmpty() } ?: return 0
	return if (lastFrameWithRolls.value.hasUntouchedRoll) {
		lastFrameWithRolls.index
	} else {
		minOf(lastFrameWithRolls.index + 1, size - 1)
	}
}

fun List<FrameEdit>.nextFrameToRecord(): FrameEdit = this[nextIndexToRecord()]
