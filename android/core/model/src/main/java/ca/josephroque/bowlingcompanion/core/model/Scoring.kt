package ca.josephroque.bowlingcompanion.core.model

import java.util.UUID

data class ScoringRoll(
	val index: Int,
	val display: String?,
	val didFoul: Boolean,
	val isSecondaryValue: Boolean,
)

fun ScoringRoll.isLastRoll(): Boolean = Roll.isLastRoll(index)

fun ScoringRoll.isFirstRoll(): Boolean = index == 0

data class ScoringFrame(
	val index: Int,
	val rolls: List<ScoringRoll>,
	val score: Int?,
) {
	val display: String?
		get() = score?.toString()
}

fun ScoringFrame.isFirstFrame(): Boolean = index == 0

fun List<ScoringFrame>.gameScore(): Int? = reversed().firstNotNullOfOrNull { it.score }

data class ScoringGame(
	val id: UUID,
	val index: Int,
	val frames: List<ScoringFrame>,
) {
	val score: Int?
		get() = frames.gameScore()
}
