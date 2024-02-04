package ca.josephroque.bowlingcompanion.feature.overview

import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewUiAction
import ca.josephroque.bowlingcompanion.feature.overview.ui.OverviewUiState
import java.util.UUID

sealed interface OverviewScreenUiState {
	data object Loading: OverviewScreenUiState

	data class Loaded(
		val overview: OverviewUiState,
	): OverviewScreenUiState
}

sealed interface OverviewScreenUiAction {
	data object DidAppear: OverviewScreenUiAction
	data class OverviewAction(val action: OverviewUiAction): OverviewScreenUiAction
}

sealed interface OverviewScreenEvent {
	data object AddBowler: OverviewScreenEvent
	data object ShowQuickPlay: OverviewScreenEvent

	data class EditStatisticsWidget(val context: String): OverviewScreenEvent
	data class EditBowler(val id: UUID): OverviewScreenEvent
	data class ShowBowlerDetails(val id: UUID): OverviewScreenEvent
	data class ShowStatistics(val widget: UUID): OverviewScreenEvent
}