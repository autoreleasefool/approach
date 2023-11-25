package ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.components.SourcePicker

@Composable
fun StatisticsOverview(
	state: StatisticsOverviewUiState,
	onAction: (StatisticsOverviewUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	SourcePicker(
		state = state.sourcePicker,
		onAction = onAction,
	)

	Column(
		verticalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(16.dp),
	) {
		Card {
			Text(
				text = stringResource(R.string.statistics_get_an_overview_title),
				style = MaterialTheme.typography.titleMedium,
				modifier = Modifier
					.padding(horizontal = 16.dp, vertical = 8.dp),
			)

			Text(
				text = stringResource(R.string.statistics_get_an_overview_description),
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.padding(bottom = 8.dp),
			)
			
			SuggestionChip(
				onClick = {},
				label = {
					Text(
						text = stringResource(R.string.coming_soon),
						style = MaterialTheme.typography.labelSmall,
					)
				},
				modifier = Modifier
					.align(Alignment.End)
					.padding(horizontal = 16.dp)
					.padding(bottom = 8.dp),
			)
		}

		Button(
			onClick = { onAction(StatisticsOverviewUiAction.ViewMoreClicked) },
			modifier = Modifier.fillMaxWidth(),
		) {
			Text(
				text = stringResource(R.string.view_detailed_statistics),
				style = MaterialTheme.typography.titleMedium,
			)
		}

		Card {
			Text(
				text = stringResource(R.string.statistics_view_more_title),
				style = MaterialTheme.typography.titleMedium,
				modifier = Modifier
					.padding(horizontal = 16.dp, vertical = 8.dp),
			)

			Text(
				text = stringResource(R.string.statistics_view_more_description),
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.padding(bottom = 8.dp),
			)
		}
	}
}

@Preview
@Composable
private fun StatisticsOverviewPreview() {
	Surface {
		StatisticsOverview(
			state = StatisticsOverviewUiState(),
			onAction = {},
		)
	}
}