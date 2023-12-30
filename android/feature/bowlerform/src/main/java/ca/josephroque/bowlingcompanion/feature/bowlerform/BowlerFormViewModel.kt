package ca.josephroque.bowlingcompanion.feature.bowlerform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.RecentlyUsedRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerCreate
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.BowlerUpdate
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.BOWLER_ID
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.BOWLER_KIND
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.R
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerFormUiAction
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerFormUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BowlerFormViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val bowlersRepository: BowlersRepository,
	private val recentlyUsedRepository: RecentlyUsedRepository,
): ApproachViewModel<BowlerFormScreenEvent>() {

	private val _uiState: MutableStateFlow<BowlerFormScreenUiState> =
		MutableStateFlow(BowlerFormScreenUiState.Loading)
	val uiState = _uiState.asStateFlow()

	private val hasLoadedInitialState: Boolean
		get() = _uiState.value !is BowlerFormScreenUiState.Loading

	private val bowlerId = savedStateHandle.get<String>(BOWLER_ID)?.let {
		UUID.fromString(it)
	}

	private val kind = savedStateHandle.get<String>(BOWLER_KIND).let { string ->
		BowlerKind.entries
			.firstOrNull { it.name == string }
	} ?: BowlerKind.PLAYABLE

	fun handleAction(action: BowlerFormScreenUiAction) {
		when (action) {
			BowlerFormScreenUiAction.LoadBowler -> loadBowler()
			is BowlerFormScreenUiAction.BowlerFormAction -> handleBowlerFormAction(action.action)
		}
	}

	private fun handleBowlerFormAction(action: BowlerFormUiAction) {
		when (action) {
			BowlerFormUiAction.BackClicked -> handleBackClicked()
			BowlerFormUiAction.DoneClicked -> saveBowler()
			BowlerFormUiAction.DiscardChangesClicked -> sendEvent(BowlerFormScreenEvent.Dismissed)
			BowlerFormUiAction.CancelDiscardChangesClicked -> setDiscardChangesDialog(isVisible = false)
			BowlerFormUiAction.ArchiveClicked -> setArchiveBowlerPrompt(isVisible = true)
			BowlerFormUiAction.ConfirmArchiveClicked -> archiveBowler()
			BowlerFormUiAction.DismissArchiveClicked -> setArchiveBowlerPrompt(isVisible = false)
			is BowlerFormUiAction.NameChanged -> updateName(action.name)
		}
	}

	private fun loadBowler() {
		if (hasLoadedInitialState) return
		viewModelScope.launch {
			if (bowlerId == null) {
				_uiState.value = BowlerFormScreenUiState.Create(
					topBar = BowlerFormTopBarUiState(
						kind = kind,
						existingName = null,
					),
					form = BowlerFormUiState(),
				)
			} else {
				val bowler = bowlersRepository.getBowlerDetails(bowlerId)
					.first()
				val update = BowlerUpdate(
					id = bowler.id,
					name = bowler.name,
				)

				_uiState.value = BowlerFormScreenUiState.Edit(
					initialValue = update,
					topBar = BowlerFormTopBarUiState(
						kind = bowler.kind,
						existingName = bowler.name,
					),
					form = BowlerFormUiState(
						name = bowler.name,
						nameErrorId = null,
						isShowingArchiveDialog = false,
						isArchiveButtonEnabled = true,
						isShowingDiscardChangesDialog = false,
					),
				)
			}
		}
	}

	private fun handleBackClicked() {
		if (_uiState.value.hasAnyChanges()) {
			setDiscardChangesDialog(isVisible = true)
		} else {
			sendEvent(BowlerFormScreenEvent.Dismissed)
		}
	}

	private fun setDiscardChangesDialog(isVisible: Boolean) {
		_uiState.updateForm {
			it.copy(isShowingDiscardChangesDialog = isVisible)
		}
	}

	private fun updateName(name: String) {
		_uiState.updateForm {
			it.copy(name = name, nameErrorId = null)
		}
	}

	private fun setArchiveBowlerPrompt(isVisible: Boolean) {
		_uiState.updateForm {
			it.copy(isShowingArchiveDialog = isVisible)
		}
	}

	private fun archiveBowler() {
		when (val state = _uiState.value) {
			BowlerFormScreenUiState.Loading, is BowlerFormScreenUiState.Create -> Unit
			is BowlerFormScreenUiState.Edit -> viewModelScope.launch {
				bowlersRepository.archiveBowler(state.initialValue.id)
				sendEvent(BowlerFormScreenEvent.Dismissed)
			}
		}
	}

	private fun saveBowler() {
		viewModelScope.launch {
			when (val state = _uiState.value) {
				BowlerFormScreenUiState.Loading -> Unit
				is BowlerFormScreenUiState.Create ->
					if (state.isSavable()) {
						val bowler = BowlerCreate(
							id = bowlerId ?: UUID.randomUUID(),
							name = state.form.name,
							kind = kind,
						)

						bowlersRepository.insertBowler(bowler)
						recentlyUsedRepository.didRecentlyUseBowler(bowler.id)
						sendEvent(BowlerFormScreenEvent.Dismissed)
					} else {
						_uiState.updateForm { form ->
							form.copy(
								nameErrorId = if (form.name.isBlank()) R.string.bowler_form_name_missing else null
							)
						}
					}
				is BowlerFormScreenUiState.Edit ->
					if (state.isSavable()) {
						val bowler = state.form.updatedModel(id = state.initialValue.id)
						bowlersRepository.updateBowler(bowler)
						recentlyUsedRepository.didRecentlyUseBowler(bowler.id)
						sendEvent(BowlerFormScreenEvent.Dismissed)
					} else {
						_uiState.updateForm { form ->
							form.copy(
								nameErrorId = if (form.name.isBlank()) R.string.bowler_form_name_missing else null
							)
						}
					}
			}
		}
	}
}
