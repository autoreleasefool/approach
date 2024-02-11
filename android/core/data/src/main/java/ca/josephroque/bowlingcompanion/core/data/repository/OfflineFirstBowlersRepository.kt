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
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.model.OpponentListItem
import ca.josephroque.bowlingcompanion.core.model.SeriesBowlerSummary
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

class OfflineFirstBowlersRepository @Inject constructor(
	private val bowlerDao: BowlerDao,
	private val leaguesRepository: LeaguesRepository,
	private val recentlyUsedRepository: RecentlyUsedRepository,
	@Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
): BowlersRepository {
	override fun getBowlerSummary(bowlerId: UUID): Flow<BowlerSummary> =
		bowlerDao.getBowlerSummary(bowlerId)

	override fun getBowlerDetails(bowlerId: UUID): Flow<BowlerDetails> =
		bowlerDao.getBowlerDetails(bowlerId)

	override fun getSeriesBowlers(series: List<UUID>): Flow<List<BowlerSummary>> =
		bowlerDao.getSeriesBowlers(series = series)
			.map {
				it
					.sortedBy { seriesBowler -> series.indexOf(seriesBowler.seriesId) }
					.map(SeriesBowlerSummary::asSummary)
			}


	override fun getBowlersList(): Flow<List<BowlerListItem>> =
		bowlerDao.getBowlersList()

	override fun getOpponentsList(): Flow<List<OpponentListItem>> =
		bowlerDao.getOpponentsList()

	override fun getArchivedBowlers(): Flow<List<ArchivedBowler>> =
		bowlerDao.getArchivedBowlers()

	override suspend fun getDefaultQuickPlay(): Pair<BowlerSummary, LeagueSummary>? {
		val recentBowlerIdStr = recentlyUsedRepository
			.observeRecentlyUsed(RecentResource.BOWLERS)
			.first()
			.firstOrNull() ?: return null

		val recentBowlerId = UUID.fromString(recentBowlerIdStr)
		val recentBowler = getBowlerSummary(recentBowlerId).first()
		val bowlerLeagues = leaguesRepository.getLeaguesList(
			recentBowlerId,
			recurrence = LeagueRecurrence.REPEATING,
		).first()
		val bowlerLeagueIds = bowlerLeagues.map(LeagueListItem::id).toSet()

		val recentLeagues = recentlyUsedRepository
			.observeRecentlyUsed(RecentResource.LEAGUES)
			.first()
			.map { UUID.fromString(it) }

		val recentLeague = recentLeagues.firstOrNull { it in bowlerLeagueIds }
			?.let { leaguesRepository.getLeagueSummary(it).first() } ?: return null

		return recentBowler to recentLeague
	}

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