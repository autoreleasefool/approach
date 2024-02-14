package ca.josephroque.bowlingcompanion.feature.avatarform

import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.AvatarFormUiAction
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.AvatarFormUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

sealed interface AvatarFormScreenUiState {
	fun hasAnyChanges(): Boolean
	fun isSavable(): Boolean

	data object Loading : AvatarFormScreenUiState {
		override fun hasAnyChanges(): Boolean = false
		override fun isSavable(): Boolean = false
	}

	data class Loaded(
		val form: AvatarFormUiState,
	) : AvatarFormScreenUiState {
		override fun isSavable(): Boolean = true
		override fun hasAnyChanges(): Boolean = form.avatar != form.initialValue
	}
}

sealed interface AvatarFormScreenUiAction {
	data object LoadAvatar : AvatarFormScreenUiAction

	data class AvatarFormAction(
		val action: AvatarFormUiAction,
	) : AvatarFormScreenUiAction
}

sealed interface AvatarFormScreenEvent {
	data class Dismissed(val result: Avatar) : AvatarFormScreenEvent
}

fun MutableStateFlow<AvatarFormScreenUiState>.updateForm(
	function: (AvatarFormUiState) -> AvatarFormUiState,
) {
	this.update { state ->
		when (state) {
			AvatarFormScreenUiState.Loading -> state
			is AvatarFormScreenUiState.Loaded -> state.copy(form = function(state.form))
		}
	}
}
