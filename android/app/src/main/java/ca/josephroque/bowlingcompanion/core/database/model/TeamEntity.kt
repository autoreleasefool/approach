package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
	tableName = "teams"
)
data class TeamEntity(
	@PrimaryKey val id: UUID,
	val name: String,
)