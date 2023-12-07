package ca.josephroque.bowlingcompanion.feature.statisticsdetails.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticListEntry

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatisticsDetailsList(
	state: StatisticsDetailsListUiState,
	onAction: (StatisticsDetailsListUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LazyColumn(modifier = modifier) {
		if (state.statistics.isEmpty()) {
			// TODO: Show Empty State
		} else {
			state.statistics.forEach { group ->
				stickyHeader {
					Text(
						text = stringResource(group.title),
						style = MaterialTheme.typography.titleMedium,
						modifier = Modifier
							.fillMaxWidth()
							.background(MaterialTheme.colorScheme.surfaceVariant)
							.padding(horizontal = 16.dp, vertical = 8.dp),
					)
				}

				items(
					items = group.entries,
					key = { it.id },
				) {
					ListEntry(it, onAction)
				}
			}
		}
	}
}

@Composable
private fun ListEntry(
	entry: StatisticListEntry,
	onAction: (StatisticsDetailsListUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
			.fillMaxWidth()
			.clickable(onClick = { onAction(StatisticsDetailsListUiAction.StatisticClicked(entry.id)) })
			.padding(16.dp),
	) {
		Text(
			text = stringResource(entry.id.titleResourceId),
			style = MaterialTheme.typography.titleMedium,
			color = MaterialTheme.colorScheme.onSurface,
			modifier = Modifier.weight(1f).padding(end = 16.dp),
		)

		Text(
			text = entry.value,
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
	}
}