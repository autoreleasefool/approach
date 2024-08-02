package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.datastore.ApproachPreferencesDataSource
import ca.josephroque.bowlingcompanion.core.model.AnalyticsOptInStatus
import ca.josephroque.bowlingcompanion.core.model.SeriesItemSize
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.UserData
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class OfflineFirstUserDataRepository @Inject constructor(
	private val approachPreferencesDataSource: ApproachPreferencesDataSource,
) : UserDataRepository {

	override val userData: Flow<UserData> =
		approachPreferencesDataSource.userData

	override suspend fun didCompleteOnboarding() {
		approachPreferencesDataSource.setOnboardingComplete(true)
	}

	override suspend fun didCompleteOpponentMigration() {
		approachPreferencesDataSource.setOpponentMigrationComplete(true)
	}

	override suspend fun didCompleteLegacyMigration() {
		approachPreferencesDataSource.setLegacyMigrationComplete(true)
	}

	override suspend fun didOpenAccessoriesTab() {
		approachPreferencesDataSource.setHasOpenedAccessoriesTab(true)
	}

	override suspend fun setAnalyticsOptInStatus(status: AnalyticsOptInStatus) {
		approachPreferencesDataSource.setAnalyticsOptInStatus(status)
	}

	override suspend fun setSeriesItemSize(size: SeriesItemSize) {
		approachPreferencesDataSource.setSeriesItemSize(size)
	}

	override suspend fun setIsCountingH2AsH(isCountingH2AsH: Boolean) {
		approachPreferencesDataSource.setIsCountingH2AsH(enabled = isCountingH2AsH)
	}

	override suspend fun setIsCountingSplitWithBonusAsSplit(isCountingSplitWithBonusAsSplit: Boolean) {
		approachPreferencesDataSource.setIsCountingSplitWithBonusAsSplit(
			enabled = isCountingSplitWithBonusAsSplit,
		)
	}

	override suspend fun setIsHidingZeroStatistics(isHidingZeroStatistics: Boolean) {
		approachPreferencesDataSource.setIsHidingZeroStatistics(isHiding = isHidingZeroStatistics)
	}

	override suspend fun setIsHidingStatisticDescriptions(isHidingStatisticDescriptions: Boolean) {
		approachPreferencesDataSource.setIsHidingStatisticDescriptions(
			isHiding = isHidingStatisticDescriptions,
		)
	}

	override suspend fun setIsHidingWidgetsInLeaguesList(isHidingWidgetsInLeaguesList: Boolean) {
		approachPreferencesDataSource.setIsHidingWidgetsInLeaguesList(
			isHiding = isHidingWidgetsInLeaguesList,
		)
	}

	override suspend fun setIsHidingWidgetsInBowlersList(isHidingWidgetsInBowlersList: Boolean) {
		approachPreferencesDataSource.setIsHidingWidgetsInBowlersList(
			isHiding = isHidingWidgetsInBowlersList,
		)
	}

	override suspend fun setLastTrackableFilterSource(source: TrackableFilter.Source?) {
		approachPreferencesDataSource.setLastTrackableFilterSource(source = source)
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

	override suspend fun didDismissLaneFormSwipeToEditTip() {
		approachPreferencesDataSource.setIsLaneFormSwipeToEditTipDismissed(isDismissed = true)
	}

	override suspend fun didDismissQuickPlayTip() {
		approachPreferencesDataSource.setIsQuickPlayTipDismissed(isDismissed = true)
	}

	override suspend fun didDismissStatisticsTapToViewChartTip() {
		approachPreferencesDataSource.setIsStatisticsTapToViewChartTipDismissed(isDismissed = true)
	}

	override suspend fun didDismissSwipeRowsTip() {
		approachPreferencesDataSource.setIsSwipeRowsTipDismissed(isDismissed = true)
	}

	override suspend fun setStatisticIDSeen(statistic: StatisticID) {
		approachPreferencesDataSource.setStatisticsIdsSeen(statistic.name)
	}

	override suspend fun setAllStatisticIDsSeen() {
		StatisticID.entries.forEach {
			setStatisticIDSeen(it)
		}
	}

	override suspend fun setLatestGameInEditor(id: UUID) {
		approachPreferencesDataSource.setLatestGameInEditor(id.toString())
	}

	override suspend fun setLatestSeriesInEditor(ids: List<UUID>) {
		approachPreferencesDataSource.setLatestSeriesInEditor(ids.map { it.toString() })
	}

	override suspend fun dismissLatestGameInEditor() {
		approachPreferencesDataSource.setLatestGameInEditor(null)
		approachPreferencesDataSource.setLatestSeriesInEditor(emptyList())
	}
}
