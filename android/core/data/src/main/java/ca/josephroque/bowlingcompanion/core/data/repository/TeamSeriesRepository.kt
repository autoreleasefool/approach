package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.ArchivedTeamSeries
import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesConnect
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesCreate
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesDetails
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesSortOrder
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesSummary
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesUpdate
import kotlinx.coroutines.flow.Flow

interface TeamSeriesRepository {
	fun getTeamSeriesList(
		teamId: TeamID,
		sortOrder: TeamSeriesSortOrder,
	): Flow<List<TeamSeriesSummary>>
	fun getArchivedTeamSeries(): Flow<List<ArchivedTeamSeries>>

	suspend fun getTeamSeriesDetails(teamSeriesId: TeamSeriesID): TeamSeriesDetails?

	suspend fun insertTeamSeries(teamSeries: TeamSeriesCreate)
	suspend fun insertTeamSeries(teamSeries: TeamSeriesConnect)
	suspend fun updateTeamSeries(teamSeries: TeamSeriesUpdate)

	suspend fun archiveTeamSeries(teamSeriesId: TeamSeriesID, archiveMemberSeries: Boolean)
	suspend fun unarchiveTeamSeries(teamSeriesId: TeamSeriesID)
}
