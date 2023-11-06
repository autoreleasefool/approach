package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.database.dao.BowlerDao
import ca.josephroque.bowlingcompanion.core.database.model.BowlerCreate
import ca.josephroque.bowlingcompanion.core.database.model.BowlerUpdate
import ca.josephroque.bowlingcompanion.core.model.BowlerDetails
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import ca.josephroque.bowlingcompanion.core.model.OpponentListItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import java.util.UUID
import javax.inject.Inject

class OfflineFirstBowlersRepository @Inject constructor(
	private val bowlerDao: BowlerDao,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
): BowlersRepository {
	override fun getBowlerDetails(bowlerId: UUID): Flow<BowlerDetails> =
		bowlerDao.getBowlerDetails(bowlerId)

	override fun getBowlersList(): Flow<List<BowlerListItem>> =
		bowlerDao.getBowlersList()

	override fun getOpponentsList(): Flow<List<OpponentListItem>> =
		bowlerDao.getOpponentsList()

	override suspend fun insertBowler(bowler: BowlerCreate) = withContext(ioDispatcher) {
		bowlerDao.insertBowler(bowler)
	}

	override suspend fun updateBowler(bowler: BowlerUpdate) = withContext(ioDispatcher) {
		bowlerDao.updateBowler(bowler)
	}

	override suspend fun archiveBowler(id: UUID, archivedOn: Instant) = withContext(ioDispatcher) {
		bowlerDao.archiveBowler(id, archivedOn)
	}
}