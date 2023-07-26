package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.League
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import java.util.UUID

@Entity(
	tableName = "leagues",
)
data class LeagueEntity(
	@PrimaryKey val id: UUID,
	val bowlerId: UUID,
	val name: String,
	val recurrence: LeagueRecurrence,
	val numberOfGames: Int?,
	val additionalPinFall: Int?,
	val additionalGames: Int?,
	val excludeFromStatistics: ExcludeFromStatistics,
)

fun LeagueEntity.asExternalModel() = League(
	id = id,
	name = name,
	recurrence = recurrence,
	numberOfGames = numberOfGames,
	additionalPinFall = additionalPinFall,
	additionalGames = additionalGames,
	excludeFromStatistics = excludeFromStatistics
)