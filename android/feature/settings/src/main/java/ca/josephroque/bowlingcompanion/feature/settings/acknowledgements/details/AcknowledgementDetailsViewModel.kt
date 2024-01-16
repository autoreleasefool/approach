package ca.josephroque.bowlingcompanion.feature.settings.acknowledgements.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.AcknowledgementsRepository
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.details.AcknowledgementDetailsTopBarUiState
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.details.AcknowledgementDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.details.AcknowledgementDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AcknowledgementDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	acknowledgementsRepository: AcknowledgementsRepository,
): ApproachViewModel<AcknowledgementDetailsScreenEvent>() {
	private val _acknowledgementName = Route.AcknowledgementDetails.getAcknowledgement(savedStateHandle)
		?: throw IllegalArgumentException("Acknowledgement name must be provided to AcknowledgementDetailsViewModel")

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

	fun handleAction(action: AcknowledgementDetailsScreenUiAction) {
		when (action) {
			is AcknowledgementDetailsScreenUiAction.AcknowledgementDetailsAction -> handleAcknowledgementDetailsAction(action.action)
		}
	}

	private fun handleAcknowledgementDetailsAction(action: AcknowledgementDetailsUiAction) {
		when (action) {
			AcknowledgementDetailsUiAction.BackClicked -> sendEvent(AcknowledgementDetailsScreenEvent.Dismissed)
		}
	}
}