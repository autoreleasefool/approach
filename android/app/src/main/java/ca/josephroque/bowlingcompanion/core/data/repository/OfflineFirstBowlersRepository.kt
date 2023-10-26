package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.dao.BowlerDao
import ca.josephroque.bowlingcompanion.core.database.model.BowlerCreate
import ca.josephroque.bowlingcompanion.core.database.model.BowlerUpdate
import ca.josephroque.bowlingcompanion.core.model.BowlerDetails
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import ca.josephroque.bowlingcompanion.core.model.OpponentListItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineFirstBowlersRepository @Inject constructor(
	private val bowlerDao: BowlerDao,
): BowlersRepository {
	override fun getBowlerDetails(bowlerId: BowlerID): Flow<BowlerDetails> =
		bowlerDao.getBowlerDetails(bowlerId)

	override fun getBowlersList(): Flow<List<BowlerListItem>> =
		bowlerDao.getBowlersList()

	override fun getOpponentsList(): Flow<List<OpponentListItem>> =
		bowlerDao.getOpponentsList()

	override suspend fun insertBowler(bowler: BowlerCreate) {
		bowlerDao.insertBowler(bowler)
	}

	override suspend fun updateBowler(bowler: BowlerUpdate) {
		bowlerDao.updateBowler(bowler)
	}

	override suspend fun deleteBowler(id: BowlerID) {
		bowlerDao.deleteBowler(id)
	}
}