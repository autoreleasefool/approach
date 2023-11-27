package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.database.dao.SeriesDao
import ca.josephroque.bowlingcompanion.core.database.model.SeriesCreate
import ca.josephroque.bowlingcompanion.core.database.model.SeriesDetails
import ca.josephroque.bowlingcompanion.core.database.model.SeriesListItem
import ca.josephroque.bowlingcompanion.core.database.model.SeriesUpdate
import ca.josephroque.bowlingcompanion.core.model.ArchivedSeries
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

class OfflineFirstSeriesRepository @Inject constructor(
	private val seriesDao: SeriesDao,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
): SeriesRepository {
	override fun getSeriesDetails(seriesId: UUID): Flow<SeriesDetails> =
		seriesDao.getSeriesDetails(seriesId)

	override fun getSeriesList(leagueId: UUID): Flow<List<SeriesListItem>> =
		seriesDao.getSeriesList(leagueId)

	override fun getArchivedSeries(): Flow<List<ArchivedSeries>> =
		seriesDao.getArchivedSeries()

	override suspend fun insertSeries(series: SeriesCreate) = withContext(ioDispatcher) {
		seriesDao.insertSeries(series)
	}

	override suspend fun updateSeries(series: SeriesUpdate) = withContext(ioDispatcher) {
		seriesDao.updateSeries(series)
	}

	override suspend fun deleteSeries(id: UUID) = withContext(ioDispatcher) {
		seriesDao.deleteSeries(id)
	}

	override suspend fun archiveSeries(id: UUID) = withContext(ioDispatcher) {
		seriesDao.archiveSeries(id, archivedOn = Clock.System.now())
	}

	override suspend fun unarchiveSeries(id: UUID) = withContext(ioDispatcher) {
		seriesDao.unarchiveSeries(id)
	}
}