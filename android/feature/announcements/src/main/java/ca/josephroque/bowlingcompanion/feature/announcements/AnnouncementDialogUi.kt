package ca.josephroque.bowlingcompanion.feature.announcements

import ca.josephroque.bowlingcompanion.feature.announcements.ui.AnnouncementUiAction
import ca.josephroque.bowlingcompanion.feature.announcements.ui.AnnouncementUiState

sealed interface AnnouncementDialogUiState {
	data object NoAnnouncement : AnnouncementDialogUiState
	data class Announcement(val announcement: AnnouncementUiState) : AnnouncementDialogUiState
}

sealed interface AnnouncementDialogUiAction {
	data class Announcement(val action: AnnouncementUiAction) : AnnouncementDialogUiAction
}

sealed interface AnnouncementDialogEvent {
	data object NavigateToAchievements : AnnouncementDialogEvent
}
