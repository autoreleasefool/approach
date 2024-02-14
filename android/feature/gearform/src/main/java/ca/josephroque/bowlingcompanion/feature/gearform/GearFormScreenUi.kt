package ca.josephroque.bowlingcompanion.feature.gearform

import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.GearUpdate
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormUiAction
import ca.josephroque.bowlingcompanion.feature.gearform.ui.GearFormUiState
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

sealed interface GearFormScreenUiState {
	fun hasAnyChanges(): Boolean
	fun isSavable(): Boolean

	data object Loading : GearFormScreenUiState {
		override fun hasAnyChanges(): Boolean = false
		override fun isSavable(): Boolean = false
	}

	data class Create(
		val form: GearFormUiState,
		val topBar: GearFormTopBarUiState,
	) : GearFormScreenUiState {
		override fun isSavable(): Boolean = form.name.isNotBlank()

		override fun hasAnyChanges(): Boolean = form != GearFormUiState()
	}

	data class Edit(
		val initialValue: GearUpdate,
		val form: GearFormUiState,
		val topBar: GearFormTopBarUiState,
	) : GearFormScreenUiState {
		override fun isSavable(): Boolean =
			form.name.isNotBlank() && form.updatedModel(existing = initialValue) != initialValue

		override fun hasAnyChanges(): Boolean = form.updatedModel(existing = initialValue) != initialValue
	}
}

fun GearFormUiState.updatedModel(existing: GearUpdate): GearUpdate = existing.copy(
	name = name,
	avatar = avatar,
	ownerId = owner?.id,
)

sealed interface GearFormScreenUiAction {
	data object LoadGear : GearFormScreenUiAction
	data class UpdatedAvatar(val avatar: Avatar) : GearFormScreenUiAction
	data class UpdatedOwner(val owner: UUID?) : GearFormScreenUiAction

	data class GearFormAction(
		val action: GearFormUiAction,
	) : GearFormScreenUiAction
}

sealed interface GearFormScreenEvent {
	data object Dismissed : GearFormScreenEvent

	data class EditAvatar(val avatar: Avatar) : GearFormScreenEvent
	data class EditOwner(val owner: UUID?) : GearFormScreenEvent
}

fun MutableStateFlow<GearFormScreenUiState>.updateForm(
	function: (GearFormUiState) -> GearFormUiState,
) {
	this.update { state ->
		when (state) {
			GearFormScreenUiState.Loading -> state
			is GearFormScreenUiState.Create -> state.copy(form = function(state.form))
			is GearFormScreenUiState.Edit -> state.copy(form = function(state.form))
		}
	}
}
