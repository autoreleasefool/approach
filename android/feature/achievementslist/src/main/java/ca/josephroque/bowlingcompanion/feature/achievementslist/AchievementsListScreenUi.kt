package ca.josephroque.bowlingcompanion.feature.achievementslist

import ca.josephroque.bowlingcompanion.feature.achievementslist.ui.AchievementsListTopBarUiAction
import ca.josephroque.bowlingcompanion.feature.achievementslist.ui.AchievementsListUiAction
import ca.josephroque.bowlingcompanion.feature.achievementslist.ui.AchievementsListUiState

sealed interface AchievementsListScreenUiState {
	data object Loading : AchievementsListScreenUiState

	data class Loaded(
		val list: AchievementsListUiState,
	) : AchievementsListScreenUiState
}

sealed interface AchievementsListScreenUiAction {
	data class TopBar(val action: AchievementsListTopBarUiAction) : AchievementsListScreenUiAction
	data class List(val action: AchievementsListUiAction) : AchievementsListScreenUiAction
}

sealed interface AchievementsListScreenEvent {
	data object Dismissed : AchievementsListScreenEvent
}
