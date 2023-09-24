package ca.josephroque.bowlingcompanion.core.database.model

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.Bowler
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import java.util.UUID

@Entity(
	tableName = "bowlers",
)
@Immutable
data class BowlerEntity(
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: UUID,
	@ColumnInfo(name = "name") val name: String,
	@ColumnInfo(name = "kind") val kind: BowlerKind,
)

fun BowlerEntity.asExternalModel() = Bowler(
	id = id,
	name = name,
	kind = kind,
)

fun Bowler.asEntity() = BowlerEntity(
	id = id,
	name = name,
	kind = kind,
)

data class BowlerWithAverage(
	val id: UUID,
	val name: String,
	val average: Double?,
)

fun BowlerWithAverage.asListItem() =
	BowlerListItem(id = id, name = name, average = average)