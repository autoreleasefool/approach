package ca.josephroque.bowlingcompanion.core.model

enum class ExcludeFromStatistics {
	INCLUDE,
	EXCLUDE,
}

fun ExcludeFromStatistics.toggle(): ExcludeFromStatistics = when (this) {
	ExcludeFromStatistics.INCLUDE -> ExcludeFromStatistics.EXCLUDE
	ExcludeFromStatistics.EXCLUDE -> ExcludeFromStatistics.INCLUDE
}