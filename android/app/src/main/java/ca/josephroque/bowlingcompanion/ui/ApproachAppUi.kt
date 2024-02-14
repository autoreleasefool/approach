package ca.josephroque.bowlingcompanion.ui

import ca.josephroque.bowlingcompanion.navigation.TopLevelDestination

data class ApproachAppUiState(
	val isOnboardingComplete: Boolean,
	val destinations: List<TopLevelDestination>,
	val badgeCount: Map<TopLevelDestination, Int>,
)

@Suppress("unused")
sealed interface ApproachAppUiAction
