package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers.IO
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.common.utils.toLocalDate
import ca.josephroque.bowlingcompanion.core.database.dao.BowlerDao
import ca.josephroque.bowlingcompanion.core.database.dao.GameDao
import ca.josephroque.bowlingcompanion.core.database.dao.GearDao
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunner
import ca.josephroque.bowlingcompanion.core.database.model.GameGearCrossRef
import ca.josephroque.bowlingcompanion.core.database.model.GameLaneCrossRef
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.model.ArchivedGame
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.FrameCreate
import ca.josephroque.bowlingcompanion.core.model.Game
import ca.josephroque.bowlingcompanion.core.model.GameCreate
import ca.josephroque.bowlingcompanion.core.model.GameEdit
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GameInProgress
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import ca.josephroque.bowlingcompanion.core.model.GameListItemBySeries
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.LaneID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.ShareableGame
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.model.isGameFinished
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus

class OfflineFirstGamesRepository @Inject constructor(
	private val bowlerDao: BowlerDao,
	private val gameDao: GameDao,
	private val gearDao: GearDao,
	private val framesRepository: FramesRepository,
	private val userDataRepository: UserDataRepository,
	private val transactionRunner: TransactionRunner,
	@Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : GamesRepository {
	override fun getGameDetails(gameId: GameID): Flow<GameEdit> = gameDao.getGameDetails(gameId).map { it.asModel() }

	override fun getGamesList(seriesId: SeriesID): Flow<List<GameListItem>> = gameDao.getGamesList(seriesId)

	override fun getGameIds(seriesId: SeriesID): Flow<List<GameID>> = gameDao.getGameIds(seriesId)

	override fun getTeamSeriesGameIds(teamSeriesId: TeamSeriesID): Flow<List<GameID>> =
		gameDao.getTeamSeriesGameIds(teamSeriesId)

	override fun getGamesFromSeries(series: List<SeriesID>, gameIndex: Int): Flow<List<GameListItemBySeries>> =
		gameDao.getGamesFromSeries(series, gameIndex)
			.map {
				val seriesIndex = series.mapIndexed { index, seriesId -> seriesId to index }.toMap()
				it.sortedBy { game -> seriesIndex[game.seriesId] }
			}

	override fun getArchivedGames(): Flow<List<ArchivedGame>> = gameDao.getArchivedGames()

	override fun getGameIndex(gameId: GameID): Flow<Int> = gameDao.getGameIndex(gameId)

	override fun getShareableGame(gameId: GameID): Flow<ShareableGame> = gameDao.getShareableGame(gameId)
		.map { it.asModel() }

	override suspend fun getGameInProgress(): GameInProgress? {
		val userData = userDataRepository.userData.first()
		val latestGameIdStr = userData.latestGameInEditor
		val latestSeriesIdsStr = userData.latestSeriesInEditor
		val latestTeamSeriesIdStr = userData.latestTeamSeriesInEditor
		if (latestGameIdStr == null || latestSeriesIdsStr.isEmpty()) return null

		val latestGameId = GameID.fromString(latestGameIdStr)
		val latestSeriesIds = latestSeriesIdsStr.map { SeriesID.fromString(it) }
		val latestTeamSeriesId = latestTeamSeriesIdStr?.let { TeamSeriesID.fromString(it) }

		val lastSeriesId = latestSeriesIds.lastOrNull() ?: return null
		val lastSeriesGameId = gameDao.getGameIds(lastSeriesId).first().lastOrNull() ?: return null

		// If user was on the last game, check if it was finished, and skip returning if it was
		if (latestGameId == lastSeriesGameId) {
			val frames = framesRepository.getFrames(lastSeriesGameId).first()
			if (frames.isGameFinished()) return null
		}

		return GameInProgress(
			teamSeriesId = latestTeamSeriesId,
			seriesIds = latestSeriesIds,
			currentGameId = latestGameId,
		)
	}

	override suspend fun setGameScoringMethod(gameId: GameID, scoringMethod: GameScoringMethod, score: Int) =
		withContext(ioDispatcher) {
			gameDao.setGameScoringMethod(gameId, scoringMethod, score)
		}

	override suspend fun setGameExcludedFromStatistics(gameId: GameID, excludeFromStatistics: ExcludeFromStatistics) =
		withContext(ioDispatcher) {
			gameDao.setGameExcludedFromStatistics(gameId, excludeFromStatistics)
		}

	override suspend fun setGameLockState(gameId: GameID, locked: GameLockState) = withContext(
		ioDispatcher,
	) {
		gameDao.setGameLockState(gameId, locked)
	}

	override suspend fun setGameScore(gameId: GameID, score: Int) = withContext(ioDispatcher) {
		gameDao.setGameScore(gameId, score)
	}

	override suspend fun setGameDuration(gameId: GameID, durationMillis: Long) = withContext(ioDispatcher) {
		gameDao.setGameDuration(gameId, durationMillis)
	}

	override suspend fun setGameLanes(gameId: GameID, lanes: Set<LaneID>) = withContext(ioDispatcher) {
		transactionRunner {
			gameDao.deleteGameLanes(gameId)
			gameDao.insertGameLanes(lanes.map { GameLaneCrossRef(gameId, it) })
		}
	}

	override suspend fun setAllGameLanes(seriesId: SeriesID, lanes: Set<LaneID>) = withContext(
		ioDispatcher,
	) {
		transactionRunner {
			val gameIds = gameDao.getGameIds(seriesId).first()
			gameIds.forEach { gameId ->
				gameDao.deleteGameLanes(gameId)
				gameDao.insertGameLanes(lanes.map { GameLaneCrossRef(gameId, it) })
			}
		}
	}

	override suspend fun insertGames(games: List<GameCreate>) = withContext(ioDispatcher) {
		if (games.isEmpty()) return@withContext

		transactionRunner {
			val frames = games.flatMap { game ->
				Game.FrameIndices.map { frameIndex ->
					FrameCreate(
						gameId = game.id,
						index = frameIndex,
					)
				}
			}

			gameDao.insertGames(games.map(GameCreate::asEntity))
			framesRepository.insertFrames(frames)

			val bowler = bowlerDao.getSeriesBowlers(listOf(games.first().seriesId)).first().first()
			val preferredGear = gearDao.getBowlerPreferredGear(bowler.id).first()
			games.forEach { game ->
				gearDao.setGameGear(
					preferredGear.map {
						GameGearCrossRef(gameId = game.id, gearId = it.gearId)
					},
				)
			}
		}
	}

	override suspend fun archiveGame(gameId: GameID) = withContext(ioDispatcher) {
		gameDao.archiveGame(gameId, archivedOn = Clock.System.now())
	}

	override suspend fun unarchiveGame(gameId: GameID) = withContext(ioDispatcher) {
		gameDao.unarchiveGame(gameId)
	}

	override suspend fun lockStaleGames() = withContext(ioDispatcher) {
		val currentDate = Clock.System.now().toLocalDate()
		val staleDate = currentDate.minus(7, DateTimeUnit.DAY)
		gameDao.lockStaleGames(staleDate)
	}
}
