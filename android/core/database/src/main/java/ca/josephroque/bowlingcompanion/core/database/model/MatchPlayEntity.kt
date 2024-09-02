package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.MatchPlayCreate
import ca.josephroque.bowlingcompanion.core.model.MatchPlayID
import ca.josephroque.bowlingcompanion.core.model.MatchPlayResult
import ca.josephroque.bowlingcompanion.core.model.MatchPlayUpdate

@Entity(
	tableName = "match_plays",
	foreignKeys = [
		ForeignKey(
			entity = GameEntity::class,
			parentColumns = ["id"],
			childColumns = ["game_id"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE,
		),
		ForeignKey(
			entity = BowlerEntity::class,
			parentColumns = ["id"],
			childColumns = ["opponent_id"],
			onDelete = ForeignKey.SET_NULL,
			onUpdate = ForeignKey.CASCADE,
		),
	],
)
data class MatchPlayEntity(
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: MatchPlayID,
	@ColumnInfo(name = "game_id", index = true) val gameId: GameID,
	@ColumnInfo(name = "opponent_id", index = true) val opponentId: BowlerID?,
	@ColumnInfo(name = "opponent_score") val opponentScore: Int?,
	@ColumnInfo(name = "result") val result: MatchPlayResult?,
)

data class MatchPlayCreateEntity(
	val id: MatchPlayID,
	@ColumnInfo(name = "game_id") val gameId: GameID,
	@ColumnInfo(name = "opponent_id") val opponentId: BowlerID?,
	@ColumnInfo(name = "opponent_score") val opponentScore: Int?,
	val result: MatchPlayResult?,
)

fun MatchPlayCreate.asEntity(): MatchPlayCreateEntity = MatchPlayCreateEntity(
	id = id,
	gameId = gameId,
	opponentId = opponentId,
	opponentScore = opponentScore,
	result = result,
)

data class MatchPlayUpdateEntity(
	@Embedded val properties: MatchPlayUpdate.Properties,
	@Relation(
		parentColumn = "opponentId",
		entityColumn = "id",
		entity = BowlerEntity::class,
	) val opponent: BowlerSummary?,
) {
	fun asModel(): MatchPlayUpdate = MatchPlayUpdate(
		id = properties.id,
		opponent = opponent,
		opponentScore = properties.opponentScore,
		result = properties.result,
	)
}

data class MatchPlayDetailsUpdateEntity(
	val id: MatchPlayID,
	@ColumnInfo(name = "opponent_id") val opponentId: BowlerID?,
	@ColumnInfo(name = "opponent_score") val opponentScore: Int?,
	val result: MatchPlayResult?,
)

fun MatchPlayUpdate.asEntity(): MatchPlayDetailsUpdateEntity = MatchPlayDetailsUpdateEntity(
	id = id,
	opponentId = opponent?.id,
	opponentScore = opponentScore,
	result = result,
)
