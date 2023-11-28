package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.model.SeriesCreate
import ca.josephroque.bowlingcompanion.core.database.model.SeriesListItem
import ca.josephroque.bowlingcompanion.core.database.model.SeriesUpdate
import ca.josephroque.bowlingcompanion.core.model.ArchivedSeries
import ca.josephroque.bowlingcompanion.core.model.SeriesDetails
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface SeriesRepository {
	fun getSeriesDetails(seriesId: UUID): Flow<SeriesDetails>
	fun getSeriesList(leagueId: UUID): Flow<List<SeriesListItem>>
	fun getArchivedSeries(): Flow<List<ArchivedSeries>>

	suspend fun insertSeries(series: SeriesCreate)
	suspend fun updateSeries(series: SeriesUpdate)
	suspend fun deleteSeries(id: UUID)
	suspend fun archiveSeries(id: UUID)
	suspend fun unarchiveSeries(id: UUID)
}