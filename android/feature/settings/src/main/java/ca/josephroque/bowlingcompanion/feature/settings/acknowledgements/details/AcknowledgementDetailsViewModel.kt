package ca.josephroque.bowlingcompanion.feature.settings.acknowledgements.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.AcknowledgementsRepository
import ca.josephroque.bowlingcompanion.feature.settings.navigation.ACKNOWLEDGEMENT
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.details.AcknowledgementDetailsTopBarUiState
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.details.AcknowledgementDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.details.AcknowledgementDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AcknowledgementDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	acknowledgementsRepository: AcknowledgementsRepository,
): ViewModel() {
	private val _acknowledgementName = savedStateHandle.get<String>(ACKNOWLEDGEMENT) ?: ""

	private val _acknowledgementDetails = acknowledgementsRepository.getAcknowledgement(_acknowledgementName)
		.map { AcknowledgementDetailsUiState(it) }

	val uiState = _acknowledgementDetails
		.map {
			AcknowledgementDetailsScreenUiState.Loaded(
				acknowledgementDetails = it,
				topBar = AcknowledgementDetailsTopBarUiState(it.acknowledgement.name),
			)
		}
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = AcknowledgementDetailsScreenUiState.Loading,
		)

	private val _events: MutableStateFlow<AcknowledgementDetailsScreenEvent?> = MutableStateFlow(null)
	val events = _events.asStateFlow()

	fun handleAction(action: AcknowledgementDetailsScreenUiAction) {
		when (action) {
			is AcknowledgementDetailsScreenUiAction.AcknowledgementDetailsAction -> handleAcknowledgementDetailsAction(action.action)
		}
	}

	private fun handleAcknowledgementDetailsAction(action: AcknowledgementDetailsUiAction) {
		when (action) {
			AcknowledgementDetailsUiAction.BackClicked -> _events.value = AcknowledgementDetailsScreenEvent.Dismissed
		}
	}
}