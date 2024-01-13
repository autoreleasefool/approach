package ca.josephroque.bowlingcompanion.feature.datamanagement.dataimport

import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.dataimport.DataImportUiAction
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.dataimport.DataImportUiState

sealed interface DataImportScreenUiState {
	data object Loading: DataImportScreenUiState

	data class Loaded(
		val dataImport: DataImportUiState,
	): DataImportScreenUiState
}

sealed interface DataImportScreenUiAction {
	data object OnAppear: DataImportScreenUiAction

	data class DataImport(
		val action: DataImportUiAction,
	): DataImportScreenUiAction
}

sealed interface DataImportScreenEvent {
	data object Dismissed: DataImportScreenEvent
}