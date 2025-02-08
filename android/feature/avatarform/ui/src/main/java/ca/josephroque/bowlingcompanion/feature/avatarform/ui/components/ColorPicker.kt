package ca.josephroque.bowlingcompanion.feature.avatarform.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.ColorPickerUiAction
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.ColorPickerUiState
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPicker(state: ColorPickerUiState, onAction: (ColorPickerUiAction) -> Unit, modifier: Modifier = Modifier) {
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
			},
		)
	}

	when (state) {
		ColorPickerUiState.Hidden -> Unit
		is ColorPickerUiState.Primary, is ColorPickerUiState.Secondary -> {
			BasicAlertDialog(
				onDismissRequest = { onAction(ColorPickerUiAction.ColorChanged(initialColor)) },
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
							onColorChanged = { currentColor.value = it.color },
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
							TextButton(onClick = { onAction(ColorPickerUiAction.ColorChanged(initialColor)) }) {
								Text(
									text = stringResource(
										ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_cancel,
									),
								)
							}

							TextButton(onClick = { onAction(ColorPickerUiAction.ColorChanged(currentColor.value)) }) {
								Text(
									text = stringResource(
										ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_save,
									),
								)
							}
						}
					}
				}
			}
		}
	}
}
