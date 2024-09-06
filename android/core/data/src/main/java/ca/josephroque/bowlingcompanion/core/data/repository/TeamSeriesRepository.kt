package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.SeriesSortOrder
import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesConnect
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesCreate
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesSummary
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesUpdate
import kotlinx.coroutines.flow.Flow

interface TeamSeriesRepository {
	fun getTeamSeriesList(teamId: TeamID, sortOrder: SeriesSortOrder): Flow<List<TeamSeriesSummary>>

	suspend fun insertTeamSeries(teamSeries: TeamSeriesCreate)
	suspend fun insertTeamSeries(teamSeries: TeamSeriesConnect)
	suspend fun updateTeamSeries(teamSeries: TeamSeriesUpdate)
}
