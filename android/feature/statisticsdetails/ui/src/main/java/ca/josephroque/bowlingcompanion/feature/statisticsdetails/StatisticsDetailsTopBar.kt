package ca.josephroque.bowlingcompanion.feature.statisticsdetails

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsDetailsTopBar(
	onAction: (StatisticsDetailsTopBarUiAction) -> Unit,
) {
	TopAppBar(
		title = {
			Text(
				text = stringResource(R.string.statistics_details_title),
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = {
			BackButton(onClick = { onAction(StatisticsDetailsTopBarUiAction.BackClicked) })
		}
	)
}