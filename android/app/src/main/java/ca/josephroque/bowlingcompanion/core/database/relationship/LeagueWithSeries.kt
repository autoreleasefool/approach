package ca.josephroque.bowlingcompanion.core.database.relationship

import androidx.room.Embedded
import androidx.room.Relation
import ca.josephroque.bowlingcompanion.core.database.model.LeagueEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesEntity

data class LeagueWithSeries(
	@Embedded val league: LeagueEntity,
	@Relation(
		parentColumn = "id",
		entityColumn = "leagueId",
	)
	val series: List<SeriesEntity>
)