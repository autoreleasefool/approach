package ca.josephroque.bowlingcompanion.core.model

enum class LeagueRecurrence {
	REPEATING,
	ONCE,
}

fun String?.asLeagueRecurrence() = when (this) {
	null -> null
	else -> LeagueRecurrence.values()
		.firstOrNull { it.name == this }
}