package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsOptInStatus
import ca.josephroque.bowlingcompanion.core.datastore.ApproachPreferencesDataSource
import ca.josephroque.bowlingcompanion.core.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineFirstUserDataRepository @Inject constructor(
	private val approachPreferencesDataSource: ApproachPreferencesDataSource,
): UserDataRepository {

	override val userData: Flow<UserData> =
		approachPreferencesDataSource.userData


	override suspend fun didCompleteOnboarding() {
		approachPreferencesDataSource.setOnboardingComplete(true)
	}

	override suspend fun didCompleteLegacyMigration() {
		approachPreferencesDataSource.setLegacyMigrationComplete(true)
	}

	override suspend fun setAnalyticsOptInStatus(status: AnalyticsOptInStatus) {
		approachPreferencesDataSource.setAnalyticsOptInStatus(status)
	}

	override suspend fun setIsCountingH2AsH(isCountingH2AsH: Boolean) {
		approachPreferencesDataSource.setIsCountingH2AsH(enabled = isCountingH2AsH)
	}

	override suspend fun setIsCountingSplitWithBonusAsSplit(isCountingSplitWithBonusAsSplit: Boolean) {
		approachPreferencesDataSource.setIsCountingSplitWithBonusAsSplit(enabled = isCountingSplitWithBonusAsSplit)
	}

	override suspend fun setIsHidingZeroStatistics(isHidingZeroStatistics: Boolean) {
		approachPreferencesDataSource.setIsHidingZeroStatistics(isHiding = isHidingZeroStatistics)
	}

	override suspend fun setIsHidingWidgetsInLeaguesList(isHidingWidgetsInLeaguesList: Boolean) {
		approachPreferencesDataSource.setIsHidingWidgetsInLeaguesList(isHiding = isHidingWidgetsInLeaguesList)
	}

	override suspend fun setIsHidingWidgetsInBowlersList(isHidingWidgetsInBowlersList: Boolean) {
		approachPreferencesDataSource.setIsHidingWidgetsInBowlersList(isHiding = isHidingWidgetsInBowlersList)
	}

	override suspend fun didRecentlyUseAlley(id: String) {
		approachPreferencesDataSource.insertRecentlyUsedAlley(id)
	}

	override suspend fun didRecentlyUseBowler(id: String) {
		approachPreferencesDataSource.insertRecentlyUsedBowler(id)
	}

	override suspend fun didRecentlyUseGear(id: String) {
		approachPreferencesDataSource.insertRecentlyUsedGear(id)
	}

	override suspend fun didRecentlyUseLeague(id: String) {
		approachPreferencesDataSource.insertRecentlyUsedLeague(id)
	}
}