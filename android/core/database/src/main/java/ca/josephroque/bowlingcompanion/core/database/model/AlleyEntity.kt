package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import ca.josephroque.bowlingcompanion.core.model.AlleyCreate
import ca.josephroque.bowlingcompanion.core.model.AlleyDetails
import ca.josephroque.bowlingcompanion.core.model.AlleyMaterial
import ca.josephroque.bowlingcompanion.core.model.AlleyMechanism
import ca.josephroque.bowlingcompanion.core.model.AlleyPinBase
import ca.josephroque.bowlingcompanion.core.model.AlleyPinFall
import ca.josephroque.bowlingcompanion.core.model.AlleyUpdate
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
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
		),
	],
)
data class AlleyEntity(
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: UUID,
	@ColumnInfo(name = "name") val name: String,
	@ColumnInfo(name = "material") val material: AlleyMaterial?,
	@ColumnInfo(name = "pin_fall") val pinFall: AlleyPinFall?,
	@ColumnInfo(name = "mechanism") val mechanism: AlleyMechanism?,
	@ColumnInfo(name = "pin_base") val pinBase: AlleyPinBase?,
	@ColumnInfo(name = "location_id", index = true, defaultValue = "NULL") val locationId: UUID?,
)

data class AlleyDetailsEntity(
	val id: UUID,
	val name: String,
	val material: AlleyMaterial?,
	@ColumnInfo(name = "pin_fall") val pinFall: AlleyPinFall?,
	val mechanism: AlleyMechanism?,
	@ColumnInfo(name = "pin_base") val pinBase: AlleyPinBase?,
) {
	fun asModel(): AlleyDetails = AlleyDetails(
		id = this.id,
		name = this.name,
		material = this.material,
		pinFall = this.pinFall,
		mechanism = this.mechanism,
		pinBase = this.pinBase,
	)
}

data class AlleyCreateEntity(
	val id: UUID,
	val name: String,
	val material: AlleyMaterial?,
	@ColumnInfo(name = "pin_fall") val pinFall: AlleyPinFall?,
	val mechanism: AlleyMechanism?,
	@ColumnInfo(name = "pin_base") val pinBase: AlleyPinBase?,
)

fun AlleyCreate.asEntity(): AlleyCreateEntity = AlleyCreateEntity(
	id = id,
	name = name,
	material = material,
	pinFall = pinFall,
	mechanism = mechanism,
	pinBase = pinBase,
)

data class AlleyUpdateEntity(
	@Embedded
	val properties: AlleyUpdate.Properties,
	@Relation(
		parentColumn = "id",
		entityColumn = "alley_id",
		entity = LaneEntity::class,
	)
	val lanes: List<LaneListItem>,
) {
	fun asModel(): AlleyUpdate = AlleyUpdate(
		id = properties.id,
		name = properties.name,
		material = properties.material,
		pinFall = properties.pinFall,
		mechanism = properties.mechanism,
		pinBase = properties.pinBase,
		lanes = lanes,
	)
}

data class AlleyDetailsUpdateEntity(
	val id: UUID,
	val name: String,
	val material: AlleyMaterial?,
	@ColumnInfo(name = "pin_fall") val pinFall: AlleyPinFall?,
	val mechanism: AlleyMechanism?,
	@ColumnInfo(name = "pin_base") val pinBase: AlleyPinBase?,
)

fun AlleyUpdate.asEntity(): AlleyDetailsUpdateEntity = AlleyDetailsUpdateEntity(
	id = id,
	name = name,
	material = material,
	pinFall = pinFall,
	mechanism = mechanism,
	pinBase = pinBase,
)
