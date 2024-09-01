package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GearID

@Entity(
	tableName = "bowler_preferred_gear",
	primaryKeys = ["bowler_id", "gear_id"],
	foreignKeys = [
		ForeignKey(
			entity = BowlerEntity::class,
			parentColumns = ["id"],
			childColumns = ["bowler_id"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE,
		),
		ForeignKey(
			entity = GearEntity::class,
			parentColumns = ["id"],
			childColumns = ["gear_id"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE,
		),
	],
	indices = [
		Index("bowler_id", "gear_id"),
	],
)
data class BowlerPreferredGearCrossRef(
	@ColumnInfo(name = "bowler_id", index = true) val bowlerId: BowlerID,
	@ColumnInfo(name = "gear_id", index = true) val gearId: GearID,
)
