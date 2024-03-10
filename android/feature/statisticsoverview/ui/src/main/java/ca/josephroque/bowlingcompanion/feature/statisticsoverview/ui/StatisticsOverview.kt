package ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun StatisticsOverview(
	state: StatisticsOverviewUiState,
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues(0.dp),
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState()),
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(8.dp),
			modifier = Modifier.padding(horizontal = 16.dp),
		) {
			Text(
				text = stringResource(R.string.statistics_get_an_overview_title),
				style = MaterialTheme.typography.titleMedium,
			)

			Text(
				text = stringResource(R.string.statistics_get_an_overview_description),
				style = MaterialTheme.typography.bodyMedium,
			)

			SuggestionChip(
				onClick = {},
				label = {
					Text(
						text = stringResource(R.string.coming_soon),
						style = MaterialTheme.typography.labelSmall,
					)
				},
				modifier = Modifier.align(Alignment.End),
			)
		}

		HorizontalDivider(thickness = 8.dp)

		Column(
			verticalArrangement = Arrangement.spacedBy(8.dp),
			modifier = Modifier
				.padding(horizontal = 16.dp),
		) {
			Text(
				text = stringResource(R.string.statistics_view_more_title),
				style = MaterialTheme.typography.titleMedium,
			)

			Text(
				text = stringResource(R.string.statistics_view_more_description),
				style = MaterialTheme.typography.bodyMedium,
			)
		}

		Spacer(modifier = Modifier.padding(contentPadding))
	}
}

@Composable
fun ViewDetailedStatisticsFloatingActionButton(
	onAction: (StatisticsOverviewUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	ExtendedFloatingActionButton(
		modifier = modifier,
		text = {
			Text(
				text = stringResource(R.string.view_detailed_statistics),
				style = MaterialTheme.typography.bodyMedium,
			)
		},
		icon = {
			Icon(
				Icons.Default.Search,
				contentDescription = null,
			)
		},
		onClick = {
			onAction(StatisticsOverviewUiAction.ViewMoreClicked)
		},
	)
}
