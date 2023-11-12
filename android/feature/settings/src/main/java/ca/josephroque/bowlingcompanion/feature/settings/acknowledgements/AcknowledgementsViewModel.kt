package ca.josephroque.bowlingcompanion.feature.settings.acknowledgements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.AcknowledgementsRepository
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.AcknowledgementsUiAction
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.AcknowledgementsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AcknowledgementsViewModel @Inject constructor(
	acknowledgementsRepository: AcknowledgementsRepository,
): ViewModel() {
	private val _acknowledgements = acknowledgementsRepository.getAcknowledgements()
		.map { AcknowledgementsUiState(it) }

	val uiState: StateFlow<AcknowledgementsSettingsScreenUiState> = _acknowledgements
		.map { AcknowledgementsSettingsScreenUiState.Loaded(acknowledgements = it) }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = AcknowledgementsSettingsScreenUiState.Loading,
		)

	private val _events: MutableStateFlow<AcknowledgementsSettingsScreenEvent?> = MutableStateFlow(null)
	val events = _events.asStateFlow()

	fun handleAction(action: AcknowledgementsSettingsScreenUiAction) {
		when (action) {
			AcknowledgementsSettingsScreenUiAction.HandledNavigation -> _events.value = null
			is AcknowledgementsSettingsScreenUiAction.AcknowledgementsAction -> handleAcknowledgementsAction(action.action)
		}
	}

	private fun handleAcknowledgementsAction(action: AcknowledgementsUiAction) {
		when (action) {
			AcknowledgementsUiAction.BackClicked -> _events.value = AcknowledgementsSettingsScreenEvent.Dismissed
			is AcknowledgementsUiAction.AcknowledgementClicked -> _events.value = AcknowledgementsSettingsScreenEvent.NavigatedToAcknowledgement(action.name)
		}
	}
}