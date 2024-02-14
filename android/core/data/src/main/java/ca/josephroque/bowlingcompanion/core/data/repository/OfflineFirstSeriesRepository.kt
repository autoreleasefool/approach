package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers.IO
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.database.dao.GameDao
import ca.josephroque.bowlingcompanion.core.database.dao.SeriesDao
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunner
import ca.josephroque.bowlingcompanion.core.database.model.SeriesDetailsEntity
import ca.josephroque.bowlingcompanion.core.database.model.SeriesListEntity
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.model.ArchivedSeries
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameCreate
import ca.josephroque.bowlingcompanion.core.model.SeriesCreate
import ca.josephroque.bowlingcompanion.core.model.SeriesDetails
import ca.josephroque.bowlingcompanion.core.model.SeriesListItem
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.core.model.SeriesSortOrder
import ca.josephroque.bowlingcompanion.core.model.SeriesUpdate
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class OfflineFirstSeriesRepository @Inject constructor(
	private val seriesDao: SeriesDao,
	private val gameDao: GameDao,
	private val gamesRepository: GamesRepository,
	private val transactionRunner: TransactionRunner,
	@Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : SeriesRepository {
	override fun getSeriesDetails(seriesId: UUID): Flow<SeriesDetails> =
		seriesDao.getSeriesDetails(seriesId).map(SeriesDetailsEntity::asModel)

	override fun getSeriesList(
		leagueId: UUID,
		sortOrder: SeriesSortOrder,
	): Flow<List<SeriesListItem>> =
		seriesDao.getSeriesList(leagueId, sortOrder).map { it.map(SeriesListEntity::asModel) }

	override fun getArchivedSeries(): Flow<List<ArchivedSeries>> = seriesDao.getArchivedSeries()

	override suspend fun insertSeries(series: SeriesCreate) = withContext(ioDispatcher) {
		transactionRunner {
			seriesDao.insertSeries(series.asEntity())
			val games = (0..<series.numberOfGames).map { index ->
				GameCreate(
					id = UUID.randomUUID(),
					seriesId = series.id,
					index = index,
					excludeFromStatistics = series.excludeFromStatistics,
				)
			}

			gamesRepository.insertGames(games)
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

	override suspend fun addGameToSeries(seriesId: UUID) = withContext(ioDispatcher) {
		transactionRunner {
			val existingGames = gamesRepository.getGamesList(seriesId).first()

			val game = GameCreate(
				id = UUID.randomUUID(),
				seriesId = seriesId,
				index = if (existingGames.isEmpty()) 0 else existingGames.maxOf { it.index } + 1,
			)

			gamesRepository.insertGames(listOf(game))
		}
	}

	override suspend fun updateSeries(series: SeriesUpdate) = withContext(ioDispatcher) {
		transactionRunner {
			val existingSeries = seriesDao.getSeriesDetails(series.id).first()

			if (existingSeries.properties.preBowl != series.preBowl) {
				val games = gameDao.getGamesList(series.id).first()
				games.forEach {
					gameDao.setGameExcludedFromStatistics(
						gameId = it.id,
						excludeFromStatistics = when (series.preBowl) {
							SeriesPreBowl.PRE_BOWL -> ExcludeFromStatistics.EXCLUDE
							SeriesPreBowl.REGULAR -> ExcludeFromStatistics.INCLUDE
						},
					)
				}
			}

			seriesDao.updateSeries(series.asEntity())
		}
	}

	override suspend fun archiveSeries(id: UUID) = withContext(ioDispatcher) {
		seriesDao.archiveSeries(id, archivedOn = Clock.System.now())
	}

	override suspend fun unarchiveSeries(id: UUID) = withContext(ioDispatcher) {
		seriesDao.unarchiveSeries(id)
	}
}
