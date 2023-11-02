package ca.josephroque.bowlingcompanion.core.model

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
	val roll0: String?,
	val roll1: String?,
	val roll2: String?,
)