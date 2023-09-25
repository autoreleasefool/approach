package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.dao.SeriesDao
import ca.josephroque.bowlingcompanion.core.model.SeriesCreate
import ca.josephroque.bowlingcompanion.core.model.SeriesListItem
import ca.josephroque.bowlingcompanion.core.model.SeriesUpdate
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class OfflineFirstSeriesRepository @Inject constructor(
	private val seriesDao: SeriesDao,
): SeriesRepository {
	override fun getSeriesList(leagueId: UUID): Flow<List<SeriesListItem>> =
		seriesDao.getSeriesList(leagueId)


	override suspend fun insertSeries(series: SeriesCreate) {
		seriesDao.insertSeries(series)
	}

	override suspend fun updateSeries(series: SeriesUpdate) {
		seriesDao.updateSeries(series)
	}

	override suspend fun deleteSeries(id: UUID) {
		seriesDao.deleteSeries(id)
	}
}