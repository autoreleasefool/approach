package ca.josephroque.bowlingcompanion.feature.datamanagement.ui.export

import android.net.Uri
import kotlinx.datetime.LocalDate

sealed interface DataExportProgress {
	data object NotStarted: DataExportProgress
	data class PickingDestination(val fileName: String): DataExportProgress
	data object Exporting: DataExportProgress
	data class Failed(val exception: Exception): DataExportProgress
	data object Complete: DataExportProgress
}

data class DataExportUiState(
	val lastExportDate: LocalDate? = null,
	val progress: DataExportProgress = DataExportProgress.NotStarted,
	val versionName: String = "",
	val versionCode: String = "",
)

sealed interface DataExportUiAction {
	data object BackClicked: DataExportUiAction
	data object ShareClicked: DataExportUiAction
	data object SaveClicked: DataExportUiAction
	data class DestinationPicked(val uri: Uri?): DataExportUiAction
}