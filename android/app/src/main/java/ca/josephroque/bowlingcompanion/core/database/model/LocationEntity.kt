package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.Location
import java.util.UUID

@Entity(
	tableName = "locations",
)
data class LocationEntity(
	@PrimaryKey val id: UUID,
	val title: String,
	val subtitle: String,
	val latitude: Double,
	val longitude: Double,
)

fun LocationEntity.asExternalModel() = Location(
	id = id,
	title = title,
	subtitle = subtitle,
	latitude = latitude,
	longitude = longitude,
)

