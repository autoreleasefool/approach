package ca.josephroque.bowlingcompanion.feature.archives

import ca.josephroque.bowlingcompanion.feature.archives.ui.ArchivesListUiAction
import ca.josephroque.bowlingcompanion.feature.archives.ui.ArchivesListUiState

sealed interface ArchivesListScreenUiState {
	data object Loading: ArchivesListScreenUiState

	data class Loaded(
		val archivesList: ArchivesListUiState,
	): ArchivesListScreenUiState
}

sealed interface ArchivesListScreenUiAction {
	data class ListAction(val action: ArchivesListUiAction): ArchivesListScreenUiAction
}

sealed interface ArchivesListScreenEvent {
	data object Dismissed: ArchivesListScreenEvent
}