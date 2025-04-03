package ca.josephroque.bowlingcompanion.feature.announcements

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.achievements.earnable.TenYearsAchievement
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.AchievementsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.feature.announcements.provider.AnnouncementsProvider
import ca.josephroque.bowlingcompanion.feature.announcements.ui.AnnouncementUiAction
import ca.josephroque.bowlingcompanion.feature.announcements.ui.AnnouncementUiState
import ca.josephroque.bowlingcompanion.feature.announcements.ui.tenyears.TenYearsAnnouncementUiAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnnouncementViewModel @Inject constructor(
	private val announcementsProvider: AnnouncementsProvider,
	private val achievementsRepository: AchievementsRepository,
	private val userDataRepository: UserDataRepository,
) : ApproachViewModel<AnnouncementDialogEvent>() {

	private val announcement = MutableStateFlow<AnnouncementUiState?>(null)

	val uiState = announcement
		.map {
			it?.let { AnnouncementDialogUiState.Announcement(it) } ?: AnnouncementDialogUiState.NoAnnouncement
		}
		.stateIn(
			viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = AnnouncementDialogUiState.NoAnnouncement
		)

	init {
		viewModelScope.launch {
			val nextAnnouncement = announcementsProvider.getNextAnnouncement()
			announcement.value = nextAnnouncement
		}
	}

	fun handleAction(action: AnnouncementDialogUiAction) {
		when (action) {
			is AnnouncementDialogUiAction.Announcement -> handleAnnouncementAction(action.action)
		}
	}

	private fun handleAnnouncementAction(action: AnnouncementUiAction) {
		when (action) {
			is AnnouncementUiAction.Dismissed -> dismissAnnouncement()
			is AnnouncementUiAction.TenYears -> handleTenYearsAction(action.action)
		}
	}

	private fun handleTenYearsAction(action: TenYearsAnnouncementUiAction) {
		when (action) {
			is TenYearsAnnouncementUiAction.ClaimBadgeClicked -> {
				dismissAnnouncement()
				sendEvent(AnnouncementDialogEvent.NavigateToAchievements)
			}
		}
	}

	private fun dismissAnnouncement() {
		val announcement = this.announcement.getAndUpdate { null }
		when (announcement) {
			is AnnouncementUiState.TenYears -> {
				viewModelScope.launch {
					achievementsRepository.insertEvent(TenYearsAchievement.BadgeClaimed)
					userDataRepository.didDismissTenYearsAnnouncement()
				}
			}
			null -> Unit
		}
	}
}