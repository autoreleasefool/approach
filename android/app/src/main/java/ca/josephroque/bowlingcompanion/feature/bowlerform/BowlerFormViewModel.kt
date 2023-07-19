package ca.josephroque.bowlingcompanion.feature.bowlerform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.model.Bowler
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.asBowlerKind
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.BOWLER_ID
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.BOWLER_KIND
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BowlerFormViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
	private val bowlersRepository: BowlersRepository,
) : ViewModel() {

	val uiState: MutableStateFlow<BowlerFormUiState> = MutableStateFlow(BowlerFormUiState.Loading)

	private val kind = savedStateHandle.get<String>(BOWLER_KIND)
		.asBowlerKind()

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
			uiState.value = BowlerFormUiState.Create(
				name = "",
				kind = kind,
				fieldErrors = BowlerFormFieldErrors()
			)
		} else {
			val bowler = bowlersRepository.getBowler(bowlerId)
				.first()

			uiState.value = BowlerFormUiState.Edit(
				name = bowler.name,
				initialValue = bowler,
				fieldErrors = BowlerFormFieldErrors()
			)
		}
	}

	private fun updateName(name: String) {
		when (val state = uiState.value) {
			BowlerFormUiState.Loading, BowlerFormUiState.Dismissed -> Unit
			is BowlerFormUiState.Edit -> uiState.value = state.copy(
				name = name,
				fieldErrors = state.fieldErrors.copy(nameErrorId = null)
			)
			is BowlerFormUiState.Create -> uiState.value = state.copy(
				name = name,
				fieldErrors = state.fieldErrors.copy(nameErrorId = null)
			)
		}
	}

	private fun saveBowler() {
		viewModelScope.launch {
			when (val state = uiState.value) {
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
						uiState.value = BowlerFormUiState.Dismissed
					} else {
						uiState.value = state.copy(
							fieldErrors = BowlerFormFieldErrors(
								nameErrorId = if (state.name.isBlank()) R.string.bowler_form_name_missing else null
							)
						)
					}
				is BowlerFormUiState.Edit ->
					if (state.isSavable()) {
						bowlersRepository.upsertBowler(state.initialValue.copy(name = state.name))
						uiState.value = BowlerFormUiState.Dismissed
					} else {
						uiState.value = state.copy(
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
			when (val state = uiState.value) {
				BowlerFormUiState.Loading, BowlerFormUiState.Dismissed, is BowlerFormUiState.Create -> Unit
				is BowlerFormUiState.Edit ->
					bowlersRepository.deleteBowler(state.initialValue.id)
			}
		}

		uiState.value = BowlerFormUiState.Dismissed
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