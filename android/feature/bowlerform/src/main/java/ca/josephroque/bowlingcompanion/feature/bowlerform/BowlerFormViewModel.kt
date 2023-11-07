package ca.josephroque.bowlingcompanion.feature.bowlerform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
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
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BowlerFormViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val bowlersRepository: BowlersRepository,
) : ViewModel() {

	private val _uiState: MutableStateFlow<BowlerFormScreenUiState> =
		MutableStateFlow(BowlerFormScreenUiState.Loading)
	val uiState = _uiState.asStateFlow()

	private val _events: MutableStateFlow<BowlerFormScreenEvent?> = MutableStateFlow(null)
	val events = _events.asStateFlow()

	private val bowlerId = savedStateHandle.get<String>(BOWLER_ID)?.let {
		UUID.fromString(it)
	}

	private val kind = savedStateHandle.get<String>(BOWLER_KIND).let { string ->
		BowlerKind.values()
			.firstOrNull { it.name == string }
	}

	fun handleAction(action: BowlerFormScreenUiAction) {
		when (action) {
			BowlerFormScreenUiAction.LoadBowler -> loadBowler()
			is BowlerFormScreenUiAction.BowlerFormAction -> handleBowlerFormAction(action.action)
		}
	}

	private fun handleBowlerFormAction(action: BowlerFormUiAction) {
		when (action) {
			BowlerFormUiAction.BackClicked -> _events.value = BowlerFormScreenEvent.Dismissed
			BowlerFormUiAction.DoneClicked -> saveBowler()
			BowlerFormUiAction.ArchiveClicked -> setArchiveBowlerPrompt(isVisible = true)
			BowlerFormUiAction.ConfirmArchiveClicked -> archiveBowler()
			BowlerFormUiAction.DismissArchiveClicked -> setArchiveBowlerPrompt(isVisible = false)
			is BowlerFormUiAction.NameChanged -> updateName(action.name)
		}
	}

	private fun getFormUiState(): BowlerFormUiState? {
		return when (val state = _uiState.value) {
			BowlerFormScreenUiState.Loading -> null
			is BowlerFormScreenUiState.Create -> state.form
			is BowlerFormScreenUiState.Edit -> state.form
		}
	}

	private fun setFormUiState(state: BowlerFormUiState) {
		when (val uiState = _uiState.value) {
			BowlerFormScreenUiState.Loading -> Unit
			is BowlerFormScreenUiState.Create -> _uiState.value = uiState.copy(form = state)
			is BowlerFormScreenUiState.Edit -> _uiState.value = uiState.copy(form = state)
		}
	}

	private fun loadBowler() {
		viewModelScope.launch {
			if (bowlerId == null) {
				_uiState.value = BowlerFormScreenUiState.Create(
					topBar = BowlerFormTopBarUiState(
						kind = kind ?: BowlerKind.PLAYABLE,
						existingName = null,
					),
					form = BowlerFormUiState(
						name = "",
						nameErrorId = null,
						isShowingArchiveDialog = false,
						isArchiveButtonEnabled = false,
					)
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
					),
				)
			}
		}
	}

	private fun updateName(name: String) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(
			name = name,
			nameErrorId = null,
		))
	}

	private fun setArchiveBowlerPrompt(isVisible: Boolean) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(
			isShowingArchiveDialog = isVisible,
		))
	}

	private fun archiveBowler() {
		val formState = getFormUiState() ?: return
		when (val state = _uiState.value) {
			BowlerFormScreenUiState.Loading, is BowlerFormScreenUiState.Create -> Unit
			is BowlerFormScreenUiState.Edit -> viewModelScope.launch {
				bowlersRepository.archiveBowler(
					id = state.initialValue.id,
					archivedOn = Clock.System.now(),
				)
				setFormUiState(state = formState.copy(isShowingArchiveDialog = false))
				_events.value = BowlerFormScreenEvent.Dismissed
			}
		}
	}

	private fun saveBowler() {
		viewModelScope.launch {
			when (val state = _uiState.value) {
				BowlerFormScreenUiState.Loading -> Unit
				is BowlerFormScreenUiState.Create -> if (state.isSavable()) {
					val bowler = BowlerCreate(
						id = bowlerId ?: UUID.randomUUID(),
						name = state.form.name,
						kind = kind ?: BowlerKind.PLAYABLE,
					)

					bowlersRepository.insertBowler(bowler)
					_events.value = BowlerFormScreenEvent.Dismissed
				} else {
					_uiState.value = state.copy(
						form = state.form.copy(
							nameErrorId = if (state.form.name.isBlank()) R.string.bowler_form_name_missing else null
						)
					)
				}
				is BowlerFormScreenUiState.Edit -> if (state.isSavable()) {
					val bowler = state.form.update(id = state.initialValue.id)
					bowlersRepository.updateBowler(bowler)
					_events.value = BowlerFormScreenEvent.Dismissed
				} else {
					_uiState.value = state.copy(
						form = state.form.copy(
							nameErrorId = if (state.form.name.isBlank()) R.string.bowler_form_name_missing else null
						)
					)
				}
			}
		}
	}
}
