package ca.josephroque.bowlingcompanion.feature.datamanagement.ui.dataimport

import android.net.Uri
import kotlinx.datetime.LocalDate

sealed interface DataImportProgress {
	data object NotStarted: DataImportProgress
	data object PickingFile: DataImportProgress
	data object Importing: DataImportProgress
	data class Failed(val exception: Exception): DataImportProgress
	data object Complete: DataImportProgress
}

data class DataImportUiState(
	val progress: DataImportProgress = DataImportProgress.NotStarted,
	val lastImportDate: LocalDate? = null,
	val isShowingRestoreDialog: Boolean = false,
	val versionName: String = "",
	val versionCode: String = "",
) {
	val isRestoreAvailable: Boolean
		get() = lastImportDate != null
}

sealed interface DataImportUiAction {
	data object BackClicked: DataImportUiAction
	data object StartImportClicked: DataImportUiAction
	data object RestoreClicked: DataImportUiAction
	data object CancelRestoreClicked: DataImportUiAction
	data object ConfirmRestoreClicked: DataImportUiAction

	data class FileSelected(val uri: Uri?): DataImportUiAction
}