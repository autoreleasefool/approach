package ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.sourcepicker

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.components.CloseButton
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourcePickerTopBar(
	onAction: (SourcePickerUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		scrollBehavior = scrollBehavior,
		title = {
			Text(text = stringResource(R.string.statistics_filter))
		},
		navigationIcon = {
			CloseButton(onClick = { onAction(SourcePickerUiAction.Dismissed) })
		},
		actions = {
			TextButton(onClick = { onAction(SourcePickerUiAction.ApplyFilterClicked) }) {
				Text(
					text = stringResource(R.string.statistics_filter_apply),
					style = MaterialTheme.typography.bodyMedium,
				)
			}
		},
	)
}
