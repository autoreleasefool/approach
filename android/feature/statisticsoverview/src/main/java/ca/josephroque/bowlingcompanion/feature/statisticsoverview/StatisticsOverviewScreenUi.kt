package ca.josephroque.bowlingcompanion.feature.statisticsoverview

import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.StatisticsOverviewUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.StatisticsOverviewUiState
import java.util.UUID

sealed interface StatisticsOverviewScreenUiState {
	data object Loading: StatisticsOverviewScreenUiState

	data class Loaded(
		val statisticsOverview: StatisticsOverviewUiState = StatisticsOverviewUiState(),
	): StatisticsOverviewScreenUiState
}

sealed interface StatisticsOverviewScreenUiAction {
	data class UpdatedBowler(val bowler: UUID?): StatisticsOverviewScreenUiAction
	data class UpdatedLeague(val league: UUID?): StatisticsOverviewScreenUiAction
	data class UpdatedSeries(val series: UUID?): StatisticsOverviewScreenUiAction
	data class UpdatedGame(val game: UUID?): StatisticsOverviewScreenUiAction
	data object FinishedNavigation: StatisticsOverviewScreenUiAction

	data class StatisticsOverviewAction(
		val action: StatisticsOverviewUiAction,
	): StatisticsOverviewScreenUiAction
}

sealed interface StatisticsOverviewScreenEvent {
	data class ShowStatistics(val filter: TrackableFilter): StatisticsOverviewScreenEvent

	data class EditBowler(val bowler: UUID?): StatisticsOverviewScreenEvent
	data class EditLeague(val bowler: UUID, val league: UUID?): StatisticsOverviewScreenEvent
	data class EditSeries(val series: UUID?): StatisticsOverviewScreenEvent
	data class EditGame(val game: UUID?): StatisticsOverviewScreenEvent
}