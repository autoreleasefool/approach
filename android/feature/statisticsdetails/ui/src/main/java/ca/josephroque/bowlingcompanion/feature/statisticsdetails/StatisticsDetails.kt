package ca.josephroque.bowlingcompanion.feature.statisticsdetails

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart.StatisticsDetailsChart
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsList
import com.skydoves.flexible.bottomsheet.material.FlexibleBottomSheet
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
		skipIntermediatelyExpanded = false,
		allowNestedScroll = false,
		flexibleSheetSize = sheetSize,
		confirmValueChange = { it != FlexibleSheetValue.Hidden },
	)

	FlexibleBottomSheet(
		onDismissRequest = {
			coroutineScope.launch { sheetState.slightlyExpand() }
		},
		onTargetChanges = {
			coroutineScope.launch {
				when (it) {
					FlexibleSheetValue.Hidden -> Unit
					FlexibleSheetValue.SlightlyExpanded -> chartHeight.animateTo(1f - sheetSize.slightlyExpanded)
					FlexibleSheetValue.IntermediatelyExpanded -> chartHeight.animateTo(1f - sheetSize.intermediatelyExpanded)
					FlexibleSheetValue.FullyExpanded -> chartHeight.animateTo(1f - sheetSize.fullyExpanded)
				}
			}
		},
		sheetState = sheetState,
	) {
		StatisticsDetailsList(
			state = state.list,
			onAction = { onAction(StatisticsDetailsUiAction.StatisticsDetailsList(it)) },
		)
	}

	StatisticsDetailsChart(
		state = state.chart,
		onAction = { onAction(StatisticsDetailsUiAction.StatisticsDetailsChart(it)) },
		modifier = modifier.fillMaxHeight(chartHeight.value),
	)
}