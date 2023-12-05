package ca.josephroque.bowlingcompanion.core.model

enum class AnalyticsOptInStatus {
	OPTED_IN,
	OPTED_OUT,
	;

	val next: AnalyticsOptInStatus
		get() = when (this) {
			OPTED_IN -> OPTED_OUT
			OPTED_OUT -> OPTED_IN
		}
}