package ca.josephroque.bowlingcompanion.feature.settings.developer

import androidx.lifecycle.ViewModel
import ca.josephroque.bowlingcompanion.feature.settings.ui.developer.DeveloperSettingsUiAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DeveloperSettingsViewModel @Inject constructor(): ViewModel() {
	private val _events: MutableStateFlow<DeveloperSettingsScreenEvent?> = MutableStateFlow(null)
	val events = _events.asStateFlow()

	fun handleAction(action: DeveloperSettingsScreenUiAction) {
		when (action) {
			is DeveloperSettingsScreenUiAction.DeveloperSettingsAction -> {
				when (action.action) {
					DeveloperSettingsUiAction.BackClicked -> {
						_events.value = DeveloperSettingsScreenEvent.Dismissed
					}
				}
			}
		}
	}
}