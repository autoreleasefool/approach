package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.Bowler
import java.util.UUID

@Entity(
	tableName = "bowlers"
)

data class BowlerEntity(
	@PrimaryKey val id: UUID,
	val name: String
)

fun BowlerEntity.asExternalModel() = Bowler(
	id = id,
	name = name,
)