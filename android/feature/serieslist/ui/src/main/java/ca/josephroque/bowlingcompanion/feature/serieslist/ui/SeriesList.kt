package ca.josephroque.bowlingcompanion.feature.serieslist.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import ca.josephroque.bowlingcompanion.core.common.utils.simpleFormat
import ca.josephroque.bowlingcompanion.core.designsystem.components.ArchiveDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.HeaderAction
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.header
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.DefaultEmptyState
import ca.josephroque.bowlingcompanion.core.model.SeriesItemSize
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun SeriesList(
	state: SeriesListUiState,
	onAction: (SeriesListUiAction) -> Unit,
	modifier: Modifier = Modifier,
	header: (@Composable LazyItemScope.() -> Unit)? = null,
) {
	state.seriesToArchive?.let {
		ArchiveDialog(
			itemName = it.date.simpleFormat(),
			onArchive = { onAction(SeriesListUiAction.ConfirmArchiveClicked) },
			onDismiss = { onAction(SeriesListUiAction.DismissArchiveClicked) },
		)
	}

	LazyColumn(modifier = modifier) {
		if (state.isEmpty) {
			item {
				DefaultEmptyState(
					title = R.string.series_list_empty_title,
					icon = R.drawable.series_list_empty_state,
					message = R.string.series_list_empty_message,
					action = R.string.series_list_add,
					onActionClick = { onAction(SeriesListUiAction.AddSeriesClicked) },
				)
			}
		} else {
			header?.also {
				item {
					it()
				}
			}

			seriesList(
				preBowlSeries = state.preBowlSeries,
				regularSeries = state.regularSeries,
				itemSize = state.itemSize,
				onUsePreBowlClick = { onAction(SeriesListUiAction.UsePreBowlClicked) },
				onSeriesClick = { onAction(SeriesListUiAction.SeriesClicked(it.id)) },
				onArchiveSeries = { onAction(SeriesListUiAction.ArchiveSeriesClicked(it)) },
				onEditSeries = { onAction(SeriesListUiAction.EditSeriesClicked(it.id)) },
			)
		}
	}
}

fun LazyListScope.seriesList(
	preBowlSeries: List<SeriesListChartItem>,
	regularSeries: List<SeriesListChartItem>,
	itemSize: SeriesItemSize,
	onUsePreBowlClick: () -> Unit,
	onSeriesClick: (SeriesListChartItem) -> Unit,
	onArchiveSeries: (SeriesListChartItem) -> Unit,
	onEditSeries: (SeriesListChartItem) -> Unit,
) {
	if (preBowlSeries.isNotEmpty()) {
		header(
			R.string.series_list_pre_bowl_title,
			action = HeaderAction(
				actionResourceId = R.string.series_list_use_pre_bowl,
				onClick = onUsePreBowlClick,
			),
		)

		itemsIndexed(
			items = preBowlSeries,
			key = { _, series -> series.id },
		) { index, series ->
			SeriesRow(
				series = series,
				itemSize = itemSize,
				onClick = { onSeriesClick(series) },
				onArchive = { onArchiveSeries(series) },
				onEdit = { onEditSeries(series) },
			)

			if (index < preBowlSeries.size - 1) {
				HorizontalDivider()
			}
		}
	}

	if (regularSeries.isNotEmpty()) {
		if (preBowlSeries.isNotEmpty()) {
			item {
				HorizontalDivider()
			}

			header(R.string.series_list_regular_title)
		}

		itemsIndexed(
			items = regularSeries,
			key = { _, series -> series.id },
		) { index, series ->
			SeriesRow(
				series = series,
				itemSize = itemSize,
				onClick = { onSeriesClick(series) },
				onArchive = { onArchiveSeries(series) },
				onEdit = { onEditSeries(series) },
			)

			if (index < regularSeries.size - 1) {
				HorizontalDivider()
			}
		}
	}
}

@Composable
private fun seriesActions(
	onArchiveSeries: () -> Unit,
	onEditSeries: () -> Unit,
): Pair<List<SwipeAction>, List<SwipeAction>> {
	val archiveAction = SwipeAction(
		icon = painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_archive),
		background = colorResource(
			ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive,
		),
		onSwipe = onArchiveSeries,
	)

	val editAction = SwipeAction(
		icon = rememberVectorPainter(Icons.Default.Edit),
		background = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.blue_300),
		onSwipe = onEditSeries,
	)

	return listOf(archiveAction) to listOf(editAction)
}

@Composable
private fun SeriesRow(
	series: SeriesListChartItem,
	itemSize: SeriesItemSize,
	onArchive: () -> Unit,
	onEdit: () -> Unit,
	onClick: () -> Unit,
) {
	val (startActions, endActions) = seriesActions(
		onArchiveSeries = onArchive,
		onEditSeries = onEdit,
	)

	SwipeableActionsBox(
		startActions = startActions,
		endActions = endActions,
	) {
		SeriesRow(
			date = series.date,
			preBowledForDate = series.appliedDate,
			total = series.total,
			scores = series.scores?.let {
				ScoreData(
					seriesLow = series.lowestScore,
					seriesHigh = series.highestScore,
					numberOfGames = series.numberOfGames,
					model = it,
				)
			},
			onClick = onClick,
			itemSize = itemSize,
		)
	}
}
