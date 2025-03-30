package ca.josephroque.bowlingcompanion.feature.announcements.ui

import ca.josephroque.bowlingcompanion.feature.announcements.ui.tenyears.TenYearsAnnouncementUiAction
import ca.josephroque.bowlingcompanion.feature.announcements.ui.tenyears.TenYearsAnnouncementUiState

sealed interface AnnouncementUiState {
	data class TenYears(val state: TenYearsAnnouncementUiState) : AnnouncementUiState
}

sealed interface AnnouncementUiAction {
	data object Dismissed : AnnouncementUiAction

	data class TenYears(val action: TenYearsAnnouncementUiAction) : AnnouncementUiAction
}