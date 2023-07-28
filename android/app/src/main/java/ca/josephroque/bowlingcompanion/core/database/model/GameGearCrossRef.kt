package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Entity
import java.util.UUID

@Entity(
	tableName = "game_gear",
	primaryKeys = ["gameId", "gearId"],
)
data class GameGearCrossRef(
	val gameId: UUID,
	val gearId: UUID,
)