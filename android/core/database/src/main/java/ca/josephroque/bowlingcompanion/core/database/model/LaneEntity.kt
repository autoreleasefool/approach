package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.LaneID
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import ca.josephroque.bowlingcompanion.core.model.LanePosition

@Entity(
	tableName = "lanes",
	foreignKeys = [
		ForeignKey(
			entity = AlleyEntity::class,
			parentColumns = ["id"],
			childColumns = ["alley_id"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE,
		),
	],
)
data class LaneEntity(
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: LaneID,
	@ColumnInfo(name = "alley_id", index = true) val alleyId: AlleyID?,
	@ColumnInfo(name = "label") val label: String,
	@ColumnInfo(name = "position") val position: LanePosition,
)

fun LaneListItem.asEntity(alleyId: AlleyID? = null): LaneEntity = LaneEntity(
	id = id,
	alleyId = alleyId,
	position = position,
	label = label,
)
