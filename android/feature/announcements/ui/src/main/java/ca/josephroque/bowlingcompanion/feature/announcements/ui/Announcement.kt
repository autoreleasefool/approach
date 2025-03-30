package ca.josephroque.bowlingcompanion.feature.announcements.ui

import androidx.compose.runtime.Composable
import ca.josephroque.bowlingcompanion.feature.announcements.ui.tenyears.TenYearsAnnouncement

@Composable
fun Announcement(
	state: AnnouncementUiState,
	onAction: (AnnouncementUiAction) -> Unit,
) {
	when (state) {
		is AnnouncementUiState.TenYears -> TenYearsAnnouncement(
			onAction = { onAction(AnnouncementUiAction.TenYears(it)) },
		)
	}
}