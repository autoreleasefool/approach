package ca.josephroque.bowlingcompanion.feature.laneform.ui

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
fun LaneFormTopBar(
	onAction: (LaneFormUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		title = { Title() },
		navigationIcon = {
			BackButton(onClick = { onAction(LaneFormUiAction.BackClicked) })
		},
		actions = {
			Actions(onAction = onAction)
		},
		scrollBehavior = scrollBehavior,
	)
}

@Composable
private fun Title() {
	Text(
		text = stringResource(R.string.lane_form_title),
		style = MaterialTheme.typography.titleMedium,
	)
}

@Composable
private fun Actions(
	onAction: (LaneFormUiAction) -> Unit,
) {
	Text(
		text = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_save),
		modifier = Modifier
			.clickable(onClick = { onAction(LaneFormUiAction.DoneClicked) })
			.padding(16.dp)
	)
}