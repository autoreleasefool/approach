package ca.josephroque.bowlingcompanion.core.database.model

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import kotlinx.datetime.Instant
import java.util.UUID

@Entity(
	tableName = "series",
	foreignKeys = [
		ForeignKey(
			entity = LeagueEntity::class,
			parentColumns = ["id"],
			childColumns = ["league_id"],
			onUpdate = ForeignKey.CASCADE,
			onDelete = ForeignKey.CASCADE,
		),
		ForeignKey(
			entity = AlleyEntity::class,
			parentColumns = ["id"],
			childColumns = ["alley_id"],
			onUpdate = ForeignKey.CASCADE,
			onDelete = ForeignKey.SET_NULL,
		),
	],
)
@Immutable
data class SeriesEntity(
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: UUID,
	@ColumnInfo(name = "league_id", index = true) val leagueId: UUID,
	@ColumnInfo(name = "date") val date: Instant,
	@ColumnInfo(name = "number_of_games") val numberOfGames: Int,
	@ColumnInfo(name = "pre_bowl") val preBowl: SeriesPreBowl,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
	@ColumnInfo(name = "alley_id", index = true) val alleyId: UUID?
)