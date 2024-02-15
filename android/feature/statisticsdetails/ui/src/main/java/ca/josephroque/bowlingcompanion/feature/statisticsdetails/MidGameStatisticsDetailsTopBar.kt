package ca.josephroque.bowlingcompanion.feature.statisticsdetails

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.components.CloseButton
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MidGameStatisticsDetailsTopBar(
	state: MidGameStatisticsDetailsTopBarUiState?,
	onAction: (MidGameStatisticsDetailsTopBarUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		scrollBehavior = scrollBehavior,
		title = {
			Text(
				text = stringResource(R.string.statistics_details_title),
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = {
			CloseButton(onClick = { onAction(MidGameStatisticsDetailsTopBarUiAction.BackClicked) })
		},
	)
}
