package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.ArchivedSeries
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesCreate
import ca.josephroque.bowlingcompanion.core.model.SeriesDetails
import ca.josephroque.bowlingcompanion.core.model.SeriesListItem
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.core.model.SeriesSortOrder
import ca.josephroque.bowlingcompanion.core.model.SeriesUpdate
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface SeriesRepository {
	fun getSeriesDetails(seriesId: UUID): Flow<SeriesDetails>
	fun getSeriesList(
		leagueId: LeagueID,
		sortOrder: SeriesSortOrder,
		preBowl: SeriesPreBowl?,
	): Flow<List<SeriesListItem>>
	fun getArchivedSeries(): Flow<List<ArchivedSeries>>

	suspend fun setSeriesAlley(seriesId: UUID, alleyId: UUID?)

	suspend fun addGameToSeries(seriesId: UUID)
	suspend fun insertSeries(series: SeriesCreate)
	suspend fun updateSeries(series: SeriesUpdate)
	suspend fun archiveSeries(id: UUID)
	suspend fun unarchiveSeries(id: UUID)
	suspend fun usePreBowl(id: UUID, date: LocalDate)
}
