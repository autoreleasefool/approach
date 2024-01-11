package ca.josephroque.bowlingcompanion.feature.datamanagement.ui.export

import kotlinx.datetime.LocalDate

data class DataExportUiState(
	val lastExportDate: LocalDate?,
)

sealed interface DataExportUiAction {
	data object BackClicked: DataExportUiAction
	data object ShareClicked: DataExportUiAction
	data object SaveClicked: DataExportUiAction
}