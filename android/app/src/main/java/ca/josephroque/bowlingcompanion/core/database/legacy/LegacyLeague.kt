package ca.josephroque.bowlingcompanion.core.database.legacy

class LegacyLeague {
	companion object {
		@Deprecated("Replaced with PRACTICE_LEAGUE_NAME")
		const val OPEN_LEAGUE_NAME = "Open"
		const val PRACTICE_LEAGUE_NAME = "Practice"
	}
}