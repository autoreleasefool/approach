package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers.IO
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.database.dao.BowlerDao
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.model.ArchivedBowler
import ca.josephroque.bowlingcompanion.core.model.BowlerCreate
import ca.josephroque.bowlingcompanion.core.model.BowlerDetails
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.BowlerUpdate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

class OfflineFirstBowlersRepository @Inject constructor(
	private val bowlerDao: BowlerDao,
	@Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
): BowlersRepository {
	override fun getBowlerSummary(bowlerId: UUID): Flow<BowlerSummary> =
		bowlerDao.getBowlerSummary(bowlerId)

	override fun getBowlerDetails(bowlerId: UUID): Flow<BowlerDetails> =
		bowlerDao.getBowlerDetails(bowlerId)

	override fun getBowlersList(): Flow<List<BowlerListItem>> =
		bowlerDao.getBowlersList()

	override fun getOpponentsList(): Flow<List<BowlerListItem>> =
		bowlerDao.getOpponentsList()

	override fun getArchivedBowlers(): Flow<List<ArchivedBowler>> =
		bowlerDao.getArchivedBowlers()

	override suspend fun insertBowler(bowler: BowlerCreate) = withContext(ioDispatcher) {
		bowlerDao.insertBowler(bowler.asEntity())
	}

	override suspend fun updateBowler(bowler: BowlerUpdate) = withContext(ioDispatcher) {
		bowlerDao.updateBowler(bowler.asEntity())
	}

	override suspend fun archiveBowler(id: UUID) = withContext(ioDispatcher) {
		bowlerDao.archiveBowler(id, archivedOn = Clock.System.now())
	}

	override suspend fun unarchiveBowler(id: UUID) = withContext(ioDispatcher) {
		bowlerDao.unarchiveBowler(id)
	}
}