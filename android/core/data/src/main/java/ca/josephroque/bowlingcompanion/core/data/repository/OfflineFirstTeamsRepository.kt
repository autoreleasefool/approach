package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.dao.TeamDao
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.model.Team
import javax.inject.Inject

class OfflineFirstTeamsRepository @Inject constructor(
	private val teamDao: TeamDao,
): TeamsRepository {
	override suspend fun insertTeams(teams: List<Team>) {
		teamDao.insertAll(teams.map(Team::asEntity))
	}
}