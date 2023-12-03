package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsWidgetEditorTopBar(
	isSaveEnabled: Boolean,
	onAction: (StatisticsWidgetEditorUiAction) -> Unit = {},
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		title = {
			Text(text = stringResource(R.string.statistics_widget_editor_title))
		},
		navigationIcon = {
			BackButton(onClick = { onAction(StatisticsWidgetEditorUiAction.BackClicked) })
		},
		actions = {
			Text(
				text = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_save),
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier
					.clickable(onClick = { onAction(StatisticsWidgetEditorUiAction.SaveClicked) }, enabled = isSaveEnabled)
					.padding(16.dp),
			)
		},
		scrollBehavior = scrollBehavior,
	)
}