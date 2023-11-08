package ca.josephroque.bowlingcompanion.feature.avatarform

import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.AvatarFormUiAction
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.AvatarFormUiState

sealed interface AvatarFormScreenUiState {
	data object Loading: AvatarFormScreenUiState

	data class Loaded(
		val form: AvatarFormUiState,
	): AvatarFormScreenUiState
}

sealed interface AvatarFormScreenUiAction {
	data object LoadAvatar: AvatarFormScreenUiAction

	data class AvatarFormAction(
		val action: AvatarFormUiAction,
	): AvatarFormScreenUiAction
}

sealed interface AvatarFormScreenEvent {
	data class Dismissed(val result: Avatar): AvatarFormScreenEvent
}