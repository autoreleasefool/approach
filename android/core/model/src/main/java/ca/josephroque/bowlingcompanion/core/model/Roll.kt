package ca.josephroque.bowlingcompanion.core.model

object Roll {
	fun isLastRoll(index: Int): Boolean =
		index == Frame.NumberOfRolls - 1
}