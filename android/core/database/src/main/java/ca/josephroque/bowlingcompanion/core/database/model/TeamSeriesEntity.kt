package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID
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
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: UUID,
	@ColumnInfo(name = "team_id", index = true) val teamId: UUID,
	val date: LocalDate,
)
