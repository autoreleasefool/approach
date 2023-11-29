package ca.josephroque.bowlingcompanion.feature.alleyform

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.data.repository.AlleysRepository
import ca.josephroque.bowlingcompanion.core.database.model.AlleyCreate
import ca.josephroque.bowlingcompanion.core.database.model.AlleyUpdate
import ca.josephroque.bowlingcompanion.core.model.AlleyMaterial
import ca.josephroque.bowlingcompanion.core.model.AlleyMechanism
import ca.josephroque.bowlingcompanion.core.model.AlleyPinBase
import ca.josephroque.bowlingcompanion.core.model.AlleyPinFall
import ca.josephroque.bowlingcompanion.feature.alleyform.navigation.ALLEY_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AlleyFormViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
	private val alleysRepository: AlleysRepository,
): ViewModel() {
	private val _uiState: MutableStateFlow<AlleyFormUiState> = MutableStateFlow(AlleyFormUiState.Loading)
	val uiState: StateFlow<AlleyFormUiState> = _uiState.asStateFlow()

	fun loadAlley() {
		viewModelScope.launch {
			val alleyId = savedStateHandle.get<String>(ALLEY_ID)?.let {
				UUID.fromString(it)
			}
			if (alleyId == null) {
				_uiState.value = AlleyFormUiState.Create(
					properties = AlleyCreate(
						id = UUID.randomUUID(),
						name = "",
						mechanism = null,
						pinFall = null,
						pinBase = null,
						material = null,
					),
					numberOfLanes = 0,
					fieldErrors = AlleyFormFieldErrors(),
				)
			} else {
				val alley = alleysRepository.getAlleyDetails(alleyId).first()
				val update = AlleyUpdate(
					id = alley.id,
					name = alley.name,
					material = alley.material,
					pinFall = alley.pinFall,
					mechanism = alley.mechanism,
					pinBase = alley.pinBase,
				)

				_uiState.value = AlleyFormUiState.Edit(
					initialValue = update,
					properties = update,
					numberOfLanes = alley.numberOfLanes,
					fieldErrors = AlleyFormFieldErrors(),
				)
			}
		}
	}

	fun saveAlley() {
		viewModelScope.launch {
			saveAlleyAndTransition(nextState = AlleyFormUiState.Dismissed)
		}
	}

	fun manageLanes() {
		// TODO: manageLanes must also update the state to be Edit since we are saving the alley at this time
		viewModelScope.launch {
			when (val state = _uiState.value) {
				AlleyFormUiState.Loading, AlleyFormUiState.Dismissed, is AlleyFormUiState.ManagingLanes -> Unit
				is AlleyFormUiState.Create ->
					saveAlleyAndTransition(nextState = AlleyFormUiState.ManagingLanes(state.properties.id))
				is AlleyFormUiState.Edit ->
					saveAlleyAndTransition(nextState = AlleyFormUiState.ManagingLanes(state.properties.id))
			}
		}
	}

	private suspend fun saveAlleyAndTransition(nextState: AlleyFormUiState) {
		when (val state = _uiState.value) {
			AlleyFormUiState.Loading, AlleyFormUiState.Dismissed, is AlleyFormUiState.ManagingLanes -> Unit
			is AlleyFormUiState.Create ->
				if (state.isSavable()) {
					alleysRepository.insertAlley(state.properties)
					_uiState.value = nextState
				} else {
					_uiState.value = state.copy(fieldErrors = state.fieldErrors())
				}
			is AlleyFormUiState.Edit ->
				if (state.isSavable()) {
					alleysRepository.updateAlley(state.properties)
					_uiState.value = nextState
				} else {
					_uiState.value = state.copy(fieldErrors = state.fieldErrors())
				}
		}
	}

	fun deleteAlley() {
		viewModelScope.launch {
			when (val state = _uiState.value) {
				AlleyFormUiState.Loading, AlleyFormUiState.Dismissed, is AlleyFormUiState.ManagingLanes, is AlleyFormUiState.Create -> Unit
				is AlleyFormUiState.Edit ->
					alleysRepository.deleteAlley(state.properties.id)
			}

			_uiState.value = AlleyFormUiState.Dismissed
		}
	}

	fun updateName(name: String) {
		when (val state = _uiState.value) {
			AlleyFormUiState.Loading, AlleyFormUiState.Dismissed, is AlleyFormUiState.ManagingLanes -> Unit
			is AlleyFormUiState.Edit -> _uiState.value = state.copy(
				properties = state.properties.copy(name = name),
				fieldErrors = state.fieldErrors.copy(nameErrorId = null),
			)
			is AlleyFormUiState.Create -> _uiState.value = state.copy(
				properties = state.properties.copy(name = name),
				fieldErrors = state.fieldErrors.copy(nameErrorId = null),
			)
		}
	}

	fun updateMaterial(material: AlleyMaterial?) {
		when (val state = _uiState.value) {
			AlleyFormUiState.Loading, AlleyFormUiState.Dismissed, is AlleyFormUiState.ManagingLanes -> Unit
			is AlleyFormUiState.Edit -> _uiState.value = state.copy(
				properties = state.properties.copy(material = material),
			)
			is AlleyFormUiState.Create -> _uiState.value = state.copy(
				properties = state.properties.copy(material = material),
			)
		}
	}

	fun updateMechanism(mechanism: AlleyMechanism?) {
		when (val state = _uiState.value) {
			AlleyFormUiState.Loading, AlleyFormUiState.Dismissed, is AlleyFormUiState.ManagingLanes -> Unit
			is AlleyFormUiState.Edit -> _uiState.value = state.copy(
				properties = state.properties.copy(mechanism = mechanism),
			)
			is AlleyFormUiState.Create -> _uiState.value = state.copy(
				properties = state.properties.copy(mechanism = mechanism),
			)
		}
	}

	fun updatePinFall(pinFall: AlleyPinFall?) {
		when (val state = _uiState.value) {
			AlleyFormUiState.Loading, AlleyFormUiState.Dismissed, is AlleyFormUiState.ManagingLanes -> Unit
			is AlleyFormUiState.Edit -> _uiState.value = state.copy(
				properties = state.properties.copy(pinFall = pinFall),
			)
			is AlleyFormUiState.Create -> _uiState.value = state.copy(
				properties = state.properties.copy(pinFall = pinFall),
			)
		}
	}

	fun updatePinBase(pinBase: AlleyPinBase?) {
		when (val state = _uiState.value) {
			AlleyFormUiState.Loading, AlleyFormUiState.Dismissed, is AlleyFormUiState.ManagingLanes -> Unit
			is AlleyFormUiState.Edit -> _uiState.value = state.copy(
				properties = state.properties.copy(pinBase = pinBase),
			)
			is AlleyFormUiState.Create -> _uiState.value = state.copy(
				properties = state.properties.copy(pinBase = pinBase),
			)
		}
	}
}

sealed interface AlleyFormUiState {
	data object Loading: AlleyFormUiState
	data object Dismissed: AlleyFormUiState
	data class ManagingLanes(
		val alleyId: UUID,
	): AlleyFormUiState

	data class Create(
		val properties: AlleyCreate,
		val numberOfLanes: Int,
		val fieldErrors: AlleyFormFieldErrors,
	): AlleyFormUiState {
		fun isSavable(): Boolean =
			properties.name.isNotBlank()

		fun fieldErrors(): AlleyFormFieldErrors =
			AlleyFormFieldErrors(
				nameErrorId = if (properties.name.isBlank()) R.string.alley_form_property_name_missing else null
			)
	}

	data class Edit(
		val initialValue: AlleyUpdate,
		val properties: AlleyUpdate,
		val numberOfLanes: Int,
		val fieldErrors: AlleyFormFieldErrors,
	): AlleyFormUiState {
		fun isSavable(): Boolean =
			properties.name.isNotBlank() && properties != initialValue

		fun fieldErrors(): AlleyFormFieldErrors =
			AlleyFormFieldErrors(
				nameErrorId = if (properties.name.isBlank()) R.string.alley_form_property_name_missing else null
			)
	}
}

data class AlleyFormFieldErrors(
	@StringRes val nameErrorId: Int? = null
)