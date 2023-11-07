package ca.josephroque.bowlingcompanion.feature.avatarform

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.ui.randomPastel
import ca.josephroque.bowlingcompanion.core.model.ui.toComposeColor
import ca.josephroque.bowlingcompanion.feature.avatarform.navigation.AVATAR_VALUE
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.AvatarFormUiState
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.ColorPickerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AvatarFormViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
): ViewModel() {
	private val _uiState: MutableStateFlow<AvatarFormUiState> = MutableStateFlow(AvatarFormUiState.Loading)
	val uiState = _uiState.asStateFlow()

	private val _events: MutableStateFlow<AvatarFormEvent?> = MutableStateFlow(null)
	val events = _events.asStateFlow()

	private val existingAvatar = savedStateHandle.get<String>(AVATAR_VALUE)?.let {
		Avatar.fromString(it)
	} ?: Avatar.default()

	fun loadAvatar() {
		_uiState.value = AvatarFormUiState.Success(
			avatar = existingAvatar,
			colorPickerState = ColorPickerUiState.Hidden,
		)
	}

	fun saveAvatar() {
		when (val state = _uiState.value) {
			AvatarFormUiState.Loading -> {
				_events.value = AvatarFormEvent.Dismissed(existingAvatar)
			}
			is AvatarFormUiState.Success -> {
				_events.value = AvatarFormEvent.Dismissed(state.avatar)
			}
		}
	}

	fun onColorChanged(color: Color) {
		when (val state = _uiState.value) {
			AvatarFormUiState.Loading -> Unit
			is AvatarFormUiState.Success -> _uiState.value = state.copy(
				avatar = when (state.colorPickerState) {
					is ColorPickerUiState.Primary -> state.avatar.copy(primaryColor = color.toRGB())
					is ColorPickerUiState.Secondary -> state.avatar.copy(secondaryColor = color.toRGB())
					ColorPickerUiState.Hidden -> state.avatar
				},
				colorPickerState = ColorPickerUiState.Hidden,
			)
		}
	}

	fun onPrimaryColorClicked() {
		when (val state = _uiState.value) {
			AvatarFormUiState.Loading -> Unit
			is AvatarFormUiState.Success -> _uiState.value = state.copy(
				colorPickerState = ColorPickerUiState.Primary(
					initialColor = state.avatar.primaryColor.toComposeColor(),
				),
			)
		}
	}

	fun onSecondaryColorClicked() {
		when (val state = _uiState.value) {
			AvatarFormUiState.Loading -> Unit
			is AvatarFormUiState.Success -> _uiState.value = state.copy(
				colorPickerState = ColorPickerUiState.Secondary(
					initialColor = state.avatar.secondaryColor.toComposeColor(),
				),
			)
		}
	}

	fun onRandomizeColorsClicked() {
		when (val state = _uiState.value) {
			AvatarFormUiState.Loading -> Unit
			is AvatarFormUiState.Success -> _uiState.value = state.copy(
				avatar = state.avatar.copy(
					primaryColor = Avatar.RGB.randomPastel(),
					secondaryColor = Avatar.RGB.randomPastel(),
				),
			)
		}
	}

	fun onLabelChanged(label: String) {
		when (val state = _uiState.value) {
			AvatarFormUiState.Loading -> Unit
			is AvatarFormUiState.Success -> _uiState.value = state.copy(
				avatar = state.avatar.copy(label = label),
			)
		}
	}
}

private fun Color.toRGB(): Avatar.RGB = Avatar.RGB(
	red = (red * 255).toInt(),
	green = (green * 255).toInt(),
	blue = (blue * 255).toInt(),
)

sealed interface AvatarFormEvent {
	data class Dismissed(val avatar: Avatar): AvatarFormEvent
}