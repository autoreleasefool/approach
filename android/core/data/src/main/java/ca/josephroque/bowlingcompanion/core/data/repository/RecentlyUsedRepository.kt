package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GearID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import kotlinx.coroutines.flow.Flow

enum class RecentResource {
	BOWLERS,
	LEAGUES,
	ALLEYS,
	GEAR,
	OPPONENTS,
	TEAM,
}

interface RecentlyUsedRepository {
	suspend fun didRecentlyUse(resource: RecentResource, id: String)
	fun observeRecentlyUsed(resource: RecentResource): Flow<List<String>>

	suspend fun didRecentlyUseBowler(id: BowlerID) =
		didRecentlyUse(RecentResource.BOWLERS, id.toString())
	suspend fun didRecentlyUseLeague(id: LeagueID) =
		didRecentlyUse(RecentResource.LEAGUES, id.toString())
	suspend fun didRecentlyUseAlley(id: AlleyID) = didRecentlyUse(RecentResource.ALLEYS, id.toString())
	suspend fun didRecentlyUseGear(id: GearID) = didRecentlyUse(RecentResource.GEAR, id.toString())
	suspend fun didRecentlyUseOpponent(id: BowlerID) =
		didRecentlyUse(RecentResource.OPPONENTS, id.toString())
	suspend fun didRecentlyUseTeam(id: UUID) = didRecentlyUse(RecentResource.TEAM, id.toString())
}
