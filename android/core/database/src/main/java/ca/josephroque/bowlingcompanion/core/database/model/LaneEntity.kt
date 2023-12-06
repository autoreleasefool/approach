package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.LaneCreate
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import ca.josephroque.bowlingcompanion.core.model.LanePosition
import java.util.UUID

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
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: UUID,
	@ColumnInfo(name = "alley_id", index = true) val alleyId: UUID,
	@ColumnInfo(name = "label") val label: String,
	@ColumnInfo(name = "position") val position: LanePosition,
)

data class LaneCreateEntity(
	val id: UUID,
	@ColumnInfo(name = "alley_id") val alleyId: UUID,
	val label: String,
	val position: LanePosition,
)

fun LaneCreate.asEntity(): LaneCreateEntity = LaneCreateEntity(
	id = id,
	alleyId = alleyId,
	position = position,
	label = label,
)