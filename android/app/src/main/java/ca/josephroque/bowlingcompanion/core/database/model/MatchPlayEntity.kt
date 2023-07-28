package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.MatchPlayResult
import java.util.UUID

@Entity(
	tableName = "match_plays",
)
data class MatchPlayEntity(
	@PrimaryKey val id: UUID,
	val gameId: UUID,
	val opponentId: UUID?,
	val opponentScore: Int?,
	val result: MatchPlayResult?,
)