package ca.josephroque.bowlingcompanion.feature.alleyform

import ca.josephroque.bowlingcompanion.core.model.AlleyUpdate
import ca.josephroque.bowlingcompanion.feature.alleyform.ui.AlleyFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.alleyform.ui.AlleyFormUiAction
import ca.josephroque.bowlingcompanion.feature.alleyform.ui.AlleyFormUiState
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

sealed interface AlleyFormScreenUiState {
	fun hasAnyChanges(): Boolean
	fun isSavable(): Boolean

	data object Loading : AlleyFormScreenUiState {
		override fun hasAnyChanges(): Boolean = false
		override fun isSavable(): Boolean = false
	}

	data class Create(
		val form: AlleyFormUiState,
		val topBar: AlleyFormTopBarUiState,
	) : AlleyFormScreenUiState {
		override fun isSavable(): Boolean = form.name.isNotBlank()

		override fun hasAnyChanges(): Boolean = form != AlleyFormUiState()
	}

	data class Edit(
		val initialValue: AlleyUpdate,
		val form: AlleyFormUiState,
		val topBar: AlleyFormTopBarUiState,
	) : AlleyFormScreenUiState {
		override fun isSavable(): Boolean =
			form.name.isNotBlank() && form.updatedModel(existing = initialValue) != initialValue

		override fun hasAnyChanges(): Boolean = form.updatedModel(existing = initialValue) != initialValue
	}
}

fun AlleyFormUiState.updatedModel(existing: AlleyUpdate): AlleyUpdate = AlleyUpdate(
	id = existing.id,
	name = name,
	material = material,
	pinFall = pinFall,
	mechanism = mechanism,
	pinBase = pinBase,
	lanes = lanes,
)

sealed interface AlleyFormScreenUiAction {
	data object LoadAlley : AlleyFormScreenUiAction

	data class LanesUpdated(val lanes: List<UUID>) : AlleyFormScreenUiAction
	data class AlleyForm(val action: AlleyFormUiAction) : AlleyFormScreenUiAction
}

sealed interface AlleyFormScreenEvent {
	data object Dismissed : AlleyFormScreenEvent

	data class ManageLanes(val existingLanes: List<UUID>) : AlleyFormScreenEvent
}

fun MutableStateFlow<AlleyFormScreenUiState>.updateForm(
	function: (AlleyFormUiState) -> AlleyFormUiState,
) {
	this.update { state ->
		when (state) {
			AlleyFormScreenUiState.Loading -> state
			is AlleyFormScreenUiState.Create -> state.copy(form = function(state.form))
			is AlleyFormScreenUiState.Edit -> state.copy(form = function(state.form))
		}
	}
}
