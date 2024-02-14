package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.editor

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.animation.Wiggle
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidget
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetSource
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetTimeline
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.R
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.widget.StatisticsWidgetCard
import java.util.UUID
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
		modifier = modifier
			.reorderable(reorderableState)
			.detectReorderAfterLongPress(reorderableState)
			.padding(horizontal = 8.dp),
	) {
		items(
			state.widgets,
			key = { it.id },
		) { widget ->
			ReorderableItem(
				reorderableState = reorderableState,
				key = widget.id,
			) { isDragging ->
				val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp, label = "elevation")

				Wiggle { modifier ->
					Box(
						contentAlignment = Alignment.TopEnd,
						modifier = modifier,
					) {
						StatisticsWidgetCard(
							widget = widget,
							onClick = { onAction(StatisticsWidgetLayoutEditorUiAction.WidgetClicked(widget)) },
							modifier = Modifier
								.aspectRatio(1f)
								.padding(8.dp)
								.shadow(elevation.value),
						)

						if (state.isDeleteModeEnabled) {
							IconButton(
								onClick = { onAction(StatisticsWidgetLayoutEditorUiAction.WidgetClicked(widget)) },
								colors = IconButtonDefaults.filledIconButtonColors(
									contentColor = colorResource(
										ca.josephroque.bowlingcompanion.core.designsystem.R.color.white,
									),
									containerColor = colorResource(
										ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive,
									),
								),
							) {
								Icon(
									Icons.Default.Delete,
									contentDescription = stringResource(R.string.cd_delete_widget),
								)
							}
						}
					}
				}
			}
		}

		item(span = { GridItemSpan(2) }) {
			Text(
				text = stringResource(
					if (state.isDeleteModeEnabled) {
						R.string.statistics_widget_layout_editor_tap_to_delete_widget
					} else {
						R.string.statistics_widget_layout_editor_tap_and_hold_to_reorder
					},
				),
				style = MaterialTheme.typography.bodySmall,
				textAlign = TextAlign.Center,
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 8.dp),
			)
		}
	}
}

@Preview
@Composable
private fun StatisticsWidgetLayoutEditorPreview() {
	val state = remember {
		mutableStateOf(
			StatisticsWidgetLayoutEditorUiState(
				widgets = listOf(
					StatisticsWidget(
						source = StatisticsWidgetSource.Bowler(UUID.randomUUID()),
						id = UUID.randomUUID(),
						timeline = StatisticsWidgetTimeline.THREE_MONTHS,
						statistic = StatisticID.ACES,
						context = "",
						priority = 0,
					),
					StatisticsWidget(
						source = StatisticsWidgetSource.Bowler(UUID.randomUUID()),
						id = UUID.randomUUID(),
						timeline = StatisticsWidgetTimeline.THREE_MONTHS,
						statistic = StatisticID.ACES,
						context = "",
						priority = 1,
					),
				),
			),
		)
	}

	Surface {
		StatisticsWidgetLayoutEditor(
			state = state.value,
			onAction = {},
		)
	}
}
