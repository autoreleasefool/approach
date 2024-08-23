package ca.josephroque.bowlingcompanion.feature.overview.ui

import ca.josephroque.bowlingcompanion.core.model.BowlerSortOrder
import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersListUiAction
import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersListUiState
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayoutUiAction
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayoutUiState

data class OverviewUiState(
	val widgets: StatisticsWidgetLayoutUiState?,
	val bowlersList: BowlersListUiState,
	val isShowingSwipeHint: Boolean,
)

data class OverviewTopBarUiState(
	val isSortOrderMenuVisible: Boolean = false,
	val isSortOrderMenuExpanded: Boolean = false,
	val sortOrder: BowlerSortOrder = BowlerSortOrder.MOST_RECENTLY_USED,
)

sealed interface OverviewUiAction {
	data object AddBowlerClicked : OverviewUiAction
	data object QuickPlayClicked : OverviewUiAction
	data object EditStatisticsWidgetClicked : OverviewUiAction
	data object SwipeHintDismissed : OverviewUiAction

	data object BowlersSortClicked : OverviewUiAction
	data object BowlersSortDismissed : OverviewUiAction
	data class BowlersSortOrderClicked(val sortOrder: BowlerSortOrder) : OverviewUiAction

	data class BowlersListAction(val action: BowlersListUiAction) : OverviewUiAction
	data class StatisticsWidgetLayout(val action: StatisticsWidgetLayoutUiAction) : OverviewUiAction
}
