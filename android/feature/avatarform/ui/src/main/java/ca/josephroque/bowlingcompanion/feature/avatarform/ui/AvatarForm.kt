package ca.josephroque.bowlingcompanion.feature.avatarform.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.DiscardChangesDialog
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.ui.AvatarImage
import ca.josephroque.bowlingcompanion.core.model.ui.toComposeColor
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.components.ColorPicker

@Composable
fun AvatarForm(
	state: AvatarFormUiState,
	onAction: (AvatarFormUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	if (state.isShowingDiscardChangesDialog) {
		DiscardChangesDialog(
			onDiscardChanges = { onAction(AvatarFormUiAction.DiscardChangesClicked) },
			onDismiss = { onAction(AvatarFormUiAction.CancelDiscardChangesClicked) },
		)
	}

	ColorPicker(
		state = state.colorPickerState,
		onAction = { onAction(AvatarFormUiAction.ColorPickerAction(it)) },
	)

	Column(
		verticalArrangement = Arrangement.spacedBy(8.dp),
		modifier = modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(16.dp)
			.imePadding(),
	) {
		AvatarImage(
			avatar = state.avatar,
			modifier = Modifier
				.size(80.dp)
				.align(Alignment.CenterHorizontally)
		)

		LabelTextField(
			label = state.avatar.label,
			onLabelChanged = { onAction(AvatarFormUiAction.LabelChanged(it)) },
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
					.clickable(onClick = { onAction(AvatarFormUiAction.PrimaryColorClicked) })
					.size(48.dp),
				onDraw = {
					drawCircle(state.avatar.primaryColor.toComposeColor())
				}
			)

			Canvas(
				modifier = Modifier
					.clickable(onClick = { onAction(AvatarFormUiAction.SecondaryColorClicked) })
					.size(48.dp),
				onDraw = {
					drawCircle(state.avatar.secondaryColor.toComposeColor())
				}
			)
		}

		TextButton(
			onClick = { onAction(AvatarFormUiAction.RandomizeColorsClicked) },
			modifier = Modifier.align(Alignment.CenterHorizontally),
		) {
			Text(text = stringResource(R.string.avatar_form_randomize_color))
		}
	}
}

@Composable
private fun LabelTextField(
	label: String,
	onLabelChanged: (String) -> Unit,
) {
	OutlinedTextField(
		value = label,
		onValueChange = onLabelChanged,
		keyboardOptions = KeyboardOptions(KeyboardCapitalization.Words),
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
		modifier = Modifier.fillMaxWidth(),
	)
}

@Preview
@Composable
private fun AvatarFormPreview() {
	Surface {
		AvatarForm(
			state = AvatarFormUiState(
				initialValue = Avatar.default(),
				avatar = Avatar.default(),
				colorPickerState = ColorPickerUiState.Hidden,
				isShowingDiscardChangesDialog = false,
			),
			onAction = {},
		)
	}
}