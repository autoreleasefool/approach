package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.dao.LeagueDao
import ca.josephroque.bowlingcompanion.core.database.model.LeagueEntity
import ca.josephroque.bowlingcompanion.core.database.model.LeagueWithAverage
import ca.josephroque.bowlingcompanion.core.database.model.asLeagueDetails
import ca.josephroque.bowlingcompanion.core.database.model.asListItem
import ca.josephroque.bowlingcompanion.core.model.LeagueCreate
import ca.josephroque.bowlingcompanion.core.model.LeagueDetails
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import ca.josephroque.bowlingcompanion.core.model.LeagueUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class OfflineFirstLeaguesRepository @Inject constructor(
	private val leagueDao: LeagueDao,
): LeaguesRepository {
	override fun getLeagueDetails(id: UUID): Flow<LeagueDetails> =
		leagueDao.getLeagueDetails(id)
			.map(LeagueEntity::asLeagueDetails)

	override fun getLeaguesList(bowlerId: UUID): Flow<List<LeagueListItem>> =
		leagueDao.getLeagueAverages(bowlerId = bowlerId)
			.map { it.map(LeagueWithAverage::asListItem) }

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