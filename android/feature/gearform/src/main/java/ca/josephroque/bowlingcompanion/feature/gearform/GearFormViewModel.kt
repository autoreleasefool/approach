package ca.josephroque.bowlingcompanion.feature.gearform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GearRepository
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.GearCreate
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.feature.gearform.navigation.GEAR_ID
import ca.josephroque.bowlingcompanion.feature.gearform.ui.R
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormUiAction
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GearFormViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val bowlersRepository: BowlersRepository,
	private val gearRepository: GearRepository,
): ViewModel() {
	private val _uiState: MutableStateFlow<GearFormScreenUiState> =
		MutableStateFlow(GearFormScreenUiState.Loading)
	val uiState = _uiState.asStateFlow()

	private val _events: MutableStateFlow<GearFormScreenEvent?> = MutableStateFlow(null)
	val events = _events.asStateFlow()

	private val gearId = savedStateHandle.get<String>(GEAR_ID)?.let {
		UUID.fromString(it)
	}

	fun handleAction(action: GearFormScreenUiAction) {
		when (action) {
			GearFormScreenUiAction.LoadGear -> loadGear()
			is GearFormScreenUiAction.UpdatedOwner -> updateOwner(owner = action.owner)
			is GearFormScreenUiAction.UpdatedAvatar -> updateAvatar(avatar = action.avatar)
			is GearFormScreenUiAction.GearFormAction -> handleGearFormAction(action.action)
			GearFormScreenUiAction.FinishedNavigation -> _events.value = null
		}
	}

	private fun handleGearFormAction(action: GearFormUiAction) {
		when (action) {
			GearFormUiAction.BackClicked -> _events.value = GearFormScreenEvent.Dismissed
			GearFormUiAction.DoneClicked -> saveGear()
			GearFormUiAction.DeleteClicked -> setArchiveGearPrompt(isVisible = true)
			GearFormUiAction.ConfirmDeleteClicked -> archiveGear()
			GearFormUiAction.DismissDeleteClicked -> setArchiveGearPrompt(isVisible = false)
			GearFormUiAction.AvatarClicked -> _events.value = GearFormScreenEvent.EditAvatar(
				avatar = getFormUiState()?.avatar ?: Avatar.default(),
			)
			GearFormUiAction.OwnerClicked -> _events.value = GearFormScreenEvent.EditOwner(
				owner = getFormUiState()?.owner?.id,
			)
			is GearFormUiAction.NameChanged -> updateName(name = action.name)
		}
	}

	private fun getFormUiState(): GearFormUiState? =
		when (val state = _uiState.value) {
			GearFormScreenUiState.Loading -> null
			is GearFormScreenUiState.Create -> state.form
			is GearFormScreenUiState.Edit -> state.form
		}

	private fun setFormUiState(state: GearFormUiState) {
		when (val uiState = _uiState.value) {
			GearFormScreenUiState.Loading -> Unit
			is GearFormScreenUiState.Create -> _uiState.value = uiState.copy(form = state)
			is GearFormScreenUiState.Edit -> _uiState.value = uiState.copy(form = state)
		}
	}

	private fun loadGear() {
		if (getFormUiState() != null) return
		viewModelScope.launch {
			val gear = gearId?.let { gearRepository.getGearUpdate(it).first() }
			val owner = gear?.ownerId?.let { bowlersRepository.getBowlerDetails(it).first() }
			val uiState = if (gear == null) {
				GearFormScreenUiState.Create(
					form = GearFormUiState(
						name = "",
						kind = GearKind.BOWLING_BALL,
						nameErrorId = null,
						owner = null,
						avatar = Avatar.default(),
						isDeleteButtonEnabled = false,
						isShowingDeleteDialog = false,
					),
					topBar = GearFormTopBarUiState(
						existingName = null,
					),
				)
			} else {
				GearFormScreenUiState.Edit(
					initialValue = gear,
					form = GearFormUiState(
						name = gear.name,
						kind = gear.kind,
						nameErrorId = null,
						owner = owner,
						avatar = gear.avatar,
						isDeleteButtonEnabled = true,
						isShowingDeleteDialog = false,
					),
					topBar = GearFormTopBarUiState(
						existingName = gear.name,
					),
				)
			}
			_uiState.value = uiState
		}
	}

	private fun updateOwner(owner: UUID?) {
		// TODO: prevent other form updates while owner is loading
		viewModelScope.launch {
			val state = getFormUiState() ?: return@launch
			val ownerDetails = owner?.let { bowlersRepository.getBowlerDetails(it).first() }
			setFormUiState(state.copy(owner = ownerDetails))
		}
	}

	private fun updateAvatar(avatar: Avatar) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(avatar = avatar))
	}

	private fun updateName(name: String) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(name = name))
	}

	private fun setArchiveGearPrompt(isVisible: Boolean) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(isShowingDeleteDialog = isVisible))
	}

	private fun archiveGear() {
		val formState = getFormUiState() ?: return
		viewModelScope.launch {
			val gear = when (val uiState = _uiState.value) {
				GearFormScreenUiState.Loading -> return@launch
				is GearFormScreenUiState.Create -> return@launch
				is GearFormScreenUiState.Edit -> uiState.initialValue
			}

			gearRepository.deleteGear(gear.id)
			setFormUiState(state = formState.copy(isShowingDeleteDialog = false))
			_events.value = GearFormScreenEvent.Dismissed
		}
	}

	private fun saveGear() {
		viewModelScope.launch {
			when (val state = _uiState.value) {
				GearFormScreenUiState.Loading -> Unit
				is GearFormScreenUiState.Create -> if (state.isSavable()) {
					val gear = GearCreate(
						id = gearId ?: UUID.randomUUID(),
						name = state.form.name,
						kind = state.form.kind,
						avatar = state.form.avatar,
						ownerId = state.form.owner?.id,
					)

					gearRepository.insertGear(gear)
					_events.value = GearFormScreenEvent.Dismissed
				} else {
					_uiState.value = state.copy(
						form = state.form.copy(
							nameErrorId = if (state.form.name.isBlank()) R.string.gear_form_name_missing else null,
						),
					)
				}
				is GearFormScreenUiState.Edit -> if (state.isSavable()) {
					val gear = state.form.updatedModel(existing = state.initialValue)
					gearRepository.updateGear(gear)
					_events.value = GearFormScreenEvent.Dismissed
				} else {
					_uiState.value = state.copy(
						form = state.form.copy(
							nameErrorId = if (state.form.name.isBlank()) R.string.gear_form_name_missing else null,
						),
					)
				}
			}
		}
	}
}