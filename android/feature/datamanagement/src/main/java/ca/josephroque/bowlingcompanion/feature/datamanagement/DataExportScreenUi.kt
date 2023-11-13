package ca.josephroque.bowlingcompanion.feature.datamanagement

import android.content.Intent
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.DataExportUiAction
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.DataExportUiState
import java.io.File

sealed interface DataExportScreenUiState {
	data object Loading: DataExportScreenUiState

	data class Loaded(
		val dataExport: DataExportUiState,
	): DataExportScreenUiState
}

sealed interface DataExportScreenUiAction {
	data object HandledShareAction: DataExportScreenUiAction
	data class DataExportAction(
		val dataExport: DataExportUiAction,
	): DataExportScreenUiAction
}

sealed interface DataExportScreenEvent {
	data object Dismissed: DataExportScreenEvent
	data class LaunchShareIntent(val file: File): DataExportScreenEvent
}