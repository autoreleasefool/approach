package ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui

import ca.josephroque.bowlingcompanion.core.model.BowlerDetails
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListUiState
import ca.josephroque.bowlingcompanion.feature.leagueslist.ui.LeaguesListUiAction
import ca.josephroque.bowlingcompanion.feature.leagueslist.ui.LeaguesListUiState
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayoutUiAction
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayoutUiState
import java.util.UUID

data class BowlerDetailsUiState(
	val bowler: BowlerDetails,
	val widgets: StatisticsWidgetLayoutUiState?,
	val leaguesList: LeaguesListUiState,
	val gearList: GearListUiState,
	val topBar: BowlerDetailsTopBarUiState,
)

data class BowlerDetailsTopBarUiState(
	val bowlerName: String = "",
)

sealed interface BowlerDetailsUiAction {
	data object BackClicked: BowlerDetailsUiAction
	data object AddLeagueClicked: BowlerDetailsUiAction
	data object EditStatisticsWidgetClicked: BowlerDetailsUiAction
	data object ManageGearClicked: BowlerDetailsUiAction

	data class LeaguesListAction(val action: LeaguesListUiAction): BowlerDetailsUiAction
	data class StatisticsWidgetLayout(val action: StatisticsWidgetLayoutUiAction): BowlerDetailsUiAction
	data class GearClicked(val id: UUID): BowlerDetailsUiAction
}