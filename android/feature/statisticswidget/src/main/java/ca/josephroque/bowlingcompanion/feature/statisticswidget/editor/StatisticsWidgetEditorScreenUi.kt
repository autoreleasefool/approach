package ca.josephroque.bowlingcompanion.feature.statisticswidget.editor

import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.statistics.Statistic
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.editor.StatisticsWidgetEditorUiAction
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.editor.StatisticsWidgetEditorUiState
import java.util.UUID

sealed interface StatisticsWidgetInitialSource {
	data class Bowler(val bowlerId: BowlerID) : StatisticsWidgetInitialSource
}

sealed interface StatisticsWidgetEditorScreenUiState {
	data object Loading : StatisticsWidgetEditorScreenUiState

	data class Loaded(val statisticsWidgetEditor: StatisticsWidgetEditorUiState) :
		StatisticsWidgetEditorScreenUiState
}

sealed interface StatisticsWidgetEditorScreenUiAction {
	data class StatisticsWidgetEditor(val action: StatisticsWidgetEditorUiAction) :
		StatisticsWidgetEditorScreenUiAction

	data class UpdatedBowler(val bowler: BowlerID?) : StatisticsWidgetEditorScreenUiAction
	data class UpdatedLeague(val league: UUID?) : StatisticsWidgetEditorScreenUiAction
	data class UpdatedStatistic(val statistic: StatisticID) : StatisticsWidgetEditorScreenUiAction
}

sealed interface StatisticsWidgetEditorScreenEvent {
	data object Dismissed : StatisticsWidgetEditorScreenEvent

	data class EditStatistic(val statistic: Statistic) : StatisticsWidgetEditorScreenEvent
	data class EditBowler(val bowlerId: BowlerID?) : StatisticsWidgetEditorScreenEvent
	data class EditLeague(val bowlerId: BowlerID, val leagueId: UUID?) :
		StatisticsWidgetEditorScreenEvent
}
