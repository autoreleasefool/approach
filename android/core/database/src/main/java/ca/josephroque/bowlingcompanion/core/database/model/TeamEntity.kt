package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.Team
import java.util.UUID

@Entity(
	tableName = "teams",
)
data class TeamEntity(
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: UUID,
	@ColumnInfo(name = "name") val name: String,
)

fun Team.asEntity() = TeamEntity(
	id = id,
	name = name,
)