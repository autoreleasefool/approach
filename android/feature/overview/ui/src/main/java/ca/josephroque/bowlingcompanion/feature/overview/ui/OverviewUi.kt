package ca.josephroque.bowlingcompanion.feature.overview.ui

import ca.josephroque.bowlingcompanion.core.model.BowlerSortOrder
import ca.josephroque.bowlingcompanion.core.model.TeamSortOrder
import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersListUiAction
import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.BowlersListUiState
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayoutUiAction
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayoutUiState
import ca.josephroque.bowlingcompanion.feature.teamslist.ui.TeamsListUiAction
import ca.josephroque.bowlingcompanion.feature.teamslist.ui.TeamsListUiState

enum class OverviewTab {
	BOWLERS,
	TEAMS,
}

data class OverviewUiState(
	val isTeamsEnabled: Boolean,
	val tab: OverviewTab,
	val widgets: StatisticsWidgetLayoutUiState?,
	val teamsList: TeamsListUiState,
	val bowlersList: BowlersListUiState,
	val isShowingSwipeHint: Boolean,
)

sealed interface OverviewTopBarUiState {
	data class BowlerTab(
		val isSortOrderMenuVisible: Boolean = false,
		val isSortOrderMenuExpanded: Boolean = false,
		val sortOrder: BowlerSortOrder = BowlerSortOrder.MOST_RECENTLY_USED,
	) : OverviewTopBarUiState

	data class TeamTab(
		val isSortOrderMenuVisible: Boolean = false,
		val isSortOrderMenuExpanded: Boolean = false,
		val sortOrder: TeamSortOrder = TeamSortOrder.MOST_RECENTLY_USED,
	) : OverviewTopBarUiState
}

sealed interface OverviewUiAction {
	data object AddBowlerClicked : OverviewUiAction
	data object AddTeamClicked : OverviewUiAction
	data object QuickPlayClicked : OverviewUiAction
	data object EditStatisticsWidgetClicked : OverviewUiAction
	data object SwipeHintDismissed : OverviewUiAction

	data class TabClicked(val tab: OverviewTab) : OverviewUiAction

	data object BowlersSortClicked : OverviewUiAction
	data object BowlersSortDismissed : OverviewUiAction
	data class BowlersSortOrderClicked(val sortOrder: BowlerSortOrder) : OverviewUiAction

	data object TeamsSortClicked : OverviewUiAction
	data object TeamsSortDismissed : OverviewUiAction
	data class TeamsSortOrderClicked(val sortOrder: TeamSortOrder) : OverviewUiAction

	data class BowlersListAction(val action: BowlersListUiAction) : OverviewUiAction
	data class TeamsListAction(val action: TeamsListUiAction) : OverviewUiAction
	data class StatisticsWidgetLayout(val action: StatisticsWidgetLayoutUiAction) : OverviewUiAction
}
