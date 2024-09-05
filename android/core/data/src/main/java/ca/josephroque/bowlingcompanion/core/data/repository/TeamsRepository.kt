package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.TeamCreate
import ca.josephroque.bowlingcompanion.core.model.TeamDetails
import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TeamListItem
import ca.josephroque.bowlingcompanion.core.model.TeamMemberListItem
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamSortOrder
import ca.josephroque.bowlingcompanion.core.model.TeamSummary
import ca.josephroque.bowlingcompanion.core.model.TeamUpdate
import kotlinx.coroutines.flow.Flow

interface TeamsRepository {
	fun getTeamList(sortOrder: TeamSortOrder): Flow<List<TeamListItem>>
	fun getTeamMembers(bowlerIds: List<BowlerID>): Flow<List<TeamMemberListItem>>
	fun getTeamDetails(id: TeamID): Flow<TeamDetails>
	fun getTeamSummary(teamSeries: TeamSeriesID): Flow<TeamSummary>

	suspend fun getTeamUpdate(id: TeamID): TeamUpdate

	suspend fun insertTeam(team: TeamCreate)
	suspend fun updateTeam(team: TeamUpdate)
	suspend fun deleteTeam(id: TeamID)
}
