package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.ArchivedLeague
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.LeagueCreate
import ca.josephroque.bowlingcompanion.core.model.LeagueDetails
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.LeagueSortOrder
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.model.LeagueUpdate
import kotlinx.coroutines.flow.Flow

interface LeaguesRepository {
	fun getLeagueBowler(id: LeagueID): Flow<BowlerSummary>
	fun getLeagueSummary(id: LeagueID): Flow<LeagueSummary>
	fun getLeagueDetails(id: LeagueID): Flow<LeagueDetails>

	fun getLeaguesList(
		bowlerId: BowlerID,
		recurrence: LeagueRecurrence? = null,
		sortOrder: LeagueSortOrder = LeagueSortOrder.ALPHABETICAL,
	): Flow<List<LeagueListItem>>

	fun getArchivedLeagues(): Flow<List<ArchivedLeague>>

	suspend fun insertLeague(league: LeagueCreate)
	suspend fun updateLeague(league: LeagueUpdate)
	suspend fun archiveLeague(id: LeagueID)
	suspend fun unarchiveLeague(id: LeagueID)
}
