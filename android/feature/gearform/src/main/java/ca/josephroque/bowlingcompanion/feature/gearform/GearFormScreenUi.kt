package ca.josephroque.bowlingcompanion.feature.gearform

import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.GearUpdate
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormUiAction
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormUiState
import java.util.UUID

sealed interface GearFormScreenUiState {
	data object Loading: GearFormScreenUiState

	data class Create(
		val form: GearFormUiState,
		val topBar: GearFormTopBarUiState,
	): GearFormScreenUiState {
		fun isSavable(): Boolean =
			form.name.isNotBlank()
	}

	data class Edit(
		val initialValue: GearUpdate,
		val form: GearFormUiState,
		val topBar: GearFormTopBarUiState,
	): GearFormScreenUiState {
		fun isSavable(): Boolean =
			form.name.isNotBlank() && form.updatedModel(existing = initialValue) != initialValue
	}
}

fun GearFormUiState.updatedModel(existing: GearUpdate): GearUpdate = existing.copy(
	id = existing.id,
	name = name,
	avatar = avatar,
	ownerId = owner?.id,
)

sealed interface GearFormScreenUiAction {
	data object LoadGear: GearFormScreenUiAction
	data class UpdatedAvatar(val avatar: Avatar): GearFormScreenUiAction
	data class UpdatedOwner(val owner: UUID?): GearFormScreenUiAction

	data class GearFormAction(
		val action: GearFormUiAction,
	): GearFormScreenUiAction
}

sealed interface GearFormScreenEvent {
	data object Dismissed: GearFormScreenEvent

	data class EditAvatar(val avatar: Avatar): GearFormScreenEvent
	data class EditOwner(val owner: UUID?): GearFormScreenEvent
}