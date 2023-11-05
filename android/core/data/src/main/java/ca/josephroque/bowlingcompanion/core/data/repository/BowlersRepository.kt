package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.model.BowlerCreate
import ca.josephroque.bowlingcompanion.core.database.model.BowlerUpdate
import ca.josephroque.bowlingcompanion.core.model.BowlerDetails
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import ca.josephroque.bowlingcompanion.core.model.OpponentListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface BowlersRepository {
	fun getBowlerDetails(bowlerId: UUID): Flow<BowlerDetails>
	fun getBowlersList(): Flow<List<BowlerListItem>>
	fun getOpponentsList(): Flow<List<OpponentListItem>>

	suspend fun insertBowler(bowler: BowlerCreate)
	suspend fun updateBowler(bowler: BowlerUpdate)
	suspend fun deleteBowler(id: UUID)
}