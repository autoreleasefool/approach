package ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui

import ca.josephroque.bowlingcompanion.core.model.BowlerDetails
import ca.josephroque.bowlingcompanion.core.model.GearID
import ca.josephroque.bowlingcompanion.core.model.LeagueSortOrder
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListUiState
import ca.josephroque.bowlingcompanion.feature.leagueslist.ui.LeaguesListUiAction
import ca.josephroque.bowlingcompanion.feature.leagueslist.ui.LeaguesListUiState
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayoutUiAction
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayoutUiState

data class BowlerDetailsUiState(
	val bowler: BowlerDetails,
	val widgets: StatisticsWidgetLayoutUiState?,
	val leaguesList: LeaguesListUiState,
	val gearList: GearListUiState,
	val topBar: BowlerDetailsTopBarUiState,
)

data class BowlerDetailsTopBarUiState(
	val bowlerName: String = "",
	val isSortOrderMenuVisible: Boolean = false,
	val isSortOrderMenuExpanded: Boolean = false,
	val sortOrder: LeagueSortOrder = LeagueSortOrder.MOST_RECENTLY_USED,
)

sealed interface BowlerDetailsUiAction {
	data object BackClicked : BowlerDetailsUiAction
	data object AddLeagueClicked : BowlerDetailsUiAction
	data object EditStatisticsWidgetClicked : BowlerDetailsUiAction
	data object ManageGearClicked : BowlerDetailsUiAction

	data object SortClicked : BowlerDetailsUiAction
	data object SortDismissed : BowlerDetailsUiAction
	data class SortOrderClicked(val sortOrder: LeagueSortOrder) : BowlerDetailsUiAction

	data class LeaguesListAction(val action: LeaguesListUiAction) : BowlerDetailsUiAction
	data class StatisticsWidgetLayout(val action: StatisticsWidgetLayoutUiAction) :
		BowlerDetailsUiAction
	data class GearClicked(val id: GearID) : BowlerDetailsUiAction
}
