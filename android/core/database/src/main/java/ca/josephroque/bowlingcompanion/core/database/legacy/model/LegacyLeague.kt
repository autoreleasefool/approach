package ca.josephroque.bowlingcompanion.core.database.legacy.model

data class LegacyLeague(
	val id: Long,
	val name: String,
	val isEvent: Boolean,
	val gamesPerSeries: Int,
	val additionalPinFall: Int,
	val additionalGames: Int,
	val bowlerId: Long,
) {
	companion object {
		@Deprecated("Replaced with PRACTICE_LEAGUE_NAME")
		const val OPEN_LEAGUE_NAME = "Open"
		const val PRACTICE_LEAGUE_NAME = "Practice"
	}
}
