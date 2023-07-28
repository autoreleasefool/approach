package ca.josephroque.bowlingcompanion.core.database.relationship

import androidx.room.Embedded
import androidx.room.Relation
import ca.josephroque.bowlingcompanion.core.database.model.BowlerEntity
import ca.josephroque.bowlingcompanion.core.database.model.LeagueEntity

data class BowlerWithLeagues(
	@Embedded val bowler: BowlerEntity,
	@Relation(
		parentColumn = "id",
		entityColumn = "bowlerId",
	)
	val leagues: List<LeagueEntity>
)