package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.TeamCreate
import ca.josephroque.bowlingcompanion.core.model.TeamListItem
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface TeamsRepository {
	fun getTeamList(): Flow<List<TeamListItem>>

	suspend fun insertTeam(team: TeamCreate)
	suspend fun deleteTeam(id: UUID)
}
