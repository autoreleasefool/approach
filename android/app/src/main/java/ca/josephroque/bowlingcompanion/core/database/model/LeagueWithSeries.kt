package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class LeagueWithSeries(
	@Embedded val league: LeagueEntity,
	@Relation(
		parentColumn = "id",
		entityColumn = "leagueId",
	)
	val series: List<SeriesEntity>
)