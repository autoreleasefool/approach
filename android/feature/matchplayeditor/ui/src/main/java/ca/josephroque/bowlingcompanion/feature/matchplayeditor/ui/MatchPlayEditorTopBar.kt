package ca.josephroque.bowlingcompanion.feature.matchplayeditor.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchPlayEditorTopBar(
	gameIndex: Int,
	onAction: (MatchPlayEditorUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		title = {
			Text(
				text = stringResource(R.string.match_play_editor_title, gameIndex + 1),
			)
		},
		navigationIcon = {
			BackButton(onClick = { onAction(MatchPlayEditorUiAction.BackClicked) })
		},
		actions = {
			Text(
				text = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_save),
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier
					.clickable(onClick = { onAction(MatchPlayEditorUiAction.DoneClicked) })
					.padding(16.dp),
			)
		},
		scrollBehavior = scrollBehavior,
	)
}