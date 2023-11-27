package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.BowlerCreate
import ca.josephroque.bowlingcompanion.core.model.BowlerDetails
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import ca.josephroque.bowlingcompanion.core.model.BowlerUpdate
import ca.josephroque.bowlingcompanion.core.model.OpponentListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface BowlersRepository {
	fun getBowlersList(): Flow<List<BowlerListItem>>
	fun getOpponentsList(): Flow<List<BowlerListItem>>

	fun getBowlerDetails(bowlerId: UUID): Flow<BowlerDetails>

	suspend fun insertBowler(bowler: BowlerCreate)
	suspend fun updateBowler(bowler: BowlerUpdate)
	suspend fun archiveBowler(id: UUID)
}