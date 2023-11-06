package ca.josephroque.bowlingcompanion.feature.bowlerform

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.database.model.BowlerCreate
import ca.josephroque.bowlingcompanion.core.database.model.BowlerUpdate
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.BOWLER_ID
import ca.josephroque.bowlingcompanion.feature.bowlerform.navigation.BOWLER_KIND
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
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

	fun loadBowler() {
		viewModelScope.launch {
			val bowlerId = savedStateHandle.get<String>(BOWLER_ID)?.let {
				UUID.fromString(it)
			}

			if (bowlerId == null) {
				_uiState.value = BowlerFormUiState.Create(
					properties = BowlerCreate(
						id = UUID.randomUUID(),
						name = "",
						kind = kind,
					),
					fieldErrors = BowlerFormFieldErrors()
				)
			} else {
				val bowler = bowlersRepository.getBowlerDetails(bowlerId)
					.first()
				val update = BowlerUpdate(
					id = bowler.id,
					name = bowler.name,
				)

				_uiState.value = BowlerFormUiState.Edit(
					initialValue = update,
					properties = update,
					fieldErrors = BowlerFormFieldErrors(),
					isShowingArchiveDialog = false,
				)
			}
		}
	}

	fun updateName(name: String) {
		when (val state = _uiState.value) {
			BowlerFormUiState.Loading, BowlerFormUiState.Dismissed -> Unit
			is BowlerFormUiState.Edit -> _uiState.value = state.copy(
				properties = state.properties.copy(name = name),
				fieldErrors = state.fieldErrors.copy(nameErrorId = null)
			)
			is BowlerFormUiState.Create -> _uiState.value = state.copy(
				properties = state.properties.copy(name = name),
				fieldErrors = state.fieldErrors.copy(nameErrorId = null)
			)
		}
	}

	fun saveBowler() {
		viewModelScope.launch {
			when (val state = _uiState.value) {
				BowlerFormUiState.Loading, BowlerFormUiState.Dismissed -> Unit
				is BowlerFormUiState.Create ->
					if (state.isSavable()) {
						bowlersRepository.insertBowler(state.properties)
						_uiState.value = BowlerFormUiState.Dismissed
					} else {
						_uiState.value = state.copy(
							fieldErrors = BowlerFormFieldErrors(
								nameErrorId = if (state.properties.name.isBlank()) R.string.bowler_form_name_missing else null
							)
						)
					}

				is BowlerFormUiState.Edit ->
					if (state.isSavable()) {
						bowlersRepository.updateBowler(state.properties)
						_uiState.value = BowlerFormUiState.Dismissed
					} else {
						_uiState.value = state.copy(
							fieldErrors = BowlerFormFieldErrors(
								nameErrorId = if (state.properties.name.isBlank()) R.string.bowler_form_name_missing else null
							)
						)
					}
			}
		}
	}

	fun archiveBowler(shouldArchive: Boolean) {
		when (val state = _uiState.value) {
			BowlerFormUiState.Loading, BowlerFormUiState.Dismissed, is BowlerFormUiState.Create -> Unit
			is BowlerFormUiState.Edit -> if (state.isShowingArchiveDialog) {
				if (shouldArchive) {
					viewModelScope.launch {
						bowlersRepository.archiveBowler(
							state.initialValue.id,
							archivedOn = Clock.System.now()
						)
						_uiState.value = BowlerFormUiState.Dismissed
					}
				} else {
					_uiState.value = state.copy(
						isShowingArchiveDialog = false
					)
				}
			} else {
				_uiState.value = state.copy(
					isShowingArchiveDialog = shouldArchive
				)
			}
		}
	}
}

sealed interface BowlerFormUiState {
	data object Loading: BowlerFormUiState
	data object Dismissed: BowlerFormUiState

	data class Create(
		val properties: BowlerCreate,
		val fieldErrors: BowlerFormFieldErrors,
	): BowlerFormUiState {
		fun isSavable(): Boolean =
			properties.name.isNotBlank()
	}

	data class Edit(
		val initialValue: BowlerUpdate,
		val properties: BowlerUpdate,
		val fieldErrors: BowlerFormFieldErrors,
		val isShowingArchiveDialog: Boolean,
	): BowlerFormUiState {
		fun isSavable(): Boolean =
			properties.name.isNotBlank() && properties != initialValue
	}
}

data class BowlerFormFieldErrors(
	@StringRes val nameErrorId: Int? = null
)