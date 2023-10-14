package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsOptInStatus
import ca.josephroque.bowlingcompanion.core.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
	val userData: Flow<UserData>

	suspend fun didCompleteOnboarding()
	suspend fun didCompleteLegacyMigration()

	suspend fun setAnalyticsOptInStatus(status: AnalyticsOptInStatus)

	suspend fun setIsCountingH2AsH(isCountingH2AsH: Boolean)
	suspend fun setIsCountingSplitWithBonusAsSplit(isCountingSplitWithBonusAsSplit: Boolean)
	suspend fun setIsHidingZeroStatistics(isHidingZeroStatistics: Boolean)
	suspend fun setIsHidingWidgetsInBowlersList(isHidingWidgetsInBowlersList: Boolean)
	suspend fun setIsHidingWidgetsInLeaguesList(isHidingWidgetsInLeaguesList: Boolean)

	suspend fun didRecentlyUseBowler(id: String)
	suspend fun didRecentlyUseLeague(id: String)
	suspend fun didRecentlyUseAlley(id: String)
	suspend fun didRecentlyUseGear(id: String)
}