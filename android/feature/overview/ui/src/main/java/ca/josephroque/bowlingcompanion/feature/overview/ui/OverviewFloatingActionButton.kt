package ca.josephroque.bowlingcompanion.feature.overview.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun OverviewFloatingActionButton(
	onAction: (OverviewUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	FloatingActionButton(
		onClick = { onAction(OverviewUiAction.QuickPlayClicked) },
		modifier = modifier,
	) {
		Icon(
			Icons.Default.PlayArrow,
			contentDescription = stringResource(R.string.overview_quick_play),
		)
	}
}