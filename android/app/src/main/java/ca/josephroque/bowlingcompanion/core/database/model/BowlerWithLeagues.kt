package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class BowlerWithLeagues(
	@Embedded val bowler: BowlerEntity,
	@Relation(
		parentColumn = "id",
		entityColumn = "bowlerId",
	)
	val leagues: List<LeagueEntity>
)