package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.Series
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import kotlinx.datetime.Instant
import java.util.UUID

@Entity(
	tableName = "series",
)
data class SeriesEntity(
	@PrimaryKey val id: UUID,
	val leagueId: UUID,
	val date: Instant,
	val numberOfGames: Int,
	val preBowl: SeriesPreBowl,
	val excludeFromStatistics: ExcludeFromStatistics,
)

fun SeriesEntity.asExternalModel() = Series(
	id = id,
	date = date,
	numberOfGames = numberOfGames,
	preBowl = preBowl,
	excludeFromStatistics = excludeFromStatistics,
)
