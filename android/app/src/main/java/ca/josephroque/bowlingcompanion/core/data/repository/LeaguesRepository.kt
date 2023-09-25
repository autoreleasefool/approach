package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.LeagueCreate
import ca.josephroque.bowlingcompanion.core.model.LeagueDetails
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import ca.josephroque.bowlingcompanion.core.model.LeagueUpdate
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface LeaguesRepository {
	fun getLeagueDetails(id: UUID): Flow<LeagueDetails>

	fun getLeaguesList(bowlerId: UUID): Flow<List<LeagueListItem>>

	suspend fun insertLeague(league: LeagueCreate)
	suspend fun updateLeague(league: LeagueUpdate)
	suspend fun deleteLeague(id: UUID)
}