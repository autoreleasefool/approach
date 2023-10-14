package ca.josephroque.bowlingcompanion.core.data.repository

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
}