package ca.josephroque.bowlingcompanion.feature.gameseditor.utils

import ca.josephroque.bowlingcompanion.core.model.Frame
import ca.josephroque.bowlingcompanion.core.model.FrameEdit
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
	ballRolled: FrameEdit.Gear
) {
	val frame = this[frameIndex].ensureRollExists(upTo = rollIndex)
	val rolls = frame.rolls.toMutableList()
	if (rolls[rollIndex].bowlingBall?.id == ballRolled.id) {
		rolls[rollIndex] = rolls[rollIndex].copy(bowlingBall = null)
	} else {
		rolls[rollIndex] = rolls[rollIndex].copy(bowlingBall = ballRolled)
	}

	this[frameIndex] = frame.copy(rolls = rolls)
}

fun List<FrameEdit>.doesRollExistInFrame(frameIndex: Int, rollIndex: Int): Boolean {
	if (frameIndex >= this.size) return false
	return rollIndex < this[frameIndex].rolls.size
}

fun MutableList<FrameEdit>.guaranteeRollExists(frameIndex: Int, rollIndex: Int) {
	this[frameIndex] = this[frameIndex].ensureRollExists(upTo = rollIndex)
}

fun FrameEdit.ensureRollExists(upTo: Int): FrameEdit {
	if (this.rolls.size == upTo + 1) return this

	val rolls = this.rolls.toMutableList()
	for (rollIndex in this.rolls.size..upTo) {
		rolls.add(FrameEdit.Roll(index = rollIndex, pinsDowned = emptySet(), didFoul = false, bowlingBall = null))
	}
	return this.copy(rolls = rolls)
}