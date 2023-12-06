package ca.josephroque.bowlingcompanion.feature.laneform.ui

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

@Composable
fun LaneFormFloatingActionButton(
	onAction: (LaneFormUiAction) -> Unit,
) {
	FloatingActionButton(onClick = { onAction(LaneFormUiAction.AddLanesClicked) }) {
		Icon(
			painter = painterResource(R.drawable.ic_list_add),
			contentDescription = stringResource(R.string.lane_form_add_multiple_lanes),
			tint = MaterialTheme.colorScheme.onSecondary,
		)
	}
}