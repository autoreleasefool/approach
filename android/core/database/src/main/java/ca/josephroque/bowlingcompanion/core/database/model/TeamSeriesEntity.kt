package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesCreate
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

@Entity(
	tableName = "team_series",
	foreignKeys = [
		ForeignKey(
			entity = TeamEntity::class,
			parentColumns = ["id"],
			childColumns = ["team_id"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE,
		),
	],
)
data class TeamSeriesEntity(
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: TeamSeriesID,
	@ColumnInfo(name = "team_id", index = true) val teamId: TeamID,
	val date: LocalDate,
	@ColumnInfo(name = "archived_on", defaultValue = "NULL") val archivedOn: Instant? = null,
)

data class TeamSeriesDetailItemEntity(
	val date: LocalDate,
	val score: Int,
	@ColumnInfo(name = "game_id") val gameId: GameID,
	@ColumnInfo(name = "game_index") val gameIndex: Int,
	@ColumnInfo(name = "game_is_archived") val gameIsArchived: Boolean,
	@ColumnInfo(name = "bowler_id") val bowlerId: BowlerID,
	@ColumnInfo(name = "bowler_name") val bowlerName: String,
)

data class TeamSeriesCreateEntity(
	@ColumnInfo(name = "team_id") val teamId: TeamID,
	val id: TeamSeriesID,
	val date: LocalDate,
)

fun TeamSeriesCreate.asEntity() = TeamSeriesCreateEntity(
	teamId = teamId,
	id = id,
	date = date,
)
