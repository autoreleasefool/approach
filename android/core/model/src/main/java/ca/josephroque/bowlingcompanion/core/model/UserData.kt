package ca.josephroque.bowlingcompanion.core.model

data class UserData(
	// Onboarding
	val isOnboardingComplete: Boolean,
	val isOpponentMigrationComplete: Boolean,
	val isLegacyMigrationComplete: Boolean,
	val hasOpenedAccessoriesTab: Boolean,

	// Analytics
	val analyticsOptIn: AnalyticsOptInStatus,

	// Statistics
	val isCountingH2AsHDisabled: Boolean,
	val isCountingSplitWithBonusAsSplitDisabled: Boolean,
	val isShowingZeroStatistics: Boolean,
	val isHidingStatisticDescriptions: Boolean,
	val isHidingWidgetsInBowlersList: Boolean,
	val isHidingWidgetsInLeaguesList: Boolean,
	val lastTrackableFilter: TrackableFilter.Source?,
	val seenStatisticIds: Set<String>,

	// Recently Used
	val recentlyUsedBowlerIds: List<String>,
	val recentlyUsedLeagueIds: List<String>,
	val recentlyUsedAlleyIds: List<String>,
	val recentlyUsedGearIds: List<String>,

	// Series
	val seriesItemSize: SeriesItemSize,

	// Tips
	val isLaneFormSwipeToEditTipDismissed: Boolean,
	val isQuickPlayTipDismissed: Boolean,
	val isStatisticsTapToViewChartTipDismissed: Boolean,
	val isSwipeRowsTipDismissed: Boolean,
	val isFrameDragHintDismissed: Boolean,

	// Game in progress
	val latestSeriesInEditor: List<String>,
	val latestGameInEditor: String?,
) {
	data class Onboarding(
		val isOnboardingComplete: Boolean,
		val isOpponentMigrationComplete: Boolean,
		val isLegacyMigrationComplete: Boolean,
	)

	val onboarding: Onboarding
		get() = Onboarding(
			isOnboardingComplete = isOnboardingComplete,
			isOpponentMigrationComplete = isOpponentMigrationComplete,
			isLegacyMigrationComplete = isLegacyMigrationComplete,
		)
}
