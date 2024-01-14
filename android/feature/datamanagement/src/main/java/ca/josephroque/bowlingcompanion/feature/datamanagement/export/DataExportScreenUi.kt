package ca.josephroque.bowlingcompanion.feature.datamanagement.export

import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.export.DataExportUiAction
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.export.DataExportUiState
import java.io.File

sealed interface DataExportScreenUiState {
	data object Loading: DataExportScreenUiState

	data class Loaded(
		val dataExport: DataExportUiState,
	): DataExportScreenUiState
}

sealed interface DataExportScreenUiAction {
	data class DataExport(
		val action: DataExportUiAction,
	): DataExportScreenUiAction
}

sealed interface DataExportScreenEvent {
	data object Dismissed: DataExportScreenEvent
	data class LaunchShareIntent(val file: File): DataExportScreenEvent
}