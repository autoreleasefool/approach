package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import java.util.UUID
import kotlinx.datetime.LocalDate

@Entity(
	tableName = "games",
	foreignKeys = [
		ForeignKey(
			entity = SeriesEntity::class,
			parentColumns = ["id"],
			childColumns = ["series_id"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE,
		),
	],
)
data class GameEntity(
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: UUID,
	@ColumnInfo(name = "series_id", index = true) val seriesId: UUID,
	@ColumnInfo(name = "index") val index: Int,
	@ColumnInfo(name = "score") val score: Int,
	@ColumnInfo(name = "locked") val locked: GameLockState,
	@ColumnInfo(name = "scoring_method") val scoringMethod: GameScoringMethod,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
)

data class GameDetails(
	@Embedded val properties: GameDetailsProperties,
	@Embedded(prefix = "series_") val series: GameDetailsSeriesProperties,
	@Embedded(prefix = "league_") val league: GameDetailsLeagueProperties,
	@Embedded(prefix = "bowler_") val bowler: GameDetailsBowlerProperties,
)

data class GameDetailsProperties(
	val id: UUID,
	val index: Int,
	val score: Int,
	val locked: GameLockState,
	val scoringMethod: GameScoringMethod,
	val excludeFromStatistics: ExcludeFromStatistics,
)

data class GameDetailsSeriesProperties(
	val date: LocalDate,
	val preBowl: SeriesPreBowl,
	val excludeFromStatistics: ExcludeFromStatistics,
)

data class GameDetailsLeagueProperties(
	val name: String,
	val excludeFromStatistics: ExcludeFromStatistics,
)

data class GameDetailsBowlerProperties(
	val name: String
)