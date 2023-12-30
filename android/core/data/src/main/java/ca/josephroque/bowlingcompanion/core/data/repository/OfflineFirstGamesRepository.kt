package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.database.dao.BowlerDao
import ca.josephroque.bowlingcompanion.core.database.dao.GameDao
import ca.josephroque.bowlingcompanion.core.database.dao.GearDao
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunner
import ca.josephroque.bowlingcompanion.core.database.model.FrameEntity
import ca.josephroque.bowlingcompanion.core.database.model.GameGearCrossRef
import ca.josephroque.bowlingcompanion.core.database.model.GameLaneCrossRef
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.model.ArchivedGame
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.FrameCreate
import ca.josephroque.bowlingcompanion.core.model.Game
import ca.josephroque.bowlingcompanion.core.model.GameCreate
import ca.josephroque.bowlingcompanion.core.model.GameEdit
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class OfflineFirstGamesRepository @Inject constructor(
	private val bowlerDao: BowlerDao,
	private val gameDao: GameDao,
	private val gearDao: GearDao,
	private val framesRepository: FramesRepository,
	private val transactionRunner: TransactionRunner,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
): GamesRepository {
	override fun getGameDetails(gameId: UUID): Flow<GameEdit> =
		gameDao.getGameDetails(gameId).map { it.asModel() }

	override fun getGamesList(seriesId: UUID): Flow<List<GameListItem>> =
		gameDao.getGamesList(seriesId)

	override fun getGameIds(seriesId: UUID): Flow<List<UUID>> =
		gameDao.getGameIds(seriesId)

	override fun getArchivedGames(): Flow<List<ArchivedGame>> =
		gameDao.getArchivedGames()

	override fun getGameIndex(gameId: UUID): Flow<Int> =
		gameDao.getGameIndex(gameId)

	override suspend fun setGameScoringMethod(
		gameId: UUID,
		scoringMethod: GameScoringMethod,
		score: Int
	) = withContext(ioDispatcher) {
		gameDao.setGameScoringMethod(gameId, scoringMethod, score)
	}

	override suspend fun setGameExcludedFromStatistics(
		gameId: UUID,
		excludeFromStatistics: ExcludeFromStatistics
	) = withContext(ioDispatcher) {
		gameDao.setGameExcludedFromStatistics(gameId, excludeFromStatistics)
	}

	override suspend fun setGameLockState(gameId: UUID, locked: GameLockState) = withContext(ioDispatcher) {
		gameDao.setGameLockState(gameId, locked)
	}

	override suspend fun setGameScore(gameId: UUID, score: Int) = withContext(ioDispatcher) {
		gameDao.setGameScore(gameId, score)
	}

	override suspend fun setGameLanes(gameId: UUID, lanes: Set<UUID>) = withContext(ioDispatcher) {
		transactionRunner {
			gameDao.deleteGameLanes(gameId)
			gameDao.insertGameLanes(lanes.map { GameLaneCrossRef(gameId, it) })
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

			val bowler = bowlerDao.getSeriesBowler(games.first().seriesId).first()
			val preferredGear = gearDao.getBowlerPreferredGear(bowler.id).first()
			games.forEach { game ->
				gearDao.setGameGear(preferredGear.map { GameGearCrossRef(gameId = game.id, gearId = it.id) })
			}
		}
	}

	override suspend fun archiveGame(gameId: UUID) = withContext(ioDispatcher) {
		gameDao.archiveGame(gameId)
	}

	override suspend fun unarchiveGame(gameId: UUID) = withContext(ioDispatcher) {
		gameDao.unarchiveGame(gameId)
	}
}