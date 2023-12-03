package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.statisticpicker

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatisticPicker(
	state: StatisticPickerUiState,
	onAction: (StatisticPickerUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LazyColumn(modifier = modifier) {
		state.statistics.forEach { group ->
			stickyHeader {
				StatisticCategoryHeader(group.title)
			}

			itemsIndexed(
				items = group.statistics,
				key = { _, statistic -> statistic.id },
			) { index, statistic ->
				StatisticRow(
					title = statistic.id.titleResourceId,
					isSelected = statistic == state.selectedStatistic,
					onClick = { onAction(StatisticPickerUiAction.StatisticClicked(statistic)) },
				)

				if (index < group.statistics.size - 1) {
					Divider(modifier = Modifier.padding(start = 56.dp))
				}
			}
		}
	}
}

@Composable
private fun StatisticCategoryHeader(title: Int) {
	Text(
		text = stringResource(title),
		style = MaterialTheme.typography.bodyLarge,
		modifier = Modifier
			.fillMaxWidth()
			.background(MaterialTheme.colorScheme.surfaceVariant)
			.padding(horizontal = 16.dp, vertical = 8.dp),
	)
}

@Composable
private fun StatisticRow(
	title: Int,
	isSelected: Boolean,
	onClick: () -> Unit,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick)
			.padding(16.dp)
	) {
		if (isSelected) {
			Icon(
				painter = painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_check_box),
				contentDescription = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.cd_resource_selected),
				tint = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.purple_500)
			)
		} else {
			Icon(
				painter = painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_check_box_outline),
				contentDescription = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.cd_resource_deselected),
				tint = MaterialTheme.colorScheme.onSurface,
			)
		}

		Text(
			text = stringResource(title),
			style = MaterialTheme.typography.bodyMedium,
		)
	}
}