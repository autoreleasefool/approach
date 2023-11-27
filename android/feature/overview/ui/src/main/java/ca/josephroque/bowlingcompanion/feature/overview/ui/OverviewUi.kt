package ca.josephroque.bowlingcompanion.feature.overview.ui

import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersListUiAction
import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersListUiState

data class OverviewUiState(
	val bowlersList: BowlersListUiState,
)

sealed interface OverviewUiAction {
	data object AddBowlerClicked: OverviewUiAction
	data object EditStatisticsWidgetClicked: OverviewUiAction

	data class BowlersListAction(val action: BowlersListUiAction): OverviewUiAction
}