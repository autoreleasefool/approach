package ca.josephroque.bowlingcompanion.feature.settings.acknowledgements

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.AcknowledgementsRepository
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.AcknowledgementsUiAction
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.AcknowledgementsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AcknowledgementsViewModel @Inject constructor(
	acknowledgementsRepository: AcknowledgementsRepository,
) : ApproachViewModel<AcknowledgementsSettingsScreenEvent>() {
	private val acknowledgements = acknowledgementsRepository.getAcknowledgements()
		.map { AcknowledgementsUiState(it) }

	val uiState: StateFlow<AcknowledgementsSettingsScreenUiState> = acknowledgements
		.map { AcknowledgementsSettingsScreenUiState.Loaded(acknowledgements = it) }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = AcknowledgementsSettingsScreenUiState.Loading,
		)

	fun handleAction(action: AcknowledgementsSettingsScreenUiAction) {
		when (action) {
			is AcknowledgementsSettingsScreenUiAction.AcknowledgementsAction -> handleAcknowledgementsAction(
				action.action,
			)
		}
	}

	private fun handleAcknowledgementsAction(action: AcknowledgementsUiAction) {
		when (action) {
			AcknowledgementsUiAction.BackClicked -> sendEvent(AcknowledgementsSettingsScreenEvent.Dismissed)
			is AcknowledgementsUiAction.AcknowledgementClicked -> sendEvent(
				AcknowledgementsSettingsScreenEvent.NavigatedToAcknowledgement(action.name),
			)
		}
	}
}
