package ca.josephroque.bowlingcompanion.core.model

import kotlinx.datetime.LocalDate
import java.util.UUID

object Frame {
	const val NumberOfRolls = 3
	val RollIndices = 0..<NumberOfRolls

	fun isLastFrame(index: Int): Boolean =
		index == Game.NumberOfFrames - 1

	fun rollIndicesAfter(after: Int): IntRange =
		(after + 1)..<NumberOfRolls
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
}

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
	)

	val hasUntouchedRoll: Boolean =
		firstUntouchedRoll != null

	val firstUntouchedRoll: Int?
		get() {
			if (rolls.size >= Frame.NumberOfRolls) return null
			return if (Frame.isLastFrame(properties.index))
				rolls.lastIndex + 1
			else
				if (deckForRoll(rolls.lastIndex).arePinsCleared()) null else rolls.lastIndex + 1
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

	fun deckForRoll(rollIndex: Int): Set<Pin> =
		rolls.takeWhile { it.index <= rollIndex }.fold(emptySet()) { acc, roll ->
			val baseAcc = if (Frame.isLastFrame(properties.index) && acc.size == 5) emptySet() else acc
			baseAcc + roll.pinsDowned
		}
}

fun List<FrameEdit>.nextIndexToRecord(): Int {
	val lastFrameWithRolls = withIndex().lastOrNull { it.value.rolls.isNotEmpty() } ?: return 0
	return if (lastFrameWithRolls.value.hasUntouchedRoll)
		lastFrameWithRolls.index
	else
		minOf(lastFrameWithRolls.index + 1, size - 1)
}

fun List<FrameEdit>.nextFrameToRecord(): FrameEdit = this[nextIndexToRecord()]