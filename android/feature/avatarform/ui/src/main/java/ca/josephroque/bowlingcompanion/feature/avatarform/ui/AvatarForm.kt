package ca.josephroque.bowlingcompanion.feature.avatarform.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.ui.AvatarImage
import ca.josephroque.bowlingcompanion.core.model.ui.toComposeColor
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun AvatarForm(
	state: AvatarFormUiState,
	onColorChanged: (Color) -> Unit,
	onPrimaryColorClicked: () -> Unit,
	onSecondaryColorClicked: () -> Unit,
	onRandomizeColorsClicked: () -> Unit,
	onLabelChanged: (String) -> Unit,
	modifier: Modifier = Modifier,
) {
	when (state) {
		AvatarFormUiState.Loading -> Unit
		is AvatarFormUiState.Success -> AvatarForm(
			avatar = state.avatar,
			colorPickerState = state.colorPickerState,
			onColorChanged = onColorChanged,
			onPrimaryColorClicked = onPrimaryColorClicked,
			onSecondaryColorClicked = onSecondaryColorClicked,
			onRandomizeColorsClicked = onRandomizeColorsClicked,
			onLabelChanged = onLabelChanged,
			modifier = modifier,
		)
	}
}

@Composable
private fun AvatarForm(
	avatar: Avatar,
	colorPickerState: ColorPickerUiState,
	onColorChanged: (Color) -> Unit,
	onPrimaryColorClicked: () -> Unit,
	onSecondaryColorClicked: () -> Unit,
	onLabelChanged: (String) -> Unit,
	onRandomizeColorsClicked: () -> Unit,
	modifier: Modifier = Modifier,
) {
	ColorPicker(
		state = colorPickerState,
		onColorChanged = onColorChanged,
	)

	Column(
		verticalArrangement = Arrangement.spacedBy(8.dp),
		modifier = modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(16.dp),
	) {
		AvatarImage(
			avatar = avatar,
			modifier = Modifier
				.size(80.dp)
				.align(Alignment.CenterHorizontally)
		)

		LabelTextField(
			label = avatar.label,
			onLabelChanged = onLabelChanged,
		)

		Text(
			text = stringResource(R.string.avatar_form_background_color),
			style = MaterialTheme.typography.bodyMedium,
		)

		Row(
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.align(Alignment.CenterHorizontally)
		) {
			Canvas(
				modifier = Modifier
					.clickable(onClick = onPrimaryColorClicked)
					.size(48.dp),
				onDraw = {
					drawCircle(avatar.primaryColor.toComposeColor())
				}
			)

			Canvas(
				modifier = Modifier
					.clickable(onClick = onSecondaryColorClicked)
					.size(48.dp),
				onDraw = {
					drawCircle(avatar.secondaryColor.toComposeColor())
				}
			)
		}

		TextButton(
			onClick = onRandomizeColorsClicked,
			modifier = Modifier.align(Alignment.CenterHorizontally),
		) {
			Text(text = stringResource(R.string.avatar_form_randomize_color))
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorPicker(
	state: ColorPickerUiState,
	onColorChanged: (Color) -> Unit,
	modifier: Modifier = Modifier,
) {
	val controller = rememberColorPickerController()
	val initialColor = remember(state) {
		when (state) {
			is ColorPickerUiState.Primary -> state.initialColor
			is ColorPickerUiState.Secondary -> state.initialColor
			else -> Color.Red
		}
	}

	val currentColor = remember(state) {
		mutableStateOf(
			when (state) {
				is ColorPickerUiState.Primary -> state.initialColor
				is ColorPickerUiState.Secondary -> state.initialColor
				else -> Color.Red
			}
		)
	}

	when (state) {
		ColorPickerUiState.Hidden -> Unit
		is ColorPickerUiState.Primary, is ColorPickerUiState.Secondary -> {
			AlertDialog(
				onDismissRequest = { onColorChanged(initialColor) }
			) {
				Surface(
					shape = RoundedCornerShape(corner = CornerSize(8.dp)),
					color = MaterialTheme.colorScheme.background,
				) {
					Column(
						verticalArrangement = Arrangement.spacedBy(8.dp),
						modifier = Modifier.padding(16.dp),
					) {
						HsvColorPicker(
							modifier = modifier
								.fillMaxWidth()
								.heightIn(max = 300.dp)
								.aspectRatio(1f),
							controller = controller,
							initialColor = initialColor,
							onColorChanged = { currentColor.value = it.color }
						)

						BrightnessSlider(
							modifier = Modifier
								.fillMaxWidth()
								.height(40.dp),
							controller = controller,
						)

						Row(
							horizontalArrangement = Arrangement.SpaceBetween,
							verticalAlignment = Alignment.CenterVertically,
							modifier = Modifier.fillMaxWidth(),
						) {
							TextButton(onClick = { onColorChanged(initialColor) }) {
								Text(text = stringResource(RCoreDesign.string.action_cancel))
							}

							TextButton(onClick = { onColorChanged(currentColor.value) }) {
								Text(text = stringResource(RCoreDesign.string.action_save))
							}
						}
					}
				}
			}
		}
	}
}

@Composable
private fun LabelTextField(
	label: String,
	onLabelChanged: (String) -> Unit,
	modifier: Modifier = Modifier,
) {
	OutlinedTextField(
		value = label,
		onValueChange = onLabelChanged,
		leadingIcon = {
			Icon(
				Icons.Filled.Person,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		},
		label = {
			Text(
				text = stringResource(R.string.avatar_form_label),
				style = MaterialTheme.typography.bodyLarge
			)
		},
		singleLine = true,
		modifier = modifier.fillMaxWidth(),
	)
}

sealed interface ColorPickerUiState {
	data object Hidden: ColorPickerUiState
	data class Primary(val initialColor: Color): ColorPickerUiState
	data class Secondary(val initialColor: Color): ColorPickerUiState
}

sealed interface AvatarFormUiState {
	data object Loading : AvatarFormUiState
	data class Success(
		val avatar: Avatar,
		val colorPickerState: ColorPickerUiState,
	): AvatarFormUiState
}

@Preview
@Composable
private fun AvatarFormPreview() {
	Surface {
		AvatarForm(
			state = AvatarFormUiState.Success(
				avatar = Avatar.default(),
				colorPickerState = ColorPickerUiState.Hidden,
			),
			onColorChanged = {},
			onPrimaryColorClicked = {},
			onSecondaryColorClicked = {},
			onRandomizeColorsClicked = {},
			onLabelChanged = {},
		)
	}
}