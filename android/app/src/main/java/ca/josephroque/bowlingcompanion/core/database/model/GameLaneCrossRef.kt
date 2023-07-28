package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Entity
import java.util.UUID

@Entity(
	tableName = "game_lanes",
	primaryKeys = ["gameId", "laneId"],
)
data class GameLaneCrossRef(
	val gameId: UUID,
	val laneId: UUID,
)