package ca.josephroque.bowlingcompanion.feature.teamdetails.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun TeamDetailsFloatingActionButton(
	onAction: (TeamDetailsFloatingActionButtonUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	FloatingActionButton(
		onClick = { onAction(TeamDetailsFloatingActionButtonUiAction.AddSeriesClicked) },
		modifier = modifier,
	) {
		Icon(
			Icons.Default.PlayArrow,
			contentDescription = stringResource(R.string.team_series_list_add),
		)
	}
}
