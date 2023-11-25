package ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.common.utils.format
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.R
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.SourcePickerUiState
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.StatisticsOverviewUiAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourcePicker(
	state: SourcePickerUiState,
	onAction: (StatisticsOverviewUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	if (!state.isShowing) return

	ModalBottomSheet(
		onDismissRequest = { onAction(StatisticsOverviewUiAction.SourcePickerDismissed) },
		modifier = modifier,
		dragHandle = {},
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(16.dp),
			modifier = Modifier.padding(horizontal = 16.dp)
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier.padding(top = 16.dp),
			) {
				Text(
					text = stringResource(R.string.statistics_filter),
					style = MaterialTheme.typography.titleMedium,
					modifier = Modifier.weight(1f),
				)

				TextButton(
					onClick = { onAction(StatisticsOverviewUiAction.ApplyFilterClicked) },
					enabled = state.source != null,
				) {
					Text(text = stringResource(R.string.statistics_filter_apply))
				}
			}

			SourcePickerItem(
				title = stringResource(R.string.statistics_filter_bowler),
				subtitle = state.source?.bowler?.name ?: stringResource(RCoreDesign.string.all),
				onClick = { onAction(StatisticsOverviewUiAction.SourcePickerBowlerClicked) },
				enabled = true,
			)

			SourcePickerItem(
				title = stringResource(R.string.statistics_filter_league),
				subtitle = state.source?.league?.name ?: stringResource(RCoreDesign.string.all),
				onClick = { onAction(StatisticsOverviewUiAction.SourcePickerLeagueClicked) },
				enabled = state.source?.bowler != null,
			)

			SourcePickerItem(
				title = stringResource(R.string.statistics_filter_series),
				subtitle = state.source?.series?.date?.format("yyyy-MM-dd")
					?: stringResource(RCoreDesign.string.all),
				onClick = { onAction(StatisticsOverviewUiAction.SourcePickerSeriesClicked) },
				enabled = state.source?.league != null,
			)

			val gameOrdinal = state.source?.game?.index?.plus(1)
			SourcePickerItem(
				title = stringResource(R.string.statistics_filter_game),
				subtitle = if (gameOrdinal == null) {
					stringResource(RCoreDesign.string.all)
				} else {
					stringResource(RCoreDesign.string.game_with_ordinal, gameOrdinal)
				},
				onClick = { onAction(StatisticsOverviewUiAction.SourcePickerGameClicked) },
				enabled = state.source?.series != null,
				modifier = Modifier.padding(bottom = 16.dp),
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SourcePickerItem(
	title: String,
	subtitle: String,
	onClick: () -> Unit,
	enabled: Boolean,
	modifier: Modifier = Modifier,
) {
	Card(
		onClick = onClick,
		modifier = modifier,
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.padding(16.dp)
		) {
			Text(
				text = title,
				style = MaterialTheme.typography.titleMedium,
				color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
				modifier = Modifier.weight(1f),
			)

			Text(
				text = subtitle,
				style = MaterialTheme.typography.bodyMedium,
				color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
			)

			Icon(
				painter = painterResource(RCoreDesign.drawable.ic_chevron_right),
				contentDescription = null,
				tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
			)
		}
	}
}

@Preview
@Composable
private fun SourcePickerPreview() {
	Surface {
		SourcePicker(
			state = SourcePickerUiState(
				isShowing = true,
				source = null,
			),
			onAction = {},
		)
	}
}