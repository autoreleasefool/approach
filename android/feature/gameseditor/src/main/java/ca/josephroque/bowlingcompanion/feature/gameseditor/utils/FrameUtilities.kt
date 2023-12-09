package ca.josephroque.bowlingcompanion.feature.gameseditor.utils

import ca.josephroque.bowlingcompanion.core.model.Frame
import ca.josephroque.bowlingcompanion.core.model.FrameEdit
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.model.arePinsCleared

fun MutableList<FrameEdit>.setPinsDowned(
	frameIndex: Int,
	rollIndex: Int,
	pinsDowned: Set<Pin>,
) {
	val frame = this[frameIndex].ensureRollExists(upTo = rollIndex)
	val rolls = frame.rolls.toMutableList()
	rolls[rollIndex] = rolls[rollIndex].copy(pinsDowned = pinsDowned)
	rolls.subList(rollIndex + 1, rolls.size).forEachIndexed { index, roll ->
		rolls[index + rollIndex + 1] = roll.copy(pinsDowned = roll.pinsDowned.subtract(pinsDowned))
	}
	this[frameIndex] = frame.copy(rolls = rolls)
}

fun MutableList<FrameEdit>.setDidFoul(
	frameIndex: Int,
	rollIndex: Int,
	didFoul: Boolean,
) {
	val frame = this[frameIndex].ensureRollExists(upTo = rollIndex)
	val rolls = frame.rolls.toMutableList()
	rolls[rollIndex] = rolls[rollIndex].copy(didFoul = didFoul)
	this[frameIndex] = frame.copy(rolls = rolls)
}

fun MutableList<FrameEdit>.setBallRolled(
	frameIndex: Int,
	rollIndex: Int,
	ballRolled: GearListItem,
) {
	val frame = this[frameIndex].ensureRollExists(upTo = rollIndex)
	val rolls = frame.rolls.toMutableList()
	if (rolls[rollIndex].bowlingBall?.id == ballRolled.id) {
		rolls[rollIndex] = rolls[rollIndex].copy(bowlingBall = null)
	} else {
		rolls[rollIndex] = rolls[rollIndex].copy(bowlingBall = FrameEdit.Gear(id = ballRolled.id, name = ballRolled.name, kind = ballRolled.kind))
	}

	this[frameIndex] = frame.copy(rolls = rolls)
}

fun MutableList<FrameEdit>.ensureFramesExist(upTo: Int) {
	for (frameIndex in 0..upTo) {
		val numberOfRolls = this[frameIndex].rolls.size
		if (numberOfRolls == Frame.NumberOfRolls) continue
		val lastRollIndex = numberOfRolls - 1
		if (!this[frameIndex].deckForRoll(lastRollIndex).arePinsCleared()) {
			this[frameIndex] = this[frameIndex].ensureRollExists(upTo = Frame.NumberOfRolls - 1)
		}
	}
}

fun FrameEdit.ensureRollExists(upTo: Int): FrameEdit {
	val rolls = this.rolls.toMutableList()
	for (rollIndex in this.rolls.size..upTo) {
		rolls.add(FrameEdit.Roll(index = rollIndex, pinsDowned = emptySet(), didFoul = false, bowlingBall = null))
	}
	return this.copy(rolls = rolls)
}