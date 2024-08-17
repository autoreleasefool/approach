package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.TeamID

@Entity(
	tableName = "team_bowler",
	primaryKeys = ["team_id", "bowler_id"],
	foreignKeys = [
		ForeignKey(
			entity = TeamEntity::class,
			parentColumns = ["id"],
			childColumns = ["team_id"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE,
		),
		ForeignKey(
			entity = BowlerEntity::class,
			parentColumns = ["id"],
			childColumns = ["bowler_id"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE,
		),
	],
	indices = [
		Index("team_id", "bowler_id"),
	],
)
data class TeamBowlerCrossRef(
	@ColumnInfo(name = "team_id", index = true) val teamId: TeamID,
	@ColumnInfo(name = "bowler_id", index = true) val bowlerId: BowlerID,
)
