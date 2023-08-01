package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.Team
import java.util.UUID

@Entity(
	tableName = "teams"
)
data class TeamEntity(
	@PrimaryKey val id: UUID,
	val name: String,
)

fun Team.asEntity() = TeamEntity(
	id = id,
	name = name,
)