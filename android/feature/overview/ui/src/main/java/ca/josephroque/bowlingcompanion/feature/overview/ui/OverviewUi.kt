package ca.josephroque.bowlingcompanion.feature.overview.ui

import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersListUiAction
import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersListUiState
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayoutUiAction
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayoutUiState

data class OverviewUiState(
	val widgets: StatisticsWidgetLayoutUiState?,
	val bowlersList: BowlersListUiState,
)

sealed interface OverviewUiAction {
	data object AddBowlerClicked: OverviewUiAction
	data object EditStatisticsWidgetClicked: OverviewUiAction

	data class BowlersListAction(val action: BowlersListUiAction): OverviewUiAction
	data class StatisticsWidgetLayout(val action: StatisticsWidgetLayoutUiAction): OverviewUiAction
}