package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.components.DetailNavigationButton
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.components.DetailRow

@Composable
internal fun StatisticsButtons(
	gameIndex: Int,
	openGameStats: () -> Unit,
	openSeriesStats: () -> Unit,
	modifier: Modifier = Modifier,
) {
	DetailRow(modifier = modifier) {
		Icon(
			painter = painterResource(R.drawable.ic_monitoring),
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onSurface,
		)

		DetailNavigationButton(
			title = stringResource(R.string.game_editor_view_stats_series),
			subtitle = stringResource(R.string.game_editor_view_stats),
			onClick = openSeriesStats
		)

		DetailNavigationButton(
			title = stringResource(R.string.game_editor_view_stats_game, gameIndex + 1),
			subtitle = stringResource(R.string.game_editor_view_stats),
			onClick = openGameStats
		)
	}
}