package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers.IO
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.database.dao.BowlerDao
import ca.josephroque.bowlingcompanion.core.database.dao.LeagueDao
import ca.josephroque.bowlingcompanion.core.database.dao.MatchPlayDao
import ca.josephroque.bowlingcompanion.core.database.dao.TeamDao
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunner
import ca.josephroque.bowlingcompanion.core.database.model.BowlerEntity
import ca.josephroque.bowlingcompanion.core.database.model.LeagueCreateEntity
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.model.ArchivedBowler
import ca.josephroque.bowlingcompanion.core.model.BowlerCreate
import ca.josephroque.bowlingcompanion.core.model.BowlerDetails
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import ca.josephroque.bowlingcompanion.core.model.BowlerSortOrder
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.BowlerUpdate
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.model.OpponentListItem
import ca.josephroque.bowlingcompanion.core.model.SeriesBowlerSummary
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamID
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class OfflineFirstBowlersRepository @Inject constructor(
	private val teamDao: TeamDao,
	private val bowlerDao: BowlerDao,
	private val leagueDao: LeagueDao,
	private val matchPlayDao: MatchPlayDao,
	private val leaguesRepository: LeaguesRepository,
	private val recentlyUsedRepository: RecentlyUsedRepository,
	private val transactionRunner: TransactionRunner,
	@Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : BowlersRepository {
	override fun getBowlerSummary(bowlerId: BowlerID): Flow<BowlerSummary> = bowlerDao.getBowlerSummary(bowlerId)

	override fun getBowlerDetails(bowlerId: BowlerID): Flow<BowlerDetails> = bowlerDao.getBowlerDetails(bowlerId)

	override fun getSeriesBowlers(series: List<SeriesID>): Flow<List<BowlerSummary>> =
		bowlerDao.getSeriesBowlers(series = series)
			.map {
				it
					.sortedBy { seriesBowler -> series.indexOf(seriesBowler.seriesId) }
					.map(SeriesBowlerSummary::asSummary)
			}

	override fun getTeamBowlers(teamId: TeamID): Flow<List<BowlerSummary>> = teamDao.getTeamMembers(teamId)
		.map { members -> members.map { BowlerSummary(id = it.id, name = it.name) } }

	override fun getBowlersList(kind: BowlerKind?, sortOrder: BowlerSortOrder): Flow<List<BowlerListItem>> =
		bowlerDao.getBowlersList(kind = kind, sortOrder = sortOrder)

	override fun getOpponentsList(): Flow<List<OpponentListItem>> = bowlerDao.getOpponentsList()

	override fun getArchivedBowlers(): Flow<List<ArchivedBowler>> = bowlerDao.getArchivedBowlers()

	override suspend fun getDefaultQuickPlay(): Pair<BowlerSummary, LeagueSummary>? {
		val recentBowlerIdStr = recentlyUsedRepository
			.observeRecentlyUsed(RecentResource.BOWLERS)
			.first()
			.firstOrNull() ?: return null

		val recentBowlerId = BowlerID.fromString(recentBowlerIdStr)
		val recentBowler = getBowlerSummary(recentBowlerId).first()
		val bowlerLeagues = leaguesRepository.getLeaguesList(
			recentBowlerId,
			recurrence = LeagueRecurrence.REPEATING,
		).first()
		val bowlerLeagueIds = bowlerLeagues.map(LeagueListItem::id).toSet()

		val recentLeagues = recentlyUsedRepository
			.observeRecentlyUsed(RecentResource.LEAGUES)
			.first()
			.map { LeagueID.fromString(it) }

		val recentLeague = recentLeagues.firstOrNull { it in bowlerLeagueIds }
			?.let { leaguesRepository.getLeagueSummary(it).first() } ?: return null

		return recentBowler to recentLeague
	}

	override suspend fun insertBowler(bowler: BowlerCreate) = withContext(ioDispatcher) {
		bowlerDao.insertBowler(bowler.asEntity())
		leagueDao.insertLeague(
			LeagueCreateEntity(
				bowlerId = bowler.id,
				id = LeagueID.randomID(),
				name = "Practice",
				recurrence = LeagueRecurrence.REPEATING,
				excludeFromStatistics = ExcludeFromStatistics.EXCLUDE,
				additionalGames = null,
				additionalPinFall = null,
				numberOfGames = null,
			),
		)
	}

	override suspend fun updateBowler(bowler: BowlerUpdate) = withContext(ioDispatcher) {
		bowlerDao.updateBowler(bowler.asEntity())
	}

	override suspend fun archiveBowler(bowlerId: BowlerID) = withContext(ioDispatcher) {
		bowlerDao.archiveBowlers(listOf(bowlerId), archivedOn = Clock.System.now())
	}

	override suspend fun unarchiveBowler(bowlerId: BowlerID) = withContext(ioDispatcher) {
		bowlerDao.unarchiveBowler(bowlerId)
	}

	override suspend fun hasOpponents(): Boolean = withContext(ioDispatcher) {
		bowlerDao.getOpponentsList().first().any { it.kind == BowlerKind.OPPONENT }
	}

	override suspend fun mergeBowlers(bowlers: List<BowlerEntity>, associateBy: Map<BowlerID, BowlerID>) =
		withContext(ioDispatcher) {
			transactionRunner {
				for ((oldId, newId) in associateBy) {
					matchPlayDao.replaceOpponentId(oldId, newId)
				}

				val bowlersToArchive = associateBy.keys - associateBy.values.toSet()
				bowlerDao.archiveBowlers(bowlersToArchive.toList(), archivedOn = Clock.System.now())

				val updatedBowlerIds = associateBy.values.toSet()
				val bowlersToUpdate = bowlers.filter { it.id in updatedBowlerIds }
				for (bowler in bowlersToUpdate) {
					bowlerDao.updateBowlerEntity(bowler)
				}
			}
		}
}
