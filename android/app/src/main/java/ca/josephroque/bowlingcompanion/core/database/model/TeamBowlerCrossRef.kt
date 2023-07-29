package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Entity
import java.util.UUID

@Entity(
	tableName = "team_bowler",
	primaryKeys = ["teamId", "bowlerId"],
)
data class TeamBowlerCrossRef(
	val teamId: UUID,
	val bowlerId: UUID,
)