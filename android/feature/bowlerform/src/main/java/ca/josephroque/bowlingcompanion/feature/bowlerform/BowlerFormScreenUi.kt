package ca.josephroque.bowlingcompanion.feature.bowlerform

import ca.josephroque.bowlingcompanion.core.model.BowlerUpdate
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerFormUiAction
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerFormUiState
import java.util.UUID

sealed interface BowlerFormScreenUiState {
	data object Loading: BowlerFormScreenUiState

	data class Create(
		val form: BowlerFormUiState,
		val topBar: BowlerFormTopBarUiState,
	): BowlerFormScreenUiState {
		fun isSavable(): Boolean =
			form.name.isNotBlank()
	}

	data class Edit(
		val initialValue: BowlerUpdate,
		val form: BowlerFormUiState,
		val topBar: BowlerFormTopBarUiState,
	): BowlerFormScreenUiState {
		fun isSavable(): Boolean =
			form.name.isNotBlank() && form.update(id = initialValue.id) != initialValue
	}
}

fun BowlerFormUiState.update(id: UUID): BowlerUpdate = BowlerUpdate(
	id = id,
	name = name,
)

sealed interface BowlerFormScreenUiAction {
	data object LoadBowler: BowlerFormScreenUiAction
	data class BowlerFormAction(
		val action: BowlerFormUiAction,
	): BowlerFormScreenUiAction
}

sealed interface BowlerFormScreenEvent {
	data object Dismissed: BowlerFormScreenEvent
}