package ca.josephroque.bowlingcompanion.core.data.repository

import kotlinx.coroutines.flow.Flow
import java.util.UUID

enum class RecentResource {
	BOWLERS,
	LEAGUES,
	ALLEYS,
	GEAR,
	OPPONENTS,
}

interface RecentlyUsedRepository {
	suspend fun didRecentlyUse(resource: RecentResource, id: String)
	fun observeRecentlyUsed(resource: RecentResource): Flow<List<String>>

	suspend fun didRecentlyUseBowler(id: UUID) = didRecentlyUse(RecentResource.BOWLERS, id.toString())
	suspend fun didRecentlyUseLeague(id: UUID) = didRecentlyUse(RecentResource.LEAGUES, id.toString())
	suspend fun didRecentlyUseAlley(id: UUID) = didRecentlyUse(RecentResource.ALLEYS, id.toString())
	suspend fun didRecentlyUseGear(id: UUID) = didRecentlyUse(RecentResource.GEAR, id.toString())
	suspend fun didRecentlyUseOpponent(id: UUID) = didRecentlyUse(RecentResource.OPPONENTS, id.toString())
}