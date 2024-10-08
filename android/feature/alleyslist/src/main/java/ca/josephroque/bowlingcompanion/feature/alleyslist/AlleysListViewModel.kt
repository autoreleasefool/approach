package ca.josephroque.bowlingcompanion.feature.alleyslist

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.AlleysRepository
import ca.josephroque.bowlingcompanion.core.model.AlleyListItem
import ca.josephroque.bowlingcompanion.feature.alleyslist.ui.AlleysListUiAction
import ca.josephroque.bowlingcompanion.feature.alleyslist.ui.AlleysListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AlleysListViewModel @Inject constructor(private val alleysRepository: AlleysRepository) :
	ApproachViewModel<AlleysListScreenEvent>() {
	private val alleyToDelete: MutableStateFlow<AlleyListItem?> = MutableStateFlow(null)

	val uiState: StateFlow<AlleysListScreenUiState> =
		combine(
			alleyToDelete,
			alleysRepository.getAlleysList(),
		) { alleyToDelete, alleysList ->
			AlleysListScreenUiState.Loaded(
				alleysList = AlleysListUiState(
					list = alleysList,
					alleyToDelete = alleyToDelete,
				),
			)
		}.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = AlleysListScreenUiState.Loading,
		)

	fun handleAction(action: AlleysListScreenUiAction) {
		when (action) {
			is AlleysListScreenUiAction.AlleysList -> handleAlleysListAction(action.action)
		}
	}

	private fun handleAlleysListAction(action: AlleysListUiAction) {
		when (action) {
			AlleysListUiAction.BackClicked -> sendEvent(AlleysListScreenEvent.Dismissed)
			AlleysListUiAction.AddAlleyClicked -> sendEvent(AlleysListScreenEvent.NavigateToAddAlley)
			is AlleysListUiAction.AlleyClicked -> sendEvent(
				AlleysListScreenEvent.NavigateToEditAlley(action.id),
			)
			is AlleysListUiAction.AlleyEdited -> sendEvent(
				AlleysListScreenEvent.NavigateToEditAlley(action.id),
			)
			is AlleysListUiAction.AlleyDeleted -> setDeleteAlleyPrompt(action.alley)
			AlleysListUiAction.ConfirmDeleteClicked -> deleteAlley()
			AlleysListUiAction.DismissDeleteClicked -> setDeleteAlleyPrompt(null)
		}
	}

	private fun setDeleteAlleyPrompt(alley: AlleyListItem?) {
		alleyToDelete.value = alley
	}

	private fun deleteAlley() {
		val alley = alleyToDelete.value ?: return
		viewModelScope.launch {
			alleysRepository.deleteAlley(alley.alleyId)
			setDeleteAlleyPrompt(null)
		}
	}
}
