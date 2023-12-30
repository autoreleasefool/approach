package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.editor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsWidgetLayoutEditorTopBar(
	state: StatisticsWidgetLayoutEditorTopBarUiState,
	onAction: (StatisticsWidgetLayoutEditorUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		title = {
			Text(text = stringResource(R.string.statistics_widget_layout_editor_title))
		},
		navigationIcon = {
			BackButton(onClick = { onAction(StatisticsWidgetLayoutEditorUiAction.BackClicked) })
		},
		actions = {
			IconButton(onClick = { onAction(StatisticsWidgetLayoutEditorUiAction.ToggleDeleteMode(!state.isDeleteModeEnabled)) }) {
				Icon(
					Icons.Default.Delete,
					contentDescription = stringResource(R.string.cd_delete_widgets),
				)
			}

			IconButton(onClick = { onAction(StatisticsWidgetLayoutEditorUiAction.AddWidgetClicked)} ) {
				Icon(
					Icons.Default.Add,
					contentDescription = stringResource(R.string.cd_add_widget),
				)
			}
		},
		scrollBehavior = scrollBehavior,
	)
}