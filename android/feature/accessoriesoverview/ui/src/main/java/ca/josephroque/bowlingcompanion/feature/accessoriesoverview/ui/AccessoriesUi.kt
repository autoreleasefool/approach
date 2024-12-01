package ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui

import ca.josephroque.bowlingcompanion.core.model.AlleyListItem
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.onboarding.AccessoriesOnboardingUiAction
import ca.josephroque.bowlingcompanion.feature.alleyslist.ui.AlleysListUiState
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListUiState

data class AccessoriesUiState(
	val isAccessoryMenuExpanded: Boolean = false,
	val isShowingOnboarding: Boolean = false,
	val alleysList: AlleysListUiState? = null,
	val gearList: GearListUiState? = null,
	val alleysItemLimit: Int,
	val gearItemLimit: Int,
)

sealed interface AccessoriesUiAction {
	data object ViewAllAlleysClicked : AccessoriesUiAction
	data object ViewAllGearClicked : AccessoriesUiAction

	data object AddAccessoryClicked : AccessoriesUiAction
	data object AccessoryMenuDismissed : AccessoriesUiAction
	data object AddAlleyClicked : AccessoriesUiAction
	data object AddGearClicked : AccessoriesUiAction

	data class AlleyClicked(val alley: AlleyListItem) : AccessoriesUiAction
	data class GearClicked(val gear: GearListItem) : AccessoriesUiAction
	data class Onboarding(val action: AccessoriesOnboardingUiAction) : AccessoriesUiAction
}
