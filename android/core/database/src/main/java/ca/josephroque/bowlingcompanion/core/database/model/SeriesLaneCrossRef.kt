package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.UUID

@Entity(
	tableName = "series_lane",
	primaryKeys = ["series_id", "lane_id"],
	foreignKeys = [
		ForeignKey(
			entity = SeriesEntity::class,
			parentColumns = ["id"],
			childColumns = ["series_id"],
			onUpdate = ForeignKey.CASCADE,
			onDelete = ForeignKey.CASCADE,
		),
		ForeignKey(
			entity = LaneEntity::class,
			parentColumns = ["id"],
			childColumns = ["lane_id"],
			onUpdate = ForeignKey.CASCADE,
			onDelete = ForeignKey.CASCADE,
		),
	],
	indices = [
		Index("series_id", "lane_id"),
	],
)
data class SeriesLaneCrossRef(
	@ColumnInfo(name = "series_id", index = true) val seriesId: UUID,
	@ColumnInfo(name = "lane_id", index = true) val laneId: UUID,
)
