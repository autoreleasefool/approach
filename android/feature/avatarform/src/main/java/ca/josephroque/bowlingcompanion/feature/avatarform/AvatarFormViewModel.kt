package ca.josephroque.bowlingcompanion.feature.avatarform

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.ui.randomPastel
import ca.josephroque.bowlingcompanion.core.model.ui.toComposeColor
import ca.josephroque.bowlingcompanion.feature.avatarform.navigation.AVATAR_VALUE
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

	private val existingAvatar = savedStateHandle.get<String>(AVATAR_VALUE)?.let {
		Avatar.fromString(it)
	} ?: Avatar.default()

	private fun getFormUiState(): AvatarFormUiState? {
		return when (val state = _uiState.value) {
			AvatarFormScreenUiState.Loading -> null
			is AvatarFormScreenUiState.Loaded -> state.form
		}
	}

	private fun setFormUiState(state: AvatarFormUiState) {
		when (val uiState = _uiState.value) {
			AvatarFormScreenUiState.Loading -> Unit
			is AvatarFormScreenUiState.Loaded -> _uiState.value = uiState.copy(form = state)
		}
	}

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
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(
			isShowingDiscardChangesDialog = isVisible,
		))
	}

	private fun saveAvatar() {
		when (val state = _uiState.value) {
			AvatarFormScreenUiState.Loading -> sendEvent(AvatarFormScreenEvent.Dismissed(existingAvatar))
			is AvatarFormScreenUiState.Loaded -> sendEvent(AvatarFormScreenEvent.Dismissed(state.form.avatar))
		}
	}

	private fun onColorChanged(color: Color) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(
			avatar = when (state.colorPickerState) {
				is ColorPickerUiState.Primary -> state.avatar.copy(primaryColor = color.toRGB())
				is ColorPickerUiState.Secondary -> state.avatar.copy(secondaryColor = color.toRGB())
				ColorPickerUiState.Hidden -> state.avatar
			},
			colorPickerState = ColorPickerUiState.Hidden,
		))
	}

	private fun onPrimaryColorClicked() {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(
			colorPickerState = ColorPickerUiState.Primary(
				initialColor = state.avatar.primaryColor.toComposeColor(),
			),
		))
	}

	private fun onSecondaryColorClicked() {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(
			colorPickerState = ColorPickerUiState.Secondary(
				initialColor = state.avatar.secondaryColor.toComposeColor(),
			),
		))
	}

	private fun onRandomizeColorsClicked() {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(
			avatar = state.avatar.copy(
				primaryColor = Avatar.RGB.randomPastel(),
				secondaryColor = Avatar.RGB.randomPastel(),
			),
		))
	}

	private fun onLabelChanged(label: String) {
		val state = getFormUiState() ?: return
		setFormUiState(state.copy(
			avatar = state.avatar.copy(label = label),
		))
	}
}

private fun Color.toRGB(): Avatar.RGB = Avatar.RGB(
	red = (red * 255).toInt(),
	green = (green * 255).toInt(),
	blue = (blue * 255).toInt(),
)