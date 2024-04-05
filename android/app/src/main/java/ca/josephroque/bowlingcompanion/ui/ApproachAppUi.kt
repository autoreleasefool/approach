package ca.josephroque.bowlingcompanion.ui

import ca.josephroque.bowlingcompanion.core.model.UserData
import ca.josephroque.bowlingcompanion.navigation.TopLevelDestination

data class ApproachAppUiState(
	val onboarding: UserData.Onboarding,
	val destinations: List<TopLevelDestination>,
	val badgeCount: Map<TopLevelDestination, Int>,
)

@Suppress("unused")
sealed interface ApproachAppUiAction
