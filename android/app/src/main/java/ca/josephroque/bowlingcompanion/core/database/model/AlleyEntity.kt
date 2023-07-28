package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.Alley
import ca.josephroque.bowlingcompanion.core.model.AlleyMaterial
import ca.josephroque.bowlingcompanion.core.model.AlleyMechanism
import ca.josephroque.bowlingcompanion.core.model.AlleyPinBase
import ca.josephroque.bowlingcompanion.core.model.AlleyPinFall
import java.util.UUID

@Entity(
	tableName = "alleys",
)
data class AlleyEntity(
	@PrimaryKey val id: UUID,
	val name: String,
	val material: AlleyMaterial?,
	val pinFall: AlleyPinFall?,
	val mechanism: AlleyMechanism?,
	val pinBase: AlleyPinBase?,
	val locationId: UUID?,
)

fun AlleyEntity.asExternalModel() = Alley(
	id = id,
	name = name,
	material = material,
	pinFall = pinFall,
	mechanism = mechanism,
	pinBase = pinBase,
)