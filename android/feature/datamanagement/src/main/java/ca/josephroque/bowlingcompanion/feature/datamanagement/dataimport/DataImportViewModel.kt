package ca.josephroque.bowlingcompanion.feature.datamanagement.dataimport

import android.net.Uri
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.data.ImportedData
import ca.josephroque.bowlingcompanion.core.analytics.trackable.data.RestoredData
import ca.josephroque.bowlingcompanion.core.common.system.SystemInfoService
import ca.josephroque.bowlingcompanion.core.common.utils.toLocalDate
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.service.DataImportService
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.dataimport.DataImportProgress
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.dataimport.DataImportUiAction
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.dataimport.DataImportUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import javax.inject.Inject

@HiltViewModel
class DataImportViewModel @Inject constructor(
	private val dataImportService: DataImportService,
	systemInfoService: SystemInfoService,
	private val analyticsClient: AnalyticsClient,
): ApproachViewModel<DataImportScreenEvent>() {

	private val _dataImportState = MutableStateFlow(DataImportUiState(
		versionCode = systemInfoService.versionCode,
		versionName = systemInfoService.versionName,
	))

	val uiState: StateFlow<DataImportScreenUiState> =
		_dataImportState
			.map { DataImportScreenUiState.Loaded(it) }
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = DataImportScreenUiState.Loading,
			)

	fun handleAction(action: DataImportScreenUiAction) {
		when (action) {
			DataImportScreenUiAction.OnAppear -> getLatestImportDate()
			is DataImportScreenUiAction.DataImport -> handleDataImportAction(action.action)
		}
	}

	private fun handleDataImportAction(action: DataImportUiAction) {
		when (action) {
			DataImportUiAction.BackClicked -> sendEvent(DataImportScreenEvent.Dismissed)
			DataImportUiAction.StartImportClicked -> importData()
			DataImportUiAction.RestoreClicked -> restoreData()
			DataImportUiAction.CancelRestoreClicked -> cancelRestore()
			DataImportUiAction.ConfirmRestoreClicked -> confirmRestore()
			is DataImportUiAction.FileSelected -> handleImportedUri(action.uri)
		}
	}

	private fun restoreData() {
		_dataImportState.update { it.copy(isShowingRestoreDialog = true) }
	}

	private fun cancelRestore() {
		_dataImportState.update { it.copy(isShowingRestoreDialog = false) }
	}

	private fun confirmRestore() {
		_dataImportState.update {
			it.copy(
				isShowingRestoreDialog = false,
				progress = DataImportProgress.Importing,
			)
		}

		viewModelScope.launch {
			try {
				dataImportService.restoreData()
				_dataImportState.update { it.copy(progress = DataImportProgress.RestoreComplete) }
			} catch (e: Exception) {
				_dataImportState.update { it.copy(progress = DataImportProgress.Failed(e)) }
			}
		}

		analyticsClient.trackEvent(RestoredData)
	}

	private fun importData() {
		_dataImportState.update { it.copy(progress = DataImportProgress.PickingFile) }
	}

	private fun handleImportedUri(uri: Uri?) {
		if (uri == null) {
			_dataImportState.update { it.copy(progress = DataImportProgress.NotStarted) }
			return
		}

		_dataImportState.update { it.copy(progress = DataImportProgress.Importing) }
		viewModelScope.launch {
			startImport(uri)
		}
	}

	private suspend fun startImport(uri: Uri) {
		try {
			dataImportService.importData(source = uri)
			_dataImportState.update { it.copy(progress = DataImportProgress.ImportComplete) }
			analyticsClient.trackEvent(ImportedData)
		} catch (e: Exception) {
			_dataImportState.update { it.copy(progress = DataImportProgress.Failed(e)) }
		}
	}

	private fun getLatestImportDate() {
		viewModelScope.launch {
			dataImportService.getLatestBackup()
				.firstOrNull()
				?.let { backupFile ->
					_dataImportState.update {
						it.copy(lastImportDate = Instant.fromEpochMilliseconds(backupFile.lastModified()).toLocalDate())
					}
				}
		}
	}
}