package ca.josephroque.bowlingcompanion.feature.alleyform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.alley.AlleyCreated
import ca.josephroque.bowlingcompanion.core.analytics.trackable.alley.AlleyDeleted
import ca.josephroque.bowlingcompanion.core.analytics.trackable.alley.AlleyUpdated
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.AlleysRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LanesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.RecentlyUsedRepository
import ca.josephroque.bowlingcompanion.core.model.AlleyCreate
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.AlleyMaterial
import ca.josephroque.bowlingcompanion.core.model.AlleyMechanism
import ca.josephroque.bowlingcompanion.core.model.AlleyPinBase
import ca.josephroque.bowlingcompanion.core.model.AlleyPinFall
import ca.josephroque.bowlingcompanion.core.model.LaneID
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.alleyform.ui.AlleyFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.alleyform.ui.AlleyFormUiAction
import ca.josephroque.bowlingcompanion.feature.alleyform.ui.AlleyFormUiState
import ca.josephroque.bowlingcompanion.feature.alleyform.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class AlleyFormViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val alleysRepository: AlleysRepository,
	private val lanesRepository: LanesRepository,
	private val recentlyUsedRepository: RecentlyUsedRepository,
	private val analyticsClient: AnalyticsClient,
) : ApproachViewModel<AlleyFormScreenEvent>() {
	private val isEditing = Route.EditAlley.getAlley(savedStateHandle) != null
	private val alleyId = Route.EditAlley.getAlley(savedStateHandle) ?: AlleyID.randomID().also {
		savedStateHandle[Route.EditAlley.ARG_ALLEY] = it.toString()
	}

	private val _uiState: MutableStateFlow<AlleyFormScreenUiState> =
		MutableStateFlow(AlleyFormScreenUiState.Loading)
	val uiState = _uiState.asStateFlow()

	private val hasLoadedInitialState: Boolean
		get() = _uiState.value !is AlleyFormScreenUiState.Loading

	fun handleAction(action: AlleyFormScreenUiAction) {
		when (action) {
			AlleyFormScreenUiAction.LoadAlley -> loadAlley()
			is AlleyFormScreenUiAction.AlleyForm -> handleAlleyFormAction(action.action)
			is AlleyFormScreenUiAction.LanesUpdated -> updateLanes(action.lanes)
		}
	}

	private fun handleAlleyFormAction(action: AlleyFormUiAction) {
		when (action) {
			AlleyFormUiAction.BackClicked -> handleBackClicked()
			AlleyFormUiAction.DoneClicked -> saveAlley()
			AlleyFormUiAction.DeleteClicked -> setDeleteAlleyPrompt(isVisible = true)
			AlleyFormUiAction.ConfirmDeleteClicked -> deleteAlley()
			AlleyFormUiAction.DismissDeleteClicked -> setDeleteAlleyPrompt(isVisible = false)
			AlleyFormUiAction.ManageLanesClicked -> manageLanes()
			AlleyFormUiAction.DiscardChangesClicked -> sendEvent(AlleyFormScreenEvent.Dismissed)
			AlleyFormUiAction.CancelDiscardChangesClicked -> setDiscardChangesDialog(isVisible = false)
			is AlleyFormUiAction.NameChanged -> updateName(action.name)
			is AlleyFormUiAction.MaterialChanged -> updateMaterial(action.material)
			is AlleyFormUiAction.MechanismChanged -> updateMechanism(action.mechanism)
			is AlleyFormUiAction.PinBaseChanged -> updatePinBase(action.pinBase)
			is AlleyFormUiAction.PinFallChanged -> updatePinFall(action.pinFall)
		}
	}

	private fun loadAlley() {
		if (hasLoadedInitialState) return
		viewModelScope.launch {
			val alley = if (isEditing) alleysRepository.getAlleyUpdate(alleyId).first() else null
			val uiState = if (alley == null) {
				AlleyFormScreenUiState.Create(
					form = AlleyFormUiState(),
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
						isShowingDiscardChangesDialog = false,
					),
					topBar = AlleyFormTopBarUiState(
						existingName = alley.name,
					),
				)
			}

			_uiState.value = uiState
		}
	}

	private fun handleBackClicked() {
		if (_uiState.value.hasAnyChanges()) {
			setDiscardChangesDialog(isVisible = true)
		} else {
			sendEvent(AlleyFormScreenEvent.Dismissed)
		}
	}

	private fun setDiscardChangesDialog(isVisible: Boolean) {
		_uiState.updateForm { it.copy(isShowingDiscardChangesDialog = isVisible) }
	}

	private fun deleteAlley() {
		viewModelScope.launch {
			val alley = when (val uiState = _uiState.value) {
				AlleyFormScreenUiState.Loading -> return@launch
				is AlleyFormScreenUiState.Create -> return@launch
				is AlleyFormScreenUiState.Edit -> uiState.initialValue
			}

			alleysRepository.deleteAlley(alley.id)
			sendEvent(AlleyFormScreenEvent.Dismissed)
			analyticsClient.trackEvent(AlleyDeleted)
		}
	}

	private fun saveAlley() {
		viewModelScope.launch {
			when (val state = _uiState.value) {
				AlleyFormScreenUiState.Loading -> Unit
				is AlleyFormScreenUiState.Create -> if (state.isSavable()) {
					val alley = AlleyCreate(
						id = alleyId,
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

					// FIXME: add location
					analyticsClient.trackEvent(
						AlleyCreated(
							withLocation = false,
							numberOfLanes = alley.lanes.size,
						),
					)
				} else {
					_uiState.updateForm {
						it.copy(
							nameErrorId = if (it.name.isBlank()) R.string.alley_form_property_name_missing else null,
						)
					}
				}
				is AlleyFormScreenUiState.Edit -> if (state.isSavable()) {
					val alley = state.form.updatedModel(existing = state.initialValue)
					alleysRepository.updateAlley(alley)
					lanesRepository.setAlleyLanes(alleyId, alley.lanes)
					recentlyUsedRepository.didRecentlyUseAlley(alley.id)
					sendEvent(AlleyFormScreenEvent.Dismissed)

					// FIXME: add location
					analyticsClient.trackEvent(
						AlleyUpdated(
							withLocation = false,
							numberOfLanes = alley.lanes.size,
						),
					)
				} else {
					_uiState.updateForm {
						it.copy(
							nameErrorId = if (it.name.isBlank()) R.string.alley_form_property_name_missing else null,
						)
					}
				}
			}
		}
	}

	private fun updateLanes(lanes: List<LaneID>) {
		viewModelScope.launch {
			val alleyLanes = lanesRepository.getLanes(lanes).first()
			_uiState.updateForm { it.copy(lanes = alleyLanes) }
		}
	}

	private fun manageLanes() {
		sendEvent(
			AlleyFormScreenEvent.ManageLanes(
				existingLanes = when (val state = _uiState.value) {
					AlleyFormScreenUiState.Loading -> return
					is AlleyFormScreenUiState.Create -> state.form.lanes.map(LaneListItem::id)
					is AlleyFormScreenUiState.Edit -> state.form.lanes.map(LaneListItem::id)
				},
			),
		)
	}

	private fun updateName(name: String) {
		_uiState.updateForm { it.copy(name = name, nameErrorId = null) }
	}

	private fun updateMaterial(material: AlleyMaterial?) {
		_uiState.updateForm { it.copy(material = material) }
	}

	private fun updateMechanism(mechanism: AlleyMechanism?) {
		_uiState.updateForm { it.copy(mechanism = mechanism) }
	}

	private fun updatePinBase(pinBase: AlleyPinBase?) {
		_uiState.updateForm { it.copy(pinBase = pinBase) }
	}

	private fun updatePinFall(pinFall: AlleyPinFall?) {
		_uiState.updateForm { it.copy(pinFall = pinFall) }
	}

	private fun setDeleteAlleyPrompt(isVisible: Boolean) {
		_uiState.updateForm { it.copy(isShowingDeleteDialog = isVisible) }
	}
}
