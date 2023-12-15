package ca.josephroque.bowlingcompanion.feature.alleyform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.AlleysRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LanesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.RecentlyUsedRepository
import ca.josephroque.bowlingcompanion.core.model.AlleyCreate
import ca.josephroque.bowlingcompanion.core.model.AlleyMaterial
import ca.josephroque.bowlingcompanion.core.model.AlleyMechanism
import ca.josephroque.bowlingcompanion.core.model.AlleyPinBase
import ca.josephroque.bowlingcompanion.core.model.AlleyPinFall
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import ca.josephroque.bowlingcompanion.feature.alleyform.navigation.ALLEY_ID
import ca.josephroque.bowlingcompanion.feature.alleyform.ui.R
import ca.josephroque.bowlingcompanion.feature.alleyform.ui.AlleyFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.alleyform.ui.AlleyFormUiAction
import ca.josephroque.bowlingcompanion.feature.alleyform.ui.AlleyFormUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AlleyFormViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val alleysRepository: AlleysRepository,
	private val lanesRepository: LanesRepository,
	private val recentlyUsedRepository: RecentlyUsedRepository,
): ApproachViewModel<AlleyFormScreenEvent>() {
	private val alleyId = savedStateHandle.get<String>(ALLEY_ID)?.let {
		UUID.fromString(it)
	}

	private val _uiState: MutableStateFlow<AlleyFormScreenUiState> =
		MutableStateFlow(AlleyFormScreenUiState.Loading)
	val uiState = _uiState.asStateFlow()

	fun handleAction(action: AlleyFormScreenUiAction) {
		when (action) {
			AlleyFormScreenUiAction.LoadAlley -> loadAlley()
			is AlleyFormScreenUiAction.AlleyForm -> handleAlleyFormAction(action.action)
			is AlleyFormScreenUiAction.LanesUpdated -> updateLanes(alleyId, lanes = action.lanes)
		}
	}

	private fun handleAlleyFormAction(action: AlleyFormUiAction) {
		when (action) {
			AlleyFormUiAction.BackClicked -> sendEvent(AlleyFormScreenEvent.Dismissed)
			AlleyFormUiAction.DoneClicked -> saveAlley()
			AlleyFormUiAction.DeleteClicked -> setDeleteAlleyPrompt(isVisible = true)
			AlleyFormUiAction.ConfirmDeleteClicked -> deleteAlley()
			AlleyFormUiAction.DismissDeleteClicked -> setDeleteAlleyPrompt(isVisible = false)
			AlleyFormUiAction.ManageLanesClicked -> manageLanes()
			is AlleyFormUiAction.NameChanged -> updateName(action.name)
			is AlleyFormUiAction.MaterialChanged -> updateMaterial(action.material)
			is AlleyFormUiAction.MechanismChanged -> updateMechanism(action.mechanism)
			is AlleyFormUiAction.PinBaseChanged -> updatePinBase(action.pinBase)
			is AlleyFormUiAction.PinFallChanged -> updatePinFall(action.pinFall)
		}
	}

	private fun getFormUiState(): AlleyFormUiState? =
		when (val state = _uiState.value) {
			AlleyFormScreenUiState.Loading -> null
			is AlleyFormScreenUiState.Create -> state.form
			is AlleyFormScreenUiState.Edit -> state.form
		}

	private fun setFormUiState(state: AlleyFormUiState) {
		when (val uiState = _uiState.value) {
			AlleyFormScreenUiState.Loading -> Unit
			is AlleyFormScreenUiState.Create -> _uiState.value = uiState.copy(form = state)
			is AlleyFormScreenUiState.Edit -> _uiState.value = uiState.copy(form = state)
		}
	}

	private fun loadAlley() {
		if (getFormUiState() != null) return
		viewModelScope.launch {
			val alley = alleyId?.let { alleysRepository.getAlleyUpdate(it).first() }
			val uiState = if (alley == null) {
				AlleyFormScreenUiState.Create(
					form = AlleyFormUiState(
						name = "",
						nameErrorId = null,
						mechanism = null,
						pinFall = null,
						pinBase = null,
						material = null,
						isDeleteButtonEnabled = false,
						isShowingDeleteDialog = false,
						lanes = emptyList(),
					),
					topBar = AlleyFormTopBarUiState(
						existingName = null,
					),
				)
			} else {
				AlleyFormScreenUiState.Edit(
					initialValue = alley,
					form = AlleyFormUiState(
						name = alley.name,
						nameErrorId = null,
						mechanism = alley.mechanism,
						pinFall = alley.pinFall,
						pinBase = alley.pinBase,
						material = alley.material,
						lanes = alley.lanes,
						isDeleteButtonEnabled = true,
						isShowingDeleteDialog = false,
					),
					topBar = AlleyFormTopBarUiState(
						existingName = alley.name,
					),
				)
			}

			_uiState.value = uiState
		}
	}

	private fun deleteAlley() {
		val formState = getFormUiState() ?: return
		viewModelScope.launch {
			val alley = when (val uiState = _uiState.value) {
				AlleyFormScreenUiState.Loading -> return@launch
				is AlleyFormScreenUiState.Create -> return@launch
				is AlleyFormScreenUiState.Edit -> uiState.initialValue
			}

			alleysRepository.deleteAlley(alley.id)
			setFormUiState(state = formState.copy(isShowingDeleteDialog = false))
			sendEvent(AlleyFormScreenEvent.Dismissed)
		}
	}

	private fun saveAlley() {
		viewModelScope.launch {
			when (val state = _uiState.value) {
				AlleyFormScreenUiState.Loading -> Unit
				is AlleyFormScreenUiState.Create -> if (state.isSavable()) {
					val alley = AlleyCreate(
						id = alleyId ?: UUID.randomUUID(),
						name = state.form.name,
						material = state.form.material,
						pinFall = state.form.pinFall,
						mechanism = state.form.mechanism,
						pinBase = state.form.pinBase,
						lanes = state.form.lanes,
					)

					alleysRepository.insertAlley(alley)
					lanesRepository.setAlleyLanes(alley.id, alley.lanes)
					recentlyUsedRepository.didRecentlyUseAlley(alley.id)
					sendEvent(AlleyFormScreenEvent.Dismissed)
				} else {
					_uiState.value = state.copy(
						form = state.form.copy(
							nameErrorId = if (state.form.name.isBlank()) R.string.alley_form_property_name_missing else null,
						),
					)
				}
				is AlleyFormScreenUiState.Edit -> if (state.isSavable()) {
					val alley = state.form.updatedModel(existing = state.initialValue)
					alleysRepository.updateAlley(alley)
					recentlyUsedRepository.didRecentlyUseAlley(alley.id)
					sendEvent(AlleyFormScreenEvent.Dismissed)
				} else {
					_uiState.value = state.copy(
						form = state.form.copy(
							nameErrorId = if (state.form.name.isBlank()) R.string.alley_form_property_name_missing else null,
						),
					)
				}
			}
		}
	}

	private fun updateLanes(alleyId: UUID?, lanes: List<UUID>) {
		val state = getFormUiState() ?: return
		alleyId ?: return
		viewModelScope.launch {
			val alleyLanes = lanesRepository.getLanes(lanes).first()
			lanesRepository.setAlleyLanes(alleyId, alleyLanes)
			setFormUiState(state.copy(lanes = alleyLanes))
		}
	}

	private fun manageLanes() {
		val state = getFormUiState() ?: return
		sendEvent(AlleyFormScreenEvent.ManageLanes(existingLanes = state.lanes.map(LaneListItem::id)))
	}

	private fun updateName(name: String) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(
			name = name,
			nameErrorId = null,
		))
	}

	private fun updateMaterial(material: AlleyMaterial?) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(material = material))
	}

	private fun updateMechanism(mechanism: AlleyMechanism?) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(mechanism = mechanism))
	}

	private fun updatePinBase(pinBase: AlleyPinBase?) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(pinBase = pinBase))
	}

	private fun updatePinFall(pinFall: AlleyPinFall?) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(pinFall = pinFall))
	}

	private fun setDeleteAlleyPrompt(isVisible: Boolean) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(isShowingDeleteDialog = isVisible))
	}
}