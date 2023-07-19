package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.Bowler
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface BowlersRepository {
	fun getBowlers(): Flow<List<Bowler>>
	fun getBowler(id: UUID): Flow<Bowler>

	suspend fun upsertBowler(bowler: Bowler)
	suspend fun deleteBowler(id: UUID)
}