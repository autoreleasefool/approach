package ca.josephroque.bowlingcompanion.feature.overview.ui

import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersListUiAction
import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersListUiState
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayoutUiAction
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayoutUiState

data class OverviewUiState(
	val widgets: StatisticsWidgetLayoutUiState?,
	val bowlersList: BowlersListUiState,
	val isShowingSwipeHint: Boolean,
)

sealed interface OverviewUiAction {
	data object AddBowlerClicked : OverviewUiAction
	data object QuickPlayClicked : OverviewUiAction
	data object EditStatisticsWidgetClicked : OverviewUiAction
	data object SwipeHintDismissed : OverviewUiAction

	data class BowlersListAction(val action: BowlersListUiAction) : OverviewUiAction
	data class StatisticsWidgetLayout(val action: StatisticsWidgetLayoutUiAction) : OverviewUiAction
}
