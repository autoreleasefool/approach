package ca.josephroque.bowlingcompanion.core.model

enum class ExcludeFromStatistics {
	INCLUDE,
	EXCLUDE,
	;

	val next: ExcludeFromStatistics
		get() = when (this) {
			INCLUDE -> EXCLUDE
			EXCLUDE -> INCLUDE
		}
}