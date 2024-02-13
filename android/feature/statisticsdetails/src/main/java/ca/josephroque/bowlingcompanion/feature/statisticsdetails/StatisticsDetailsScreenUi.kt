package ca.josephroque.bowlingcompanion.feature.statisticsdetails

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart.StatisticsDetailsChartUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart.StatisticsDetailsChartUiState
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiState

sealed interface StatisticsDetailsScreenUiState {
	data object Loading: StatisticsDetailsScreenUiState

	data class Loaded @OptIn(ExperimentalMaterial3Api::class) constructor(
		val headerPeekHeight: Float = 0f,
		val bottomSheetValue: SheetValue = SheetValue.PartiallyExpanded,
		val list: StatisticsDetailsListUiState,
		val chart: StatisticsDetailsChartUiState,
	): StatisticsDetailsScreenUiState
}

sealed interface StatisticsDetailsBottomSheetUiAction {
	data class SheetValueChanged @OptIn(ExperimentalMaterial3Api::class) constructor(val value: SheetValue): StatisticsDetailsBottomSheetUiAction
}

sealed interface StatisticsDetailsScreenUiAction {
	data class Chart(val action: StatisticsDetailsChartUiAction): StatisticsDetailsScreenUiAction
	data class List(val action: StatisticsDetailsListUiAction): StatisticsDetailsScreenUiAction
	data class TopBar(val action: StatisticsDetailsTopBarUiAction): StatisticsDetailsScreenUiAction
	data class BottomSheet(val action: StatisticsDetailsBottomSheetUiAction): StatisticsDetailsScreenUiAction
}

sealed interface StatisticsDetailsScreenEvent {
	data object Dismissed: StatisticsDetailsScreenEvent
}