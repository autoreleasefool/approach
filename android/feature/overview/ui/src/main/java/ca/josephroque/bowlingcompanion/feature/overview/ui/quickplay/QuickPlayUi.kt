package ca.josephroque.bowlingcompanion.feature.overview.ui.quickplay

import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.model.Series

data class QuickPlayUiState(
	val bowlers: List<Pair<BowlerSummary, LeagueSummary>> = emptyList(),
	val numberOfGames: Int = Series.DEFAULT_NUMBER_OF_GAMES,
	val isShowingQuickPlayTip: Boolean = false,
)

sealed interface QuickPlayUiAction {
	data object BackClicked : QuickPlayUiAction
	data object StartClicked : QuickPlayUiAction
	data object AddBowlerClicked : QuickPlayUiAction
	data object TipClicked : QuickPlayUiAction

	data class NumberOfGamesChanged(val numberOfGames: Int) : QuickPlayUiAction
	data class BowlerClicked(val bowler: BowlerSummary) : QuickPlayUiAction
	data class BowlerDeleted(val bowler: BowlerSummary) : QuickPlayUiAction
	data class BowlerMoved(val from: Int, val to: Int) : QuickPlayUiAction
}
