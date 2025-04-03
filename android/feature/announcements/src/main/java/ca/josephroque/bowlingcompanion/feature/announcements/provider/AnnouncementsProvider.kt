package ca.josephroque.bowlingcompanion.feature.announcements.provider

import ca.josephroque.bowlingcompanion.feature.announcements.ui.AnnouncementUiState

interface AnnouncementsProvider {
	suspend fun getNextAnnouncement(): AnnouncementUiState?
}
