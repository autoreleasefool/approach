package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.widget.StatisticsWidgetCard
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyGridState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun StatisticsWidgetLayoutEditor(
	state: StatisticsWidgetLayoutEditorUiState,
	onAction: (StatisticsWidgetLayoutEditorUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val reorderableState = rememberReorderableLazyGridState(
		onMove = { from, to ->
			onAction(StatisticsWidgetLayoutEditorUiAction.WidgetMoved(from.index, to.index))
		},
	)

	LazyVerticalGrid(
		columns = GridCells.Fixed(2),
		state = reorderableState.gridState,
		modifier = modifier.reorderable(reorderableState),
	) {
		items(
			state.widgets,
		) { widget ->
			ReorderableItem(
				reorderableState = reorderableState,
				key = widget.id,
			) {
				StatisticsWidgetCard(
					widget = widget,
					onClick = { onAction(StatisticsWidgetLayoutEditorUiAction.WidgetClicked(widget)) },
					modifier = Modifier
						.aspectRatio(1f)
						.detectReorderAfterLongPress(reorderableState),
				)
			}
		}

		item {
			Text(
				text = "Add",
				modifier = Modifier.clickable { onAction(StatisticsWidgetLayoutEditorUiAction.AddWidgetClicked) },
			)
		}
	}
}