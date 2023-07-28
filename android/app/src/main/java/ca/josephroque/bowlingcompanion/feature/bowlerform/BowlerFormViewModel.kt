package ca.josephroque.bowlingcompanion.feature.bowlerform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.model.Bowler
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.BOWLER_ID
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.BOWLER_KIND
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BowlerFormViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
	private val bowlersRepository: BowlersRepository,
) : ViewModel() {

	private val _uiState: MutableStateFlow<BowlerFormUiState> = MutableStateFlow(BowlerFormUiState.Loading)
	val uiState: StateFlow<BowlerFormUiState> = _uiState.asStateFlow()

	private val kind = savedStateHandle.get<String>(BOWLER_KIND).let { string ->
		BowlerKind.values()
			.firstOrNull { it.name == string }
	} ?: BowlerKind.PLAYABLE

	fun handleEvent(event: BowlerFormUiEvent) {
		when (event) {
			BowlerFormUiEvent.OnAppear ->
				viewModelScope.launch { loadBowler() }
			BowlerFormUiEvent.DeleteButtonClick ->
				deleteBowler()
			BowlerFormUiEvent.SaveButtonClick ->
				saveBowler()
			is BowlerFormUiEvent.NameChanged ->
				updateName(event.name)
		}
	}

	private suspend fun loadBowler() {
		val bowlerId = savedStateHandle.get<UUID?>(BOWLER_ID)
		if (bowlerId == null) {
			_uiState.value = BowlerFormUiState.Create(
				name = "",
				kind = kind,
				fieldErrors = BowlerFormFieldErrors()
			)
		} else {
			val bowler = bowlersRepository.getBowler(bowlerId)
				.first()

			_uiState.value = BowlerFormUiState.Edit(
				name = bowler.name,
				initialValue = bowler,
				fieldErrors = BowlerFormFieldErrors()
			)
		}
	}

	private fun updateName(name: String) {
		when (val state = _uiState.value) {
			BowlerFormUiState.Loading, BowlerFormUiState.Dismissed -> Unit
			is BowlerFormUiState.Edit -> _uiState.value = state.copy(
				name = name,
				fieldErrors = state.fieldErrors.copy(nameErrorId = null)
			)
			is BowlerFormUiState.Create -> _uiState.value = state.copy(
				name = name,
				fieldErrors = state.fieldErrors.copy(nameErrorId = null)
			)
		}
	}

	private fun saveBowler() {
		viewModelScope.launch {
			when (val state = _uiState.value) {
				 BowlerFormUiState.Loading, BowlerFormUiState.Dismissed -> Unit
				is BowlerFormUiState.Create ->
					if (state.isSavable()) {
						bowlersRepository.upsertBowler(
							Bowler(
								id = UUID.randomUUID(),
								name = state.name,
								kind = state.kind
							)
						)
						_uiState.value = BowlerFormUiState.Dismissed
					} else {
						_uiState.value = state.copy(
							fieldErrors = BowlerFormFieldErrors(
								nameErrorId = if (state.name.isBlank()) R.string.bowler_form_name_missing else null
							)
						)
					}
				is BowlerFormUiState.Edit ->
					if (state.isSavable()) {
						bowlersRepository.upsertBowler(state.initialValue.copy(name = state.name))
						_uiState.value = BowlerFormUiState.Dismissed
					} else {
						_uiState.value = state.copy(
							fieldErrors = BowlerFormFieldErrors(
								nameErrorId = if (state.name.isBlank()) R.string.bowler_form_name_missing else null
							)
						)
					}
			}
		}
	}

	private fun deleteBowler() {
		viewModelScope.launch {
			when (val state = _uiState.value) {
				BowlerFormUiState.Loading, BowlerFormUiState.Dismissed, is BowlerFormUiState.Create -> Unit
				is BowlerFormUiState.Edit ->
					bowlersRepository.deleteBowler(state.initialValue.id)
			}
		}

		_uiState.value = BowlerFormUiState.Dismissed
	}
}

sealed interface BowlerFormUiState {
	object Loading: BowlerFormUiState
	object Dismissed: BowlerFormUiState

	data class Create(
		val name: String,
		val kind: BowlerKind,
		val fieldErrors: BowlerFormFieldErrors,
	): BowlerFormUiState {
		fun isSavable(): Boolean {
			return name.isNotBlank()
		}
	}

	data class Edit(
		val name: String,
		val initialValue: Bowler,
		val fieldErrors: BowlerFormFieldErrors,
	): BowlerFormUiState {
		fun isSavable(): Boolean {
			return name.isNotBlank() && name != initialValue.name
		}
	}
}

data class BowlerFormFieldErrors(
	val nameErrorId: Int? = null
)

sealed interface BowlerFormUiEvent {
	object OnAppear: BowlerFormUiEvent

	object SaveButtonClick: BowlerFormUiEvent

	object DeleteButtonClick: BowlerFormUiEvent

	class NameChanged(val name: String):
			BowlerFormUiEvent
}