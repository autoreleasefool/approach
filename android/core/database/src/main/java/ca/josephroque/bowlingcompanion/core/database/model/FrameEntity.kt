package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import ca.josephroque.bowlingcompanion.core.model.Pin
import java.util.UUID

@Entity(
	tableName = "frames",
	primaryKeys = ["game_id", "index"],
	foreignKeys = [
		ForeignKey(
			entity = GameEntity::class,
			parentColumns = ["id"],
			childColumns = ["game_id"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE,
		),
		ForeignKey(
			entity = GearEntity::class,
			parentColumns = ["id"],
			childColumns = ["ball0"],
			onDelete = ForeignKey.SET_NULL,
			onUpdate = ForeignKey.CASCADE,
		),
		ForeignKey(
			entity = GearEntity::class,
			parentColumns = ["id"],
			childColumns = ["ball1"],
			onDelete = ForeignKey.SET_NULL,
			onUpdate = ForeignKey.CASCADE,
		),
		ForeignKey(
			entity = GearEntity::class,
			parentColumns = ["id"],
			childColumns = ["ball2"],
			onDelete = ForeignKey.SET_NULL,
			onUpdate = ForeignKey.CASCADE,
		),
	],
	indices = [
		Index("game_id", "index"),
	]
)
data class FrameEntity(
	@ColumnInfo(name = "game_id", index = true) val gameId: UUID,
	@ColumnInfo(name = "index", index = true) val index: Int,
	@ColumnInfo(name = "roll0") val roll0: Roll?,
	@ColumnInfo(name = "roll1") val roll1: Roll?,
	@ColumnInfo(name = "roll2") val roll2: Roll?,
	@ColumnInfo(name = "ball0", index = true) val ball0: UUID?,
	@ColumnInfo(name = "ball1", index = true) val ball1: UUID?,
	@ColumnInfo(name = "ball2", index = true) val ball2: UUID?,
) {
	data class Roll(
		val pinsDowned: Set<Pin>,
		val didFoul: Boolean,
	) {
		fun toBitString(): String {
			val bitString = StringBuilder()
			bitString.append(if (didFoul) '1' else '0')
			Pin.values().forEach { pin ->
				bitString.append(if (pin in pinsDowned) '1' else '0')
			}
			return bitString.toString()
		}

		companion object {
			fun fromBitString(string: String?): Roll? {
				string ?: return null

				val didFoul = string.first() != '0'
				val pinsDowned = string.drop(1).mapIndexedNotNull { index, bit ->
					if (bit == '0') null else Pin.values()[index]
				}

				return Roll(pinsDowned.toSet(), didFoul)
			}
		}
	}

	data class IndexedRoll(
		val index: Int,
		val roll: Roll,
	)

	val rolls: List<IndexedRoll>
		get() = listOfNotNull(
			roll0?.let { IndexedRoll(0, it) },
			roll1?.let { IndexedRoll(1, it) },
			roll2?.let { IndexedRoll(2, it) },
		)
}