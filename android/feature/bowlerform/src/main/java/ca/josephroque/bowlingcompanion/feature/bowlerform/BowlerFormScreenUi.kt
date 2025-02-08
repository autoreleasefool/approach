package ca.josephroque.bowlingcompanion.feature.bowlerform

import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerUpdate
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerFormUiAction
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerFormUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

sealed interface BowlerFormScreenUiState {
	fun hasAnyChanges(): Boolean
	fun isSavable(): Boolean

	data object Loading : BowlerFormScreenUiState {
		override fun hasAnyChanges(): Boolean = false
		override fun isSavable(): Boolean = false
	}

	data class Create(val form: BowlerFormUiState, val topBar: BowlerFormTopBarUiState) :
		BowlerFormScreenUiState {
		override fun isSavable(): Boolean = form.name.isNotBlank()

		override fun hasAnyChanges(): Boolean = form != BowlerFormUiState()
	}

	data class Edit(
		val initialValue: BowlerUpdate,
		val form: BowlerFormUiState,
		val topBar: BowlerFormTopBarUiState,
	) : BowlerFormScreenUiState {
		override fun isSavable(): Boolean = form.name.isNotBlank() && form.updatedModel(id = initialValue.id) != initialValue

		override fun hasAnyChanges(): Boolean = form.updatedModel(id = initialValue.id) != initialValue
	}
}

fun BowlerFormUiState.updatedModel(id: BowlerID): BowlerUpdate = BowlerUpdate(
	id = id,
	name = name,
)

sealed interface BowlerFormScreenUiAction {
	data object LoadBowler : BowlerFormScreenUiAction
	data class BowlerFormAction(val action: BowlerFormUiAction) : BowlerFormScreenUiAction
}

sealed interface BowlerFormScreenEvent {
	data object Dismissed : BowlerFormScreenEvent
}

fun MutableStateFlow<BowlerFormScreenUiState>.updateForm(function: (BowlerFormUiState) -> BowlerFormUiState) {
	this.update { state ->
		when (state) {
			BowlerFormScreenUiState.Loading -> state
			is BowlerFormScreenUiState.Create -> {
				val updatedState = state.copy(form = function(state.form))
				updatedState.copy(
					topBar = updatedState.topBar.copy(isSaveButtonEnabled = updatedState.hasAnyChanges()),
				)
			}
			is BowlerFormScreenUiState.Edit -> {
				val updatedState = state.copy(form = function(state.form))
				updatedState.copy(
					topBar = updatedState.topBar.copy(isSaveButtonEnabled = updatedState.hasAnyChanges()),
				)
			}
		}
	}
}
