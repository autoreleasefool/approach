package ca.josephroque.bowlingcompanion.core.model

import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsOptInStatus

data class UserData(
	val isOnboardingComplete: Boolean = false,
	val isLegacyMigrationComplete: Boolean = false,
	val analyticsOptIn: AnalyticsOptInStatus = AnalyticsOptInStatus.OPTED_IN,
)