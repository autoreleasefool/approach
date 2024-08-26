package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.database.dao.TeamBowlerDao
import ca.josephroque.bowlingcompanion.core.database.dao.TeamDao
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunner
import ca.josephroque.bowlingcompanion.core.database.model.TeamBowlerCrossRef
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.model.TeamCreate
import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TeamListItem
import ca.josephroque.bowlingcompanion.core.model.TeamMemberListItem
import ca.josephroque.bowlingcompanion.core.model.TeamSortOrder
import ca.josephroque.bowlingcompanion.core.model.TeamSummary
import ca.josephroque.bowlingcompanion.core.model.TeamUpdate
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class OfflineFirstTeamsRepository @Inject constructor(
	private val teamDao: TeamDao,
	private val teamBowlerDao: TeamBowlerDao,
	private val transactionRunner: TransactionRunner,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : TeamsRepository {
	override fun getTeamList(sortOrder: TeamSortOrder): Flow<List<TeamListItem>> =
		teamDao.getTeamList(sortOrder)

	override fun getTeamMembers(bowlerIds: List<BowlerID>): Flow<List<TeamMemberListItem>> =
		teamBowlerDao.getTeamMembers(bowlerIds)

	override fun getTeamSummary(id: TeamID): Flow<TeamSummary> = teamDao.getTeamSummary(id)

	override fun getTeamMembers(teamId: TeamID): Flow<List<TeamMemberListItem>> =
		teamDao.getTeamMembers(teamId)

	override suspend fun insertTeam(team: TeamCreate) = withContext(ioDispatcher) {
		transactionRunner {
			teamDao.insertTeam(team.asEntity())
			teamBowlerDao.setTeamBowlers(
				team.members.mapIndexed { index, member -> TeamBowlerCrossRef(team.id, member.id, index) },
			)
		}
	}

	override suspend fun updateTeam(team: TeamUpdate) = withContext(ioDispatcher) {
		transactionRunner {
			teamDao.updateTeam(team.asEntity())
			teamBowlerDao.deleteTeamBowlers(team.id)
			teamBowlerDao.setTeamBowlers(
				team.members.mapIndexed { index, member -> TeamBowlerCrossRef(team.id, member.id, index) },
			)
		}
	}

	override suspend fun deleteTeam(id: TeamID) = withContext(ioDispatcher) {
		teamDao.deleteTeam(id)
	}
}
