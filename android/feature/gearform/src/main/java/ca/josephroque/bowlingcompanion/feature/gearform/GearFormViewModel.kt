package ca.josephroque.bowlingcompanion.feature.gearform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.gear.GearCreated
import ca.josephroque.bowlingcompanion.core.analytics.trackable.gear.GearDeleted
import ca.josephroque.bowlingcompanion.core.analytics.trackable.gear.GearUpdated
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GearRepository
import ca.josephroque.bowlingcompanion.core.data.repository.RecentlyUsedRepository
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GearCreate
import ca.josephroque.bowlingcompanion.core.model.GearID
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultKey
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormUiAction
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormUiState
import ca.josephroque.bowlingcompanion.feature.gearform.ui.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val GEAR_FORM_OWNER_PICKER_RESULT_KEY = ResourcePickerResultKey("GearFormOwnerPickerResultKey")

@HiltViewModel
class GearFormViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val bowlersRepository: BowlersRepository,
	private val gearRepository: GearRepository,
	private val recentlyUsedRepository: RecentlyUsedRepository,
	private val analyticsClient: AnalyticsClient,
) : ApproachViewModel<GearFormScreenEvent>() {
	private val _uiState: MutableStateFlow<GearFormScreenUiState> =
		MutableStateFlow(GearFormScreenUiState.Loading)
	val uiState = _uiState.asStateFlow()

	private val hasLoadedInitialState: Boolean
		get() = _uiState.value !is GearFormScreenUiState.Loading

	private val gearId = Route.EditGear.getGear(savedStateHandle)

	fun handleAction(action: GearFormScreenUiAction) {
		when (action) {
			GearFormScreenUiAction.LoadGear -> loadGear()
			is GearFormScreenUiAction.UpdatedOwner -> updateOwner(owner = action.owner)
			is GearFormScreenUiAction.UpdatedAvatar -> updateAvatar(avatar = action.avatar)
			is GearFormScreenUiAction.GearFormAction -> handleGearFormAction(action.action)
		}
	}

	private fun handleGearFormAction(action: GearFormUiAction) {
		when (action) {
			GearFormUiAction.BackClicked -> handleBackClicked()
			GearFormUiAction.DoneClicked -> saveGear()
			GearFormUiAction.DeleteClicked -> setDeleteGearPrompt(isVisible = true)
			GearFormUiAction.ConfirmDeleteClicked -> deleteGear()
			GearFormUiAction.DismissDeleteClicked -> setDeleteGearPrompt(isVisible = false)
			GearFormUiAction.DiscardChangesClicked -> sendEvent(GearFormScreenEvent.Dismissed)
			GearFormUiAction.CancelDiscardChangesClicked -> setDiscardChangesDialog(isVisible = false)
			GearFormUiAction.AvatarClicked -> editAvatar()
			GearFormUiAction.OwnerClicked -> editOwner()
			is GearFormUiAction.NameChanged -> updateName(name = action.name)
			is GearFormUiAction.KindChanged -> updateKind(kind = action.kind)
		}
	}

	private fun loadGear() {
		if (hasLoadedInitialState) return
		viewModelScope.launch {
			val gear = gearId?.let { gearRepository.getGearUpdate(it).first() }
			val owner = gear?.ownerId?.let { bowlersRepository.getBowlerDetails(it).first() }
			val uiState = if (gear == null) {
				GearFormScreenUiState.Create(
					form = GearFormUiState(),
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
						isAvatarLabelOverridden = true,
						isDeleteButtonEnabled = true,
						isShowingDeleteDialog = false,
						isShowingDiscardChangesDialog = false,
						isKindPickerEnabled = false,
					),
					topBar = GearFormTopBarUiState(
						existingName = gear.name,
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
			sendEvent(GearFormScreenEvent.Dismissed)
		}
	}

	private fun setDiscardChangesDialog(isVisible: Boolean) {
		_uiState.updateForm { it.copy(isShowingDiscardChangesDialog = isVisible) }
	}

	private fun editOwner() {
		sendEvent(
			GearFormScreenEvent.EditOwner(
				owner = when (val state = _uiState.value) {
					GearFormScreenUiState.Loading -> return
					is GearFormScreenUiState.Create -> state.form.owner?.id
					is GearFormScreenUiState.Edit -> state.form.owner?.id
				},
			),
		)
	}

	private fun updateOwner(owner: BowlerID?) {
		viewModelScope.launch {
			val ownerDetails = owner?.let { bowlersRepository.getBowlerDetails(it).first() }
			_uiState.updateForm { it.copy(owner = ownerDetails) }
		}
	}

	private fun editAvatar() {
		sendEvent(
			GearFormScreenEvent.EditAvatar(
				avatar = when (val state = _uiState.value) {
					GearFormScreenUiState.Loading -> return
					is GearFormScreenUiState.Create -> state.form.avatar
					is GearFormScreenUiState.Edit -> state.form.avatar
				},
			),
		)
	}

	private fun updateAvatar(avatar: Avatar) {
		_uiState.updateForm {
			it.copy(
				avatar = avatar,
				isAvatarLabelOverridden = true,
			)
		}
	}

	private fun updateName(name: String) {
		_uiState.updateForm {
			it.copy(
				name = name,
				nameErrorId = null,
				avatar = when {
					it.isAvatarLabelOverridden -> it.avatar
					name.isBlank() -> it.avatar.copy(label = Avatar.default().label)
					else -> it.avatar.copy(label = name)
				},
			)
		}
	}

	private fun updateKind(kind: GearKind) {
		_uiState.updateForm { it.copy(kind = kind) }
	}

	private fun setDeleteGearPrompt(isVisible: Boolean) {
		_uiState.updateForm { it.copy(isShowingDeleteDialog = isVisible) }
	}

	private fun deleteGear() {
		viewModelScope.launch {
			val gear = when (val uiState = _uiState.value) {
				GearFormScreenUiState.Loading -> return@launch
				is GearFormScreenUiState.Create -> return@launch
				is GearFormScreenUiState.Edit -> uiState.initialValue
			}

			gearRepository.deleteGear(gear.id)
			sendEvent(GearFormScreenEvent.Dismissed)
			analyticsClient.trackEvent(GearDeleted(gear.kind))
		}
	}

	private fun saveGear() {
		viewModelScope.launch {
			when (val state = _uiState.value) {
				GearFormScreenUiState.Loading -> Unit
				is GearFormScreenUiState.Create -> if (state.isSavable()) {
					val gear = GearCreate(
						id = gearId ?: GearID.randomID(),
						name = state.form.name,
						kind = state.form.kind,
						avatar = state.form.avatar,
						ownerId = state.form.owner?.id,
					)

					gearRepository.insertGear(gear)
					recentlyUsedRepository.didRecentlyUseGear(gear.id)
					sendEvent(GearFormScreenEvent.Dismissed)
					analyticsClient.trackEvent(GearCreated(gear.kind))
				} else {
					_uiState.updateForm {
						it.copy(
							nameErrorId = if (state.form.name.isBlank()) R.string.gear_form_name_missing else null,
						)
					}
				}
				is GearFormScreenUiState.Edit -> if (state.isSavable()) {
					val gear = state.form.updatedModel(existing = state.initialValue)
					gearRepository.updateGear(gear)
					recentlyUsedRepository.didRecentlyUseGear(gear.id)
					sendEvent(GearFormScreenEvent.Dismissed)
					analyticsClient.trackEvent(GearUpdated(gear.kind))
				} else {
					_uiState.updateForm {
						it.copy(
							nameErrorId = if (state.form.name.isBlank()) R.string.gear_form_name_missing else null,
						)
					}
				}
			}
		}
	}
}
