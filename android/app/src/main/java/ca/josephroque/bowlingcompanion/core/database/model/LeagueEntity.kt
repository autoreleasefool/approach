package ca.josephroque.bowlingcompanion.core.database.model

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import kotlinx.datetime.Instant
import java.util.UUID

@Entity(
	tableName = "leagues",
	foreignKeys = [
		ForeignKey(
			entity = BowlerEntity::class,
			parentColumns = ["id"],
			childColumns = ["bowler_id"],
			onUpdate = ForeignKey.CASCADE,
			onDelete = ForeignKey.CASCADE,
		),
	],
)
@Immutable
data class LeagueEntity(
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: UUID,
	@ColumnInfo(name = "bowler_id", index = true) val bowlerId: UUID,
	@ColumnInfo(name = "name") val name: String,
	@ColumnInfo(name = "recurrence") val recurrence: LeagueRecurrence,
	@ColumnInfo(name = "number_of_games") val numberOfGames: Int?,
	@ColumnInfo(name = "additional_pin_fall") val additionalPinFall: Int?,
	@ColumnInfo(name = "additional_games") val additionalGames: Int?,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
)

data class LeagueWithAverage(
	val id: UUID,
	val name: String,
	val recurrence: LeagueRecurrence,
	val average: Double?,
	val lastSeriesDate: Instant?,
)

fun LeagueWithAverage.asListItem() =
	LeagueListItem(id = id, name = name, average = average, recurrence = recurrence, lastSeriesDate = lastSeriesDate)