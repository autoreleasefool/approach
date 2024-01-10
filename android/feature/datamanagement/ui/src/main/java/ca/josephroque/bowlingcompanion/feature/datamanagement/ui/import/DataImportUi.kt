package ca.josephroque.bowlingcompanion.feature.datamanagement.ui.import

sealed interface DataImportProgress {
	data object NotStarted: DataImportProgress
	data object PickingFile: DataImportProgress
	data object Importing: DataImportProgress
	data class Failed(val error: Error): DataImportProgress
	data object Complete: DataImportProgress
}

data class DataImportUiState(
	val progress: DataImportProgress = DataImportProgress.NotStarted,
	val versionName: String = "",
	val versionCode: String = "",
)

sealed interface DataImportUiAction {
	data object BackClicked: DataImportUiAction
	data object StartImportClicked: DataImportUiAction
}