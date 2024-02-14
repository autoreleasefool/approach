package ca.josephroque.bowlingcompanion.feature.avatarform.ui

import androidx.compose.ui.graphics.Color
import ca.josephroque.bowlingcompanion.core.model.Avatar

data class AvatarFormUiState(
	val initialValue: Avatar,
	val avatar: Avatar,
	val colorPickerState: ColorPickerUiState,
	val isShowingDiscardChangesDialog: Boolean,
)

sealed interface AvatarFormUiAction {
	data object BackClicked : AvatarFormUiAction
	data object DoneClicked : AvatarFormUiAction

	data object PrimaryColorClicked : AvatarFormUiAction
	data object SecondaryColorClicked : AvatarFormUiAction
	data object RandomizeColorsClicked : AvatarFormUiAction

	data object DiscardChangesClicked : AvatarFormUiAction
	data object CancelDiscardChangesClicked : AvatarFormUiAction

	data class LabelChanged(val label: String) : AvatarFormUiAction
	data class ColorPickerAction(val event: ColorPickerUiAction) : AvatarFormUiAction
}

sealed interface ColorPickerUiState {
	data object Hidden : ColorPickerUiState
	data class Primary(val initialColor: Color) : ColorPickerUiState
	data class Secondary(val initialColor: Color) : ColorPickerUiState
}

sealed interface ColorPickerUiAction {
	data class ColorChanged(val color: Color) : ColorPickerUiAction
}
