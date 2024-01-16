package ca.josephroque.bowlingcompanion.feature.avatarform

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.ui.randomPastel
import ca.josephroque.bowlingcompanion.core.model.ui.toComposeColor
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.AvatarFormUiAction
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.AvatarFormUiState
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.ColorPickerUiAction
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.ColorPickerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AvatarFormViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
): ApproachViewModel<AvatarFormScreenEvent>() {
	private val _uiState: MutableStateFlow<AvatarFormScreenUiState> =
		MutableStateFlow(AvatarFormScreenUiState.Loading)
	val uiState = _uiState.asStateFlow()

	private val hasLoadedInitialState: Boolean
		get() = _uiState.value !is AvatarFormScreenUiState.Loading

	private val existingAvatar = Route.EditAvatar.getAvatar(savedStateHandle)?.let {
		Avatar.fromString(it)
	} ?: Avatar.default()

	fun handleAction(action: AvatarFormScreenUiAction) {
		when (action) {
			AvatarFormScreenUiAction.LoadAvatar -> loadAvatar()
			is AvatarFormScreenUiAction.AvatarFormAction -> handleAvatarFormAction(action.action)
		}
	}

	private fun handleAvatarFormAction(action: AvatarFormUiAction) {
		when (action) {
			AvatarFormUiAction.BackClicked -> handleBackClicked()
			AvatarFormUiAction.DoneClicked -> saveAvatar()
			AvatarFormUiAction.DiscardChangesClicked -> dismissEditor()
			AvatarFormUiAction.CancelDiscardChangesClicked -> setDiscardChangesDialog(isVisible = false)
			is AvatarFormUiAction.PrimaryColorClicked -> onPrimaryColorClicked()
			is AvatarFormUiAction.SecondaryColorClicked -> onSecondaryColorClicked()
			is AvatarFormUiAction.RandomizeColorsClicked -> onRandomizeColorsClicked()
			is AvatarFormUiAction.LabelChanged -> onLabelChanged(action.label)
			is AvatarFormUiAction.ColorPickerAction -> handleColorPickerAction(action.event)
		}
	}

	private fun handleColorPickerAction(action: ColorPickerUiAction) {
		when (action) {
			is ColorPickerUiAction.ColorChanged -> onColorChanged(action.color)
		}
	}

	private fun dismissEditor() {
		sendEvent(AvatarFormScreenEvent.Dismissed(existingAvatar))
	}

	private fun loadAvatar() {
		if (hasLoadedInitialState) return
		_uiState.value = AvatarFormScreenUiState.Loaded(
			form = AvatarFormUiState(
				initialValue = existingAvatar,
				avatar = existingAvatar,
				colorPickerState = ColorPickerUiState.Hidden,
				isShowingDiscardChangesDialog = false,
			),
		)
	}

	private fun handleBackClicked() {
		if (_uiState.value.hasAnyChanges()) {
			setDiscardChangesDialog(isVisible = true)
		} else {
			dismissEditor()
		}
	}

	private fun setDiscardChangesDialog(isVisible: Boolean) {
		_uiState.updateForm { it.copy(isShowingDiscardChangesDialog = isVisible) }
	}

	private fun saveAvatar() {
		when (val state = _uiState.value) {
			AvatarFormScreenUiState.Loading -> sendEvent(AvatarFormScreenEvent.Dismissed(existingAvatar))
			is AvatarFormScreenUiState.Loaded -> sendEvent(AvatarFormScreenEvent.Dismissed(state.form.avatar))
		}
	}

	private fun onColorChanged(color: Color) {
		_uiState.updateForm {
			it.copy(
				avatar = when (it.colorPickerState) {
					is ColorPickerUiState.Primary -> it.avatar.copy(primaryColor = color.toRGB())
					is ColorPickerUiState.Secondary -> it.avatar.copy(secondaryColor = color.toRGB())
					ColorPickerUiState.Hidden -> it.avatar
				},
				colorPickerState = ColorPickerUiState.Hidden,
			)
		}
	}

	private fun onPrimaryColorClicked() {
		_uiState.updateForm {
			it.copy(
				colorPickerState = ColorPickerUiState.Primary(
					initialColor = it.avatar.primaryColor.toComposeColor(),
				),
			)
		}
	}

	private fun onSecondaryColorClicked() {
		_uiState.updateForm {
			it.copy(
				colorPickerState = ColorPickerUiState.Secondary(
					initialColor = it.avatar.secondaryColor.toComposeColor(),
				),
			)
		}
	}

	private fun onRandomizeColorsClicked() {
		_uiState.updateForm {
			it.copy(
				avatar = it.avatar.copy(
					primaryColor = Avatar.RGB.randomPastel(),
					secondaryColor = Avatar.RGB.randomPastel(),
				),
			)
		}
	}

	private fun onLabelChanged(label: String) {
		_uiState.updateForm {
			it.copy(avatar = it.avatar.copy(label = label))
		}
	}
}

private fun Color.toRGB(): Avatar.RGB = Avatar.RGB(
	red = (red * 255).toInt(),
	green = (green * 255).toInt(),
	blue = (blue * 255).toInt(),
)