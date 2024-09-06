package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers.IO
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.database.dao.TeamSeriesDao
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunner
import ca.josephroque.bowlingcompanion.core.database.model.TeamSeriesCreateEntity
import ca.josephroque.bowlingcompanion.core.database.model.TeamSeriesSeriesCrossRef
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.model.SeriesCreate
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.SeriesSortOrder
import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesConnect
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesCreate
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesSummary
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesUpdate
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class OfflineFirstTeamSeriesRepository @Inject constructor(
	private val teamSeriesDao: TeamSeriesDao,
	private val seriesRepository: SeriesRepository,
	private val transactionRunner: TransactionRunner,
	@Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : TeamSeriesRepository {
	override fun getTeamSeriesList(
		teamId: TeamID,
		sortOrder: SeriesSortOrder,
	): Flow<List<TeamSeriesSummary>> = teamSeriesDao.getTeamSeriesList(teamId, sortOrder)

	override suspend fun insertTeamSeries(teamSeries: TeamSeriesConnect) = withContext(ioDispatcher) {
		transactionRunner {
			teamSeriesDao.insertSeries(
				TeamSeriesCreateEntity(
					id = teamSeries.id,
					teamId = teamSeries.teamId,
					date = teamSeries.date,
				),
			)

			val teamSeriesSeries = teamSeries.seriesIds.mapIndexed { index, seriesId ->
				TeamSeriesSeriesCrossRef(
					teamSeriesId = teamSeries.id,
					seriesId = seriesId,
					position = index,
				)
			}

			teamSeriesDao.insertAll(teamSeriesSeries)
		}
	}

	override suspend fun insertTeamSeries(teamSeries: TeamSeriesCreate) = withContext(ioDispatcher) {
		transactionRunner {
			teamSeriesDao.insertSeries(teamSeries.asEntity())
			val series = teamSeries.leagues.map { leagueId ->
				SeriesCreate(
					id = SeriesID.randomID(),
					leagueId = leagueId,
					date = teamSeries.date,
					preBowl = teamSeries.preBowl,
					numberOfGames = teamSeries.numberOfGames,
					manualScores = teamSeries.manualScores?.get(leagueId),
					excludeFromStatistics = teamSeries.excludeFromStatistics,
					appliedDate = null,
					alleyId = teamSeries.alleyId,
				)
			}

			val teamSeriesSeries = series.mapIndexed { index, it ->
				seriesRepository.insertSeries(it)
				TeamSeriesSeriesCrossRef(
					teamSeriesId = teamSeries.id,
					seriesId = it.id,
					position = index,
				)
			}

			teamSeriesDao.insertAll(teamSeriesSeries)
		}
	}

	override suspend fun updateTeamSeries(teamSeries: TeamSeriesUpdate) = withContext(ioDispatcher) {
		transactionRunner {
			val teamSeriesSeries = teamSeries.seriesIds.mapIndexed { index, it ->
				TeamSeriesSeriesCrossRef(
					teamSeriesId = teamSeries.id,
					seriesId = it,
					position = index,
				)
			}

			teamSeriesDao.deleteSeries(teamSeries.id)
			teamSeriesDao.insertAll(teamSeriesSeries)
		}
	}
}
