package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.ArchivedSeries
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesCreate
import ca.josephroque.bowlingcompanion.core.model.SeriesDetails
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.SeriesLeagueUpdate
import ca.josephroque.bowlingcompanion.core.model.SeriesListItem
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.core.model.SeriesSortOrder
import ca.josephroque.bowlingcompanion.core.model.SeriesUpdate
import ca.josephroque.bowlingcompanion.core.model.ShareableSeries
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface SeriesRepository {
	fun getSeriesDetails(seriesId: SeriesID): Flow<SeriesDetails>
	fun getSeriesList(leagueId: LeagueID, sortOrder: SeriesSortOrder, preBowl: SeriesPreBowl?): Flow<List<SeriesListItem>>
	fun getArchivedSeries(): Flow<List<ArchivedSeries>>
	fun getEventSeriesIdsList(eventIds: List<LeagueID>): Flow<List<SeriesID>>
	fun getTeamSeriesIds(teamSeriesId: TeamSeriesID): Flow<List<SeriesID>>
	fun getShareableSeries(seriesId: SeriesID): Flow<ShareableSeries>

	suspend fun setSeriesAlley(seriesId: SeriesID, alleyId: AlleyID?)

	suspend fun addGameToSeries(seriesId: SeriesID)

	suspend fun usePreBowl(id: SeriesID, date: LocalDate)

	suspend fun insertSeries(series: SeriesCreate)
	suspend fun updateSeries(series: SeriesUpdate)
	suspend fun updateSeriesLeague(series: SeriesLeagueUpdate)

	suspend fun archiveSeries(id: SeriesID)
	suspend fun unarchiveSeries(id: SeriesID)
}
