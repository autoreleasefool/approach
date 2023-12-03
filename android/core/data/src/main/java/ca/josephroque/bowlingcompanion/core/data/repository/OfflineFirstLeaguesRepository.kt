package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.database.dao.LeagueDao
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.model.ArchivedLeague
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.LeagueCreate
import ca.josephroque.bowlingcompanion.core.model.LeagueDetails
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.model.LeagueUpdate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

class OfflineFirstLeaguesRepository @Inject constructor(
	private val leagueDao: LeagueDao,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
): LeaguesRepository {
	override fun getLeagueBowler(id: UUID): Flow<BowlerSummary> =
		leagueDao.getLeagueBowler(id)

	override fun getLeagueSummary(id: UUID): Flow<LeagueSummary> =
		leagueDao.getLeagueSummary(id)

	override fun getLeagueDetails(id: UUID): Flow<LeagueDetails> =
		leagueDao.getLeagueDetails(id)

	override fun getLeaguesList(bowlerId: UUID): Flow<List<LeagueListItem>> =
		leagueDao.getLeagueAverages(bowlerId = bowlerId)

	override fun getArchivedLeagues(): Flow<List<ArchivedLeague>> =
		leagueDao.getArchivedLeagues()

	override suspend fun insertLeague(league: LeagueCreate) = withContext(ioDispatcher) {
		leagueDao.insertLeague(league.asEntity())
	}

	override suspend fun updateLeague(league: LeagueUpdate) = withContext(ioDispatcher) {
		leagueDao.updateLeague(league.asEntity())
	}

	override suspend fun deleteLeague(id: UUID) = withContext(ioDispatcher) {
		leagueDao.deleteLeague(id)
	}

	override suspend fun archiveLeague(id: UUID) = withContext(ioDispatcher) {
		leagueDao.archiveLeague(id, archivedOn = Clock.System.now())
	}

	override suspend fun unarchiveLeague(id: UUID) = withContext(ioDispatcher) {
		leagueDao.unarchiveLeague(id)
	}
}