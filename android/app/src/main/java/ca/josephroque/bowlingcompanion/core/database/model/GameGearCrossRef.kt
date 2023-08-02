package ca.josephroque.bowlingcompanion.core.database.model

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.UUID

@Entity(
	tableName = "game_gear",
	primaryKeys = ["game_id", "gear_id"],
	foreignKeys = [
		ForeignKey(
			entity = GameEntity::class,
			parentColumns = ["id"],
			childColumns = ["game_id"],
			onUpdate = ForeignKey.CASCADE,
			onDelete = ForeignKey.CASCADE,
		),
		ForeignKey(
			entity = GearEntity::class,
			parentColumns = ["id"],
			childColumns = ["gear_id"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE,
		),
	],
	indices = [
		Index("game_id", "gear_id"),
	],
)
@Immutable
data class GameGearCrossRef(
	@ColumnInfo(name = "game_id", index = true) val gameId: UUID,
	@ColumnInfo(name = "gear_id", index = true) val gearId: UUID,
)