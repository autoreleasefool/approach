package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.error

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.components.CloseButton
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsWidgetErrorTopBar(
	onAction: (StatisticsWidgetErrorTopBarUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		scrollBehavior = scrollBehavior,
		title = {
			Text(
				text = stringResource(R.string.statistics_widget_error_title),
				style = MaterialTheme.typography.titleMedium,
			)
		},
		navigationIcon = {
			CloseButton(onClick = { onAction(StatisticsWidgetErrorTopBarUiAction.BackClicked) })
		},
	)
}
