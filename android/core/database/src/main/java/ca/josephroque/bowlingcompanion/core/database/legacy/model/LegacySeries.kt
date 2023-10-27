package ca.josephroque.bowlingcompanion.core.database.legacy.model

import java.util.Date

data class LegacySeries(
	val id: Long,
	val date: Date,
	val numberOfGames: Int,
	val leagueId: Long,
)