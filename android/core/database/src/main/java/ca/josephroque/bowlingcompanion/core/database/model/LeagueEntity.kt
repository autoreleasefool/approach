package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.LeagueCreate
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.LeagueUpdate
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
data class LeagueEntity(
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: UUID,
	@ColumnInfo(name = "bowler_id", index = true) val bowlerId: UUID,
	@ColumnInfo(name = "name") val name: String,
	@ColumnInfo(name = "recurrence") val recurrence: LeagueRecurrence,
	@ColumnInfo(name = "number_of_games") val numberOfGames: Int?,
	@ColumnInfo(name = "additional_pin_fall") val additionalPinFall: Int?,
	@ColumnInfo(name = "additional_games") val additionalGames: Int?,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
	@ColumnInfo(name = "archived_on", defaultValue = "NULL") val archivedOn: Instant? = null,
)

data class LeagueCreateEntity(
	@ColumnInfo(name = "bowler_id") val bowlerId: UUID,
	val id: UUID,
	val name: String,
	val recurrence: LeagueRecurrence,
	@ColumnInfo(name = "number_of_games") val numberOfGames: Int?,
	@ColumnInfo(name = "additional_pin_fall")val additionalPinFall: Int?,
	@ColumnInfo(name = "additional_games") val additionalGames: Int?,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
)

fun LeagueCreate.asEntity(): LeagueCreateEntity = LeagueCreateEntity(
	bowlerId = bowlerId,
	id = id,
	name = name,
	recurrence = recurrence,
	numberOfGames = numberOfGames,
	additionalPinFall = additionalPinFall,
	additionalGames = additionalGames,
	excludeFromStatistics = excludeFromStatistics,
)

data class LeagueUpdateEntity(
	val id: UUID,
	val name: String,
	@ColumnInfo(name = "additional_pin_fall") val additionalPinFall: Int?,
	@ColumnInfo(name = "additional_games") val additionalGames: Int?,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
)

fun LeagueUpdate.asEntity(): LeagueUpdateEntity = LeagueUpdateEntity(
	id = id,
	name = name,
	additionalPinFall = additionalPinFall,
	additionalGames = additionalGames,
	excludeFromStatistics = excludeFromStatistics,
)