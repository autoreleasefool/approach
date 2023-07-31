package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.Bowler
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.BowlerUpdate
import kotlinx.coroutines.flow.Flow
import java.util.UUID

data class BowlerQuery(
	val kind: BowlerKind = BowlerKind.PLAYABLE,
)

interface BowlersRepository {
	fun getBowlers(query: BowlerQuery): Flow<List<Bowler>>
	fun getBowler(id: UUID): Flow<Bowler>

	suspend fun insertBowler(bowler: Bowler)
	suspend fun updateBowler(bowler: BowlerUpdate)
	suspend fun deleteBowler(id: UUID)
}