package ca.josephroque.bowlingcompanion.core.model

enum class GameScoringMethod {
	MANUAL,
	BY_FRAME,
}

fun String?.asGameScoringMethod() = when (this) {
	null -> null
	else -> GameScoringMethod.values()
		.firstOrNull { it.name == this }
}