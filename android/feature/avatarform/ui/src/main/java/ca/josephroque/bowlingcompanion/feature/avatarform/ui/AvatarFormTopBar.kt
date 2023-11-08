package ca.josephroque.bowlingcompanion.feature.avatarform.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarFormTopBar(
	onAction: (AvatarFormUiAction) -> Unit,
) {
	TopAppBar(
		title = {
			Text(
				text = stringResource(R.string.avatar_form_title),
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = { BackButton(onClick = { onAction(AvatarFormUiAction.BackClicked) }) },
		actions = {
			TextButton(onClick = { onAction(AvatarFormUiAction.DoneClicked) }) {
				Text(
					text = stringResource(RCoreDesign.string.action_save),
					style = MaterialTheme.typography.bodyMedium,
				)
			}
		},
	)
}