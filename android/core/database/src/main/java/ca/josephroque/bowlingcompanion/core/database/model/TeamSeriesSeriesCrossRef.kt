package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.UUID

@Entity(
	tableName = "team_series_series",
	primaryKeys = ["team_series_id", "series_id"],
	foreignKeys = [
		ForeignKey(
			entity = TeamSeriesEntity::class,
			parentColumns = ["id"],
			childColumns = ["team_series_id"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE,
		),
		ForeignKey(
			entity = SeriesEntity::class,
			parentColumns = ["id"],
			childColumns = ["series_id"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE,
		),
	],
	indices = [
		Index("team_series_id", "series_id"),
	],
)
data class TeamSeriesSeriesCrossRef(
	@ColumnInfo(name = "team_series_id", index = true) val teamSeriesId: UUID,
	@ColumnInfo(name = "series_id", index = true) val seriesId: UUID,
	@ColumnInfo val position: Int = 0,
)
