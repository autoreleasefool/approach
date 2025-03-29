package ca.josephroque.bowlingcompanion.feature.achievementslist

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.AchievementsRepository
import ca.josephroque.bowlingcompanion.core.model.AchievementID
import ca.josephroque.bowlingcompanion.core.model.AchievementListItem
import ca.josephroque.bowlingcompanion.feature.achievementslist.ui.AchievementsListTopBarUiAction
import ca.josephroque.bowlingcompanion.feature.achievementslist.ui.AchievementsListUiAction
import ca.josephroque.bowlingcompanion.feature.achievementslist.ui.AchievementsListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AchievementsListViewModel @Inject constructor(
	private val achievementsRepository: AchievementsRepository,
) : ApproachViewModel<AchievementsListScreenEvent>() {

	val uiState = achievementsRepository
		.getEarnedAchievements()
		.map {
			AchievementsListScreenUiState.Loaded(
				list = AchievementsListUiState(
					achievements = it,
				),
			)
		}
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = AchievementsListScreenUiState.Loading,
		)

	fun handleAction(action: AchievementsListScreenUiAction) {
		when (action) {
			is AchievementsListScreenUiAction.List -> handleAchievementsListAction(action.action)
			is AchievementsListScreenUiAction.TopBar -> handleTopBarAction(action.action)
		}
	}

	private fun handleTopBarAction(action: AchievementsListTopBarUiAction) {
		when (action) {
			is AchievementsListTopBarUiAction.BackClicked -> sendEvent(AchievementsListScreenEvent.Dismissed)
		}
	}

	private fun handleAchievementsListAction(action: AchievementsListUiAction) {
		when (action) {
			is AchievementsListUiAction.AchievementClicked -> showAchievementDetails(action.achievement)
		}
	}

	private fun showAchievementDetails(achievement: AchievementListItem) {
		TODO("Show achievement details")
	}
}