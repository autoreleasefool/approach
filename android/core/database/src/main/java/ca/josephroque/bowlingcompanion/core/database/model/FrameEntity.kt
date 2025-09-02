package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Relation
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.FrameCreate
import ca.josephroque.bowlingcompanion.core.model.FrameEdit
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GearID
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.model.ScoreableFrame
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.TrackableFrame
import kotlinx.datetime.LocalDate

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
	],
)
data class FrameEntity(
	@ColumnInfo(name = "game_id", index = true) val gameId: GameID,
	@ColumnInfo(name = "index", index = true) val index: Int,
	@ColumnInfo(name = "roll0") val roll0: FrameRollEntity?,
	@ColumnInfo(name = "roll1") val roll1: FrameRollEntity?,
	@ColumnInfo(name = "roll2") val roll2: FrameRollEntity?,
	@ColumnInfo(name = "ball0", index = true) val ball0: GearID?,
	@ColumnInfo(name = "ball1", index = true) val ball1: GearID?,
	@ColumnInfo(name = "ball2", index = true) val ball2: GearID?,
)

data class FrameRollEntity(val pinsDowned: Set<Pin>, val didFoul: Boolean) {
	fun toBitString(): String {
		val bitString = StringBuilder()
		bitString.append(if (didFoul) '1' else '0')
		Pin.entries.forEach { pin ->
			bitString.append(if (pin in pinsDowned) '1' else '0')
		}
		return bitString.toString()
	}

	companion object {
		fun fromBitString(string: String?): FrameRollEntity? {
			string ?: return null

			val didFoul = string.first() != '0'
			val pinsDowned = string.drop(1).mapIndexedNotNull { index, bit ->
				if (bit == '0') null else Pin.entries[index]
			}

			return FrameRollEntity(pinsDowned.toSet(), didFoul)
		}
	}
}

fun FrameEdit.asEntity(): FrameEntity = FrameEntity(
	gameId = this.properties.gameId,
	index = this.properties.index,
	roll0 = this.rolls.getOrNull(0)?.let { FrameRollEntity(it.pinsDowned, it.didFoul) },
	roll1 = this.rolls.getOrNull(1)?.let { FrameRollEntity(it.pinsDowned, it.didFoul) },
	roll2 = this.rolls.getOrNull(2)?.let { FrameRollEntity(it.pinsDowned, it.didFoul) },
	ball0 = this.rolls.getOrNull(0)?.bowlingBall?.id,
	ball1 = this.rolls.getOrNull(1)?.bowlingBall?.id,
	ball2 = this.rolls.getOrNull(2)?.bowlingBall?.id,
)

fun FrameCreate.asEntity(): FrameEntity = FrameEntity(
	gameId = gameId,
	index = index,
	roll0 = null,
	roll1 = null,
	roll2 = null,
	ball0 = null,
	ball1 = null,
	ball2 = null,
)

data class TrackableFrameEntity(
	val seriesId: SeriesID,
	val gameId: GameID,
	val gameIndex: Int,
	val index: Int,
	@ColumnInfo(name = "roll0") val roll0: FrameRollEntity?,
	@ColumnInfo(name = "roll1") val roll1: FrameRollEntity?,
	@ColumnInfo(name = "roll2") val roll2: FrameRollEntity?,
	val date: LocalDate,
) {
	fun asModel(): TrackableFrame = TrackableFrame(
		seriesId = this.seriesId,
		gameId = this.gameId,
		gameIndex = this.gameIndex,
		index = this.index,
		rolls = listOfNotNull(
			this.roll0?.let { TrackableFrame.Roll(0, it.pinsDowned, it.didFoul) },
			this.roll1?.let { TrackableFrame.Roll(1, it.pinsDowned, it.didFoul) },
			this.roll2?.let { TrackableFrame.Roll(2, it.pinsDowned, it.didFoul) },
		),
		date = this.date,
	)
}

data class FrameEditEntity(
	@Embedded val properties: FrameEditPropertiesEntity,
	@Embedded(prefix = "ball0_") val ball0: FrameEditGearEntity?,
	@Embedded(prefix = "ball1_") val ball1: FrameEditGearEntity?,
	@Embedded(prefix = "ball2_") val ball2: FrameEditGearEntity?,
) {
	fun asModel(): FrameEdit = FrameEdit(
		properties = this.properties.asModel(),
		rolls = listOfNotNull(
			this.properties.roll0?.let { FrameEdit.Roll(0, it.pinsDowned, it.didFoul, this.ball0?.asModel()) },
			this.properties.roll1?.let { FrameEdit.Roll(1, it.pinsDowned, it.didFoul, this.ball1?.asModel()) },
			this.properties.roll2?.let { FrameEdit.Roll(2, it.pinsDowned, it.didFoul, this.ball2?.asModel()) },
		),
	)
}

data class FrameEditPropertiesEntity(
	val gameId: GameID,
	val index: Int,
	val roll0: FrameRollEntity?,
	val roll1: FrameRollEntity?,
	val roll2: FrameRollEntity?,
) {
	fun asModel() = FrameEdit.Properties(
		gameId = this.gameId,
		index = this.index,
	)
}

data class FrameEditGearEntity(val id: GearID, val name: String, val kind: GearKind, val avatar: Avatar) {
	fun asModel() = FrameEdit.Gear(
		id = id,
		name = name,
		kind = kind,
		avatar = avatar,
	)
}

data class ScoreableFramesEntity(
	@Embedded val properties: ScoreableFramesPropertiesEntity,
	@Relation(
		parentColumn = "id",
		entityColumn = "game_id",
		entity = FrameEntity::class,
		projection = ["index", "roll0", "roll1", "roll2"],
	)
	val frames: List<ScoreableFrameEntity>,
)

data class ScoreableFramesPropertiesEntity(val id: GameID, val index: Int)

data class ScoreableFrameEntity(
	val index: Int,
	val roll0: FrameRollEntity?,
	val roll1: FrameRollEntity?,
	val roll2: FrameRollEntity?,
) {
	fun asModel(): ScoreableFrame = ScoreableFrame(
		index = this.index,
		rolls = listOfNotNull(
			this.roll0?.let { ScoreableFrame.Roll(0, it.pinsDowned, it.didFoul) },
			this.roll1?.let { ScoreableFrame.Roll(1, it.pinsDowned, it.didFoul) },
			this.roll2?.let { ScoreableFrame.Roll(2, it.pinsDowned, it.didFoul) },
		),
	)
}
