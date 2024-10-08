package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.LaneID

@Entity(
	tableName = "game_lanes",
	primaryKeys = ["game_id", "lane_id"],
	foreignKeys = [
		ForeignKey(
			entity = GameEntity::class,
			parentColumns = ["id"],
			childColumns = ["game_id"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE,
		),
		ForeignKey(
			entity = LaneEntity::class,
			parentColumns = ["id"],
			childColumns = ["lane_id"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE,
		),
	],
	indices = [
		Index("game_id", "lane_id"),
	],
)
data class GameLaneCrossRef(
	@ColumnInfo(name = "game_id", index = true) val gameId: GameID,
	@ColumnInfo(name = "lane_id", index = true) val laneId: LaneID,
)
