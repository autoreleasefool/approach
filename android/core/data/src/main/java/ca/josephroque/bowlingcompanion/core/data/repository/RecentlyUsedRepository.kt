package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.BowlerID
import java.util.UUID
import kotlinx.coroutines.flow.Flow

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

	suspend fun didRecentlyUseBowler(id: BowlerID) =
		didRecentlyUse(RecentResource.BOWLERS, id.value.toString())
	suspend fun didRecentlyUseLeague(id: UUID) = didRecentlyUse(RecentResource.LEAGUES, id.toString())
	suspend fun didRecentlyUseAlley(id: UUID) = didRecentlyUse(RecentResource.ALLEYS, id.toString())
	suspend fun didRecentlyUseGear(id: UUID) = didRecentlyUse(RecentResource.GEAR, id.toString())
	suspend fun didRecentlyUseOpponent(id: BowlerID) =
		didRecentlyUse(RecentResource.OPPONENTS, id.value.toString())
}
