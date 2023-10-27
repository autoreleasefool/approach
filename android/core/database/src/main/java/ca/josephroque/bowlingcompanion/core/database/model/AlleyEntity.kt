package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.AlleyMaterial
import ca.josephroque.bowlingcompanion.core.model.AlleyMechanism
import ca.josephroque.bowlingcompanion.core.model.AlleyPinBase
import ca.josephroque.bowlingcompanion.core.model.AlleyPinFall
import java.util.UUID

@Entity(
	tableName = "alleys",
	foreignKeys = [
		ForeignKey(
			entity = LocationEntity::class,
			parentColumns = ["id"],
			childColumns = ["location_id"],
			onUpdate = ForeignKey.CASCADE,
			onDelete = ForeignKey.CASCADE,
		)
	],
)
data class AlleyEntity(
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: UUID,
	@ColumnInfo(name = "name") val name: String,
	@ColumnInfo(name = "material") val material: AlleyMaterial?,
	@ColumnInfo(name = "pin_fall") val pinFall: AlleyPinFall?,
	@ColumnInfo(name = "mechanism") val mechanism: AlleyMechanism?,
	@ColumnInfo(name = "pin_base") val pinBase: AlleyPinBase?,
	@ColumnInfo(name = "location_id", index = true) val locationId: UUID?,
)

data class AlleyCreate(
	val id: UUID,
	val name: String,
	val material: AlleyMaterial?,
	@ColumnInfo(name = "pin_fall") val pinFall: AlleyPinFall?,
	val mechanism: AlleyMechanism?,
	@ColumnInfo(name = "pin_base") val pinBase: AlleyPinBase?,
)

data class AlleyUpdate(
	val id: UUID,
	val name: String,
	val material: AlleyMaterial?,
	@ColumnInfo(name = "pin_fall") val pinFall: AlleyPinFall?,
	val mechanism: AlleyMechanism?,
	@ColumnInfo(name = "pin_base") val pinBase: AlleyPinBase?,
)