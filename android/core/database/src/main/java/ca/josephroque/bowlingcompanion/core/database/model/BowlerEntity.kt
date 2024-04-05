package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.BowlerCreate
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.BowlerUpdate
import ca.josephroque.bowlingcompanion.core.model.OpponentListItem
import java.util.UUID
import kotlinx.datetime.Instant

@Entity(tableName = "bowlers")
data class BowlerEntity(
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: UUID,
	@ColumnInfo(name = "name") val name: String,
	@ColumnInfo(name = "kind") val kind: BowlerKind,
	@ColumnInfo(name = "archived_on", defaultValue = "NULL") val archivedOn: Instant? = null,
)

data class BowlerCreateEntity(
	val id: UUID,
	val name: String,
	val kind: BowlerKind,
)

fun BowlerCreate.asEntity(): BowlerCreateEntity = BowlerCreateEntity(
	id = id,
	name = name,
	kind = kind,
)

data class BowlerUpdateEntity(
	val id: UUID,
	val name: String,
)

fun BowlerUpdate.asEntity(): BowlerUpdateEntity = BowlerUpdateEntity(
	id = id,
	name = name,
)

fun OpponentListItem.asEntity(): BowlerEntity = BowlerEntity(
	id = id,
	name = name,
	kind = kind,
)
