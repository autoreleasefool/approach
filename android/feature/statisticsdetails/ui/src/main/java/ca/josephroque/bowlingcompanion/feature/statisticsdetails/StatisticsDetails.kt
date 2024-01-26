package ca.josephroque.bowlingcompanion.feature.statisticsdetails

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart.StatisticsDetailsChart
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsList
import com.skydoves.flexible.bottomsheet.material3.FlexibleBottomSheet
import com.skydoves.flexible.core.FlexibleSheetSize
import com.skydoves.flexible.core.FlexibleSheetValue
import com.skydoves.flexible.core.rememberFlexibleBottomSheetState
import kotlinx.coroutines.launch

@Composable
fun StatisticsDetails(
	state: StatisticsDetailsUiState,
	onAction: (StatisticsDetailsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val coroutineScope = rememberCoroutineScope()
	val chartHeight = remember { Animatable(1f) }
	val sheetSize = FlexibleSheetSize()
	val sheetState = rememberFlexibleBottomSheetState(
		skipSlightlyExpanded = false,
		skipIntermediatelyExpanded = true,
		allowNestedScroll = false,
		flexibleSheetSize = sheetSize,
		confirmValueChange = { it != FlexibleSheetValue.Hidden },
	)
	val listState = rememberLazyListState()

	LaunchedEffect(state.nextSheetSize) {
		if (state.nextSheetSize == sheetState.currentValue) return@LaunchedEffect
		when (state.nextSheetSize) {
			FlexibleSheetValue.Hidden -> Unit
			FlexibleSheetValue.SlightlyExpanded -> sheetState.slightlyExpand()
			FlexibleSheetValue.IntermediatelyExpanded -> sheetState.intermediatelyExpand()
			FlexibleSheetValue.FullyExpanded -> sheetState.fullyExpand()
		}

		val highlightedEntry = state.list.highlightedEntry
		val highlightedGroup = state.list.statistics.indexOfFirst {
			group -> group.entries.firstOrNull { it.id == highlightedEntry } != null
		}
		if (highlightedGroup >= 0) {
			// FIXME: Measure and add offset to item in group
			listState.animateScrollToItem(index = highlightedGroup)
		}
	}

	FlexibleBottomSheet(
		onDismissRequest = {
			coroutineScope.launch { sheetState.slightlyExpand() }
		},
		onTargetChanges = {
			onAction(StatisticsDetailsUiAction.NextSheetSize(it))
			coroutineScope.launch {
				when (it) {
					FlexibleSheetValue.Hidden -> Unit
					FlexibleSheetValue.SlightlyExpanded -> chartHeight.animateTo(1f - sheetSize.slightlyExpanded)
					FlexibleSheetValue.IntermediatelyExpanded -> chartHeight.animateTo(1f - sheetSize.intermediatelyExpanded)
					FlexibleSheetValue.FullyExpanded -> chartHeight.animateTo(1f - sheetSize.fullyExpanded)
				}
			}
		},
		dragHandle = {
			Surface(
				modifier = Modifier
					.padding(vertical = 12.dp)
					.semantics { contentDescription = "DragHandle" },
				shape = MaterialTheme.shapes.extraLarge,
				color = MaterialTheme.colorScheme.surfaceVariant,
			) {
				Box(
					Modifier
						.size(
							width = 32.dp,
							height = 4.dp,
						)
				)
			}
		},
		sheetState = sheetState,
	) {
		StatisticsDetailsList(
			state = state.list,
			listState = listState,
			onAction = { onAction(StatisticsDetailsUiAction.StatisticsDetailsList(it)) },
		)
	}

	StatisticsDetailsChart(
		state = state.chart,
		onAction = { onAction(StatisticsDetailsUiAction.StatisticsDetailsChart(it)) },
		modifier = modifier.fillMaxHeight(chartHeight.value),
	)
}