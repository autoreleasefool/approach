package ca.josephroque.bowlingcompanion.core.model

import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsOptInStatus

data class UserData(
	// Onboarding
	val isOnboardingComplete: Boolean,
	val isLegacyMigrationComplete: Boolean,

	// Analytics
	val analyticsOptIn: AnalyticsOptInStatus,

	// Statistics
	val isCountingH2AsHDisabled: Boolean,
	val isCountingSplitWithBonusAsSplitDisabled: Boolean,
	val isShowingZeroStatistics: Boolean,
	val isHidingWidgetsInBowlersList: Boolean,
	val isHidingWidgetsInLeaguesList: Boolean,

	// Recently Used
	val recentlyUsedBowlerIds: List<String>,
	val recentlyUsedLeagueIds: List<String>,
	val recentlyUsedAlleyIds: List<String>,
	val recentlyUsedGearIds: List<String>,
)