package ca.josephroque.bowlingcompanion.core.model

enum class ExcludeFromStatistics {
	INCLUDE,
	EXCLUDE,
}

fun String?.asExcludeFromStatistics() = when (this) {
	null -> ExcludeFromStatistics.INCLUDE
	else -> ExcludeFromStatistics.values()
		.firstOrNull { it.name == this }
		?: ExcludeFromStatistics.INCLUDE
}