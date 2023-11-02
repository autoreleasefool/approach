package ca.josephroque.bowlingcompanion.core.model

import java.util.UUID

data class ScoringRoll(
	val index: Int,
	val display: String?,
	val didFoul: Boolean,
)

data class ScoringFrame(
	val index: Int,
	val rolls: List<ScoringRoll>,
	val score: Int?
) {
	val display: String?
		get() = score?.toString()
}

fun List<ScoringFrame>.gameScore(): Int? =
	reversed().firstNotNullOf { it.score }

data class ScoringGame(
	val id: UUID,
	val index: Int,
	val frames: List<ScoringFrame>,
) {
	val score: Int?
		get() = frames.gameScore()
}