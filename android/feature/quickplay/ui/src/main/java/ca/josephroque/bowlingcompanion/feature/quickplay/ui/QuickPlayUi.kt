package ca.josephroque.bowlingcompanion.feature.quickplay.ui

import androidx.annotation.StringRes
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.model.Series

data class QuickPlayTopBarUiState(
	@StringRes val title: Int = R.string.quick_play,
	val isAddBowlerEnabled: Boolean = false,
)

sealed interface QuickPlayTopBarUiAction {
	data object BackClicked : QuickPlayTopBarUiAction
	data object AddBowlerClicked : QuickPlayTopBarUiAction
}

data class QuickPlayUiState(
	val bowlers: List<Pair<BowlerSummary, LeagueSummary?>> = emptyList(),
	val numberOfGames: Int = Series.DEFAULT_NUMBER_OF_GAMES,
	val isStartButtonEnabled: Boolean = false,
	val isShowingQuickPlayTip: Boolean = false,
	val isDeleteBowlersEnabled: Boolean = false,
)

sealed interface QuickPlayUiAction {
	data object StartClicked : QuickPlayUiAction
	data object TipClicked : QuickPlayUiAction

	data class NumberOfGamesChanged(val numberOfGames: Int) : QuickPlayUiAction
	data class BowlerClicked(val bowler: BowlerSummary) : QuickPlayUiAction
	data class BowlerDeleted(val bowler: BowlerSummary) : QuickPlayUiAction
	data class BowlerMoved(val from: Int, val to: Int) : QuickPlayUiAction
}
