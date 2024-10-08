package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers.IO
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.common.utils.toLocalDate
import ca.josephroque.bowlingcompanion.core.database.dao.LeagueDao
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunner
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.model.ArchivedLeague
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.LeagueCreate
import ca.josephroque.bowlingcompanion.core.model.LeagueDetails
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.LeagueSortOrder
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.model.LeagueUpdate
import ca.josephroque.bowlingcompanion.core.model.Series
import ca.josephroque.bowlingcompanion.core.model.SeriesCreate
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class OfflineFirstLeaguesRepository @Inject constructor(
	private val leagueDao: LeagueDao,
	private val transactionRunner: TransactionRunner,
	private val seriesRepository: SeriesRepository,
	@Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : LeaguesRepository {
	override fun getLeagueBowler(id: LeagueID): Flow<BowlerSummary> = leagueDao.getLeagueBowler(id)

	override fun getLeagueSummary(id: LeagueID): Flow<LeagueSummary> = leagueDao.getLeagueSummary(id)

	override fun getLeagueDetails(id: LeagueID): Flow<LeagueDetails> = leagueDao.getLeagueDetails(id)

	override fun getLeaguesList(
		bowlerId: BowlerID,
		recurrence: LeagueRecurrence?,
		sortOrder: LeagueSortOrder,
	): Flow<List<LeagueListItem>> =
		leagueDao.getLeagueAverages(bowlerId = bowlerId, recurrence = recurrence, sortOrder = sortOrder)

	override fun getArchivedLeagues(): Flow<List<ArchivedLeague>> = leagueDao.getArchivedLeagues()

	override suspend fun insertLeague(league: LeagueCreate) = withContext(ioDispatcher) {
		transactionRunner {
			leagueDao.insertLeague(league.asEntity())
			when (league.recurrence) {
				LeagueRecurrence.REPEATING -> Unit
				LeagueRecurrence.ONCE -> seriesRepository.insertSeries(
					SeriesCreate(
						leagueId = league.id,
						id = SeriesID.randomID(),
						numberOfGames = league.numberOfGames ?: Series.DEFAULT_NUMBER_OF_GAMES,
						date = Clock.System.now().toLocalDate(),
						appliedDate = null,
						preBowl = SeriesPreBowl.REGULAR,
						excludeFromStatistics = ExcludeFromStatistics.INCLUDE,
						alleyId = null,
						manualScores = null,
					),
				)
			}
		}
	}

	override suspend fun insertAllLeagues(leagues: List<LeagueCreate>) = withContext(ioDispatcher) {
		transactionRunner {
			leagues.forEach { insertLeague(it) }
		}
	}

	override suspend fun updateLeague(league: LeagueUpdate) = withContext(ioDispatcher) {
		leagueDao.updateLeague(league.asEntity())
	}

	override suspend fun archiveLeague(id: LeagueID) = withContext(ioDispatcher) {
		leagueDao.archiveLeague(id, archivedOn = Clock.System.now())
	}

	override suspend fun unarchiveLeague(id: LeagueID) = withContext(ioDispatcher) {
		leagueDao.unarchiveLeague(id)
	}
}
