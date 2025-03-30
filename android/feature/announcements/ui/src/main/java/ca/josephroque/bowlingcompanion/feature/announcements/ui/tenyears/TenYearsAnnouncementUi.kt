package ca.josephroque.bowlingcompanion.feature.announcements.ui.tenyears

object TenYearsAnnouncementUiState

sealed interface TenYearsAnnouncementUiAction {
	data object ClaimBadgeClicked : TenYearsAnnouncementUiAction
}