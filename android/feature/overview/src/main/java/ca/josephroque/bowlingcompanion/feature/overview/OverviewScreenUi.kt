package ca.josephroque.bowlingcompanion.feature.overview

import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewTopBarUiState
import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewUiAction
import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewUiState

sealed interface OverviewScreenUiState {
	data object Loading : OverviewScreenUiState

	data class Loaded(
		val overview: OverviewUiState,
		val topBar: OverviewTopBarUiState,
		val isGameInProgressSnackBarVisible: Boolean,
	) : OverviewScreenUiState
}

sealed interface OverviewScreenUiAction {
	data object DidAppear : OverviewScreenUiAction

	data object GameInProgressSnackBarDismissed : OverviewScreenUiAction
	data object ResumeGameInProgressClicked : OverviewScreenUiAction

	data class OverviewAction(val action: OverviewUiAction) : OverviewScreenUiAction
}

sealed interface OverviewScreenEvent {
	data object AddBowler : OverviewScreenEvent
	data object ShowQuickPlay : OverviewScreenEvent

	data class EditStatisticsWidget(val context: String) : OverviewScreenEvent
	data class EditBowler(val id: BowlerID) : OverviewScreenEvent
	data class ResumeGame(val seriesIds: List<SeriesID>, val currentGameId: GameID) :
		OverviewScreenEvent
	data class ShowBowlerDetails(val id: BowlerID) : OverviewScreenEvent
	data class ShowWidgetStatistics(val filter: TrackableFilter) : OverviewScreenEvent
	data object ShowWidgetNotEnoughDataError : OverviewScreenEvent
	data object ShowWidgetUnavailableError : OverviewScreenEvent
}
