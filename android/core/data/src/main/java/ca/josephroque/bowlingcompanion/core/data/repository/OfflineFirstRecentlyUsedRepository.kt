package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers.IO
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OfflineFirstRecentlyUsedRepository @Inject constructor(
	private val userDataRepository: UserDataRepository,
	@Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : RecentlyUsedRepository {
	override suspend fun didRecentlyUse(resource: RecentResource, id: String) {
		withContext(ioDispatcher) {
			when (resource) {
				RecentResource.BOWLERS, RecentResource.OPPONENTS -> userDataRepository.didRecentlyUseBowler(id)
				RecentResource.LEAGUES -> userDataRepository.didRecentlyUseLeague(id)
				RecentResource.GEAR -> userDataRepository.didRecentlyUseGear(id)
				RecentResource.ALLEYS -> userDataRepository.didRecentlyUseAlley(id)
				RecentResource.TEAM -> userDataRepository.didRecentlyUseTeam(id)
			}
		}
	}

	override fun observeRecentlyUsed(resource: RecentResource): Flow<List<String>> =
		userDataRepository.userData.map {
			when (resource) {
				RecentResource.ALLEYS -> it.recentlyUsedAlleyIds
				RecentResource.BOWLERS, RecentResource.OPPONENTS -> it.recentlyUsedBowlerIds
				RecentResource.GEAR -> it.recentlyUsedGearIds
				RecentResource.LEAGUES -> it.recentlyUsedLeagueIds
				RecentResource.TEAM -> it.recentlyUsedTeamIds
			}
		}.distinctUntilChanged()
}
