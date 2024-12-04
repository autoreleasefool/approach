package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.AnalyticsOptInStatus
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.SeriesItemSize
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.UserData
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface UserDataRepository {
	val userData: Flow<UserData>

	suspend fun didCompleteOnboarding()
	suspend fun didCompleteOpponentMigration()
	suspend fun didCompleteLegacyMigration()
	suspend fun didOpenAccessoriesTab()

	suspend fun setUserAnalyticsID(id: UUID)
	suspend fun setAnalyticsOptInStatus(status: AnalyticsOptInStatus)

	suspend fun setSeriesItemSize(size: SeriesItemSize)

	suspend fun setStatisticIDSeen(statistic: StatisticID)
	suspend fun setAllStatisticIDsSeen()

	suspend fun setIsCountingH2AsH(isCountingH2AsH: Boolean)
	suspend fun setIsCountingSplitWithBonusAsSplit(isCountingSplitWithBonusAsSplit: Boolean)
	suspend fun setIsHidingZeroStatistics(isHidingZeroStatistics: Boolean)
	suspend fun setIsHidingStatisticDescriptions(isHidingStatisticDescriptions: Boolean)
	suspend fun setIsHidingWidgetsInBowlersList(isHidingWidgetsInBowlersList: Boolean)
	suspend fun setIsHidingWidgetsInLeaguesList(isHidingWidgetsInLeaguesList: Boolean)
	suspend fun setLastTrackableFilterSource(source: TrackableFilter.Source?)
	suspend fun setIsHidingTeamScoresInGameDetails(isHidingTeamScoresInGameDetails: Boolean)

	suspend fun didRecentlyUseBowler(id: String)
	suspend fun didRecentlyUseLeague(id: String)
	suspend fun didRecentlyUseAlley(id: String)
	suspend fun didRecentlyUseGear(id: String)
	suspend fun didRecentlyUseTeam(id: String)

	suspend fun didDismissLaneFormSwipeToEditTip()
	suspend fun didDismissQuickPlayTip()
	suspend fun didDismissStatisticsTapToViewChartTip()
	suspend fun didDismissSwipeRowsTip()
	suspend fun didDismissFrameDragHint()

	suspend fun setLatestTeamSeriesInEditor(id: TeamSeriesID?)
	suspend fun setLatestSeriesInEditor(ids: List<SeriesID>)
	suspend fun setLatestGameInEditor(id: GameID)
	suspend fun dismissLatestGameInEditor()
}

fun UserData.hasSeenStatistic(id: StatisticID): Boolean = seenStatisticIds.contains(id.name)
