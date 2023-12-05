package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.components.NavigationButton
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R

@Composable
internal fun StatisticsButtons(
	gameIndex: Int,
	onAction: (GameDetailsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier,
	) {
		NavigationButton(
			title = stringResource(R.string.game_editor_view_stats_series),
			subtitle = stringResource(R.string.game_editor_view_stats),
			onClick = { onAction(GameDetailsUiAction.ViewSeriesStatsClicked) },
			modifier = Modifier.weight(1f),
			icon = {
				Icon(
					painter = painterResource(RCoreDesign.drawable.ic_monitoring),
					contentDescription = null,
					tint = MaterialTheme.colorScheme.onSurface,
				)
			},
			trailingIcon = null,
		)

		NavigationButton(
			title = stringResource(R.string.game_editor_view_stats_game, gameIndex + 1),
			subtitle = stringResource(R.string.game_editor_view_stats),
			onClick = { onAction(GameDetailsUiAction.ViewGameStatsClicked) },
			modifier = Modifier.weight(1f),
			icon = {
				Icon(
					painter = painterResource(RCoreDesign.drawable.ic_monitoring),
					contentDescription = null,
					tint = MaterialTheme.colorScheme.onSurface,
				)
			},
			trailingIcon = null,
		)
	}
}

@Preview
@Composable
private fun StatisticsButtonsPreview() {
	Surface {
		StatisticsButtons(
			gameIndex = 0,
			onAction = {},
		)
	}
}