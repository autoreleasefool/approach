package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.ArchivedLeague
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.LeagueCreate
import ca.josephroque.bowlingcompanion.core.model.LeagueDetails
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.model.LeagueUpdate
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface LeaguesRepository {
	fun getLeagueBowler(id: UUID): Flow<BowlerSummary>
	fun getLeagueSummary(id: UUID): Flow<LeagueSummary>
	fun getLeagueDetails(id: UUID): Flow<LeagueDetails>

	fun getLeaguesList(bowlerId: UUID, recurrence: LeagueRecurrence? = null): Flow<List<LeagueListItem>>

	fun getArchivedLeagues(): Flow<List<ArchivedLeague>>

	suspend fun insertLeague(league: LeagueCreate)
	suspend fun updateLeague(league: LeagueUpdate)
	suspend fun archiveLeague(id: UUID)
	suspend fun unarchiveLeague(id: UUID)
}