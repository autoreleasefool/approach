package ca.josephroque.bowlingcompanion.feature.datamanagement.import

import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.import.DataImportUiAction
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.import.DataImportUiState

sealed interface DataImportScreenUiState {
	data object Loading: DataImportScreenUiState

	data class Loaded(
		val dataImport: DataImportUiState,
	): DataImportScreenUiState
}

sealed interface DataImportScreenUiAction {
	data class ReceivedVersionInfo(
		val versionName: String,
		val versionCode: String,
	): DataImportScreenUiAction

	data class DataImport(
		val action: DataImportUiAction,
	): DataImportScreenUiAction
}

sealed interface DataImportScreenEvent {
	data object Dismissed: DataImportScreenEvent
	data object LaunchFilePicker: DataImportScreenEvent
}