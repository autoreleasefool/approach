package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.database.dao.FrameDao
import ca.josephroque.bowlingcompanion.core.database.dao.GameDao
import ca.josephroque.bowlingcompanion.core.database.dao.SeriesDao
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunner
import ca.josephroque.bowlingcompanion.core.database.model.FrameEntity
import ca.josephroque.bowlingcompanion.core.database.model.GameEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesDetailsEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesListEntity
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.model.ArchivedSeries
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.Game
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.SeriesCreate
import ca.josephroque.bowlingcompanion.core.model.SeriesDetails
import ca.josephroque.bowlingcompanion.core.model.SeriesListItem
import ca.josephroque.bowlingcompanion.core.model.SeriesSortOrder
import ca.josephroque.bowlingcompanion.core.model.SeriesUpdate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

class OfflineFirstSeriesRepository @Inject constructor(
	private val seriesDao: SeriesDao,
	private val gameDao: GameDao,
	private val frameDao: FrameDao,
	private val transactionRunner: TransactionRunner,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
): SeriesRepository {
	override fun getSeriesDetails(seriesId: UUID): Flow<SeriesDetails> =
		seriesDao.getSeriesDetails(seriesId).map(SeriesDetailsEntity::asModel)

	override fun getSeriesList(leagueId: UUID, sortOrder: SeriesSortOrder): Flow<List<SeriesListItem>> =
		seriesDao.getSeriesList(leagueId, sortOrder).map { it.map(SeriesListEntity::asModel) }

	override fun getArchivedSeries(): Flow<List<ArchivedSeries>> =
		seriesDao.getArchivedSeries()

	override suspend fun insertSeries(series: SeriesCreate) = withContext(ioDispatcher) {
		transactionRunner {
			seriesDao.insertSeries(series.asEntity())
			val games = (0..<series.numberOfGames).map { index ->
				GameEntity(
					id = UUID.randomUUID(),
					seriesId = series.id,
					index = index,
					score = 0,
					locked = GameLockState.UNLOCKED,
					scoringMethod = GameScoringMethod.BY_FRAME,
					excludeFromStatistics = ExcludeFromStatistics.INCLUDE,
				)
			}

			val frames = games.flatMap { game ->
				Game.FrameIndices.map { frameIndex ->
					FrameEntity(
						gameId = game.id,
						index = frameIndex,
						roll0 = null,
						roll1 = null,
						roll2 = null,
						ball0 = null,
						ball1 = null,
						ball2 = null,
					)
				}
			}

			gameDao.insertGames(games)
			frameDao.insertFrames(frames)
		}
	}

	override suspend fun setSeriesAlley(seriesId: UUID, alleyId: UUID?) = withContext(ioDispatcher) {
		transactionRunner {
			val series = seriesDao.getSeriesDetails(seriesId).firstOrNull() ?: return@transactionRunner

			if (series.alley?.id == alleyId) {
				return@transactionRunner
			}

			val games = gameDao.getGamesList(seriesId).firstOrNull() ?: return@transactionRunner
			seriesDao.setSeriesAlley(seriesId, alleyId)
			games.forEach {
				gameDao.deleteGameLanes(it.id)
			}
		}
	}

	override suspend fun updateSeries(series: SeriesUpdate) = withContext(ioDispatcher) {
		seriesDao.updateSeries(series.asEntity())
	}

	override suspend fun archiveSeries(id: UUID) = withContext(ioDispatcher) {
		seriesDao.archiveSeries(id, archivedOn = Clock.System.now())
	}

	override suspend fun unarchiveSeries(id: UUID) = withContext(ioDispatcher) {
		seriesDao.unarchiveSeries(id)
	}
}