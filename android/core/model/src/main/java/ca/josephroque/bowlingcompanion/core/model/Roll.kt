package ca.josephroque.bowlingcompanion.core.model

object Roll {
	fun isLastRoll(index: Int): Boolean = index == Frame.NUMBER_OF_ROLLS - 1
}
