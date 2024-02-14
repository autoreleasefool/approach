package ca.josephroque.bowlingcompanion.feature.settings.developer

import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.feature.settings.ui.developer.DeveloperSettingsUiAction
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeveloperSettingsViewModel @Inject constructor() :
	ApproachViewModel<DeveloperSettingsScreenEvent>() {
	fun handleAction(action: DeveloperSettingsScreenUiAction) {
		when (action) {
			is DeveloperSettingsScreenUiAction.DeveloperSettingsAction -> {
				when (action.action) {
					DeveloperSettingsUiAction.BackClicked -> sendEvent(DeveloperSettingsScreenEvent.Dismissed)
				}
			}
		}
	}
}
