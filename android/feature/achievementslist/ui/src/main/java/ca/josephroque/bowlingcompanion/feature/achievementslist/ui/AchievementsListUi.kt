package ca.josephroque.bowlingcompanion.feature.achievementslist.ui

import ca.josephroque.bowlingcompanion.core.model.AchievementListItem

data class AchievementsListUiState(
	val achievements: List<AchievementListItem>,
	val selectedAchievement: AchievementListItem? = null,
)

sealed interface AchievementsListTopBarUiAction {
	data object BackClicked : AchievementsListTopBarUiAction
}

sealed interface AchievementsListUiAction {
	data class AchievementClicked(val achievement: AchievementListItem) : AchievementsListUiAction
}
