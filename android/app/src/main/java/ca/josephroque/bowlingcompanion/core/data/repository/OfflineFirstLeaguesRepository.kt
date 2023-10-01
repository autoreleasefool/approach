package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.dao.LeagueDao
import ca.josephroque.bowlingcompanion.core.database.model.LeagueCreate
import ca.josephroque.bowlingcompanion.core.database.model.LeagueUpdate
import ca.josephroque.bowlingcompanion.core.model.LeagueDetails
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class OfflineFirstLeaguesRepository @Inject constructor(
	private val leagueDao: LeagueDao,
): LeaguesRepository {
	override fun getLeagueDetails(id: UUID): Flow<LeagueDetails> =
		leagueDao.getLeagueDetails(id)

	override fun getLeaguesList(bowlerId: UUID): Flow<List<LeagueListItem>> =
		leagueDao.getLeagueAverages(bowlerId = bowlerId)

	override suspend fun insertLeague(league: LeagueCreate) {
		leagueDao.insertLeague(league)
	}

	override suspend fun updateLeague(league: LeagueUpdate) {
		leagueDao.updateLeague(league)
	}

	override suspend fun deleteLeague(id: UUID) {
		leagueDao.deleteLeague(id)
	}
}