package ca.josephroque.bowlingcompanion.feature.datamanagement.export

import android.net.Uri
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.system.SystemInfoService
import ca.josephroque.bowlingcompanion.core.common.utils.toLocalDate
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.DataTransferRepository
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.export.DataExportProgress
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.export.DataExportUiAction
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.export.DataExportUiState
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
class DataExportViewModel @Inject constructor(
	private val dataTransferRepository: DataTransferRepository,
	systemInfoService: SystemInfoService,
): ApproachViewModel<DataExportScreenEvent>() {

	private val _dataExportState = MutableStateFlow(DataExportUiState(
		versionName = systemInfoService.versionName,
		versionCode = systemInfoService.versionCode,
	))

	val uiState: StateFlow<DataExportScreenUiState> = _dataExportState
		.map { DataExportScreenUiState.Loaded(it) }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = DataExportScreenUiState.Loading,
		)

	fun handleAction(action: DataExportScreenUiAction) {
		when (action) {
			DataExportScreenUiAction.OnAppear -> getLatestExportDate()
			is DataExportScreenUiAction.DataExport -> handleDataExportAction(action.action)
		}
	}

	private fun handleDataExportAction(action: DataExportUiAction) {
		when (action) {
			DataExportUiAction.BackClicked -> sendEvent(DataExportScreenEvent.Dismissed)
			DataExportUiAction.SaveClicked -> saveData()
			DataExportUiAction.ShareClicked -> shareData()
			is DataExportUiAction.DestinationPicked -> handleExportedUri(action.uri)
		}
	}

	private fun getLatestExportDate() {
		viewModelScope.launch {
			dataTransferRepository.getLatestDatabaseExport()
				.firstOrNull()
				?.let { exportFile ->
					_dataExportState.update {
						it.copy(lastExportDate = Instant.fromEpochMilliseconds(exportFile.lastModified()).toLocalDate())
					}
				}
		}
	}

	private fun saveData() {
		_dataExportState.update {
			it.copy(
				progress = DataExportProgress.PickingDestination(dataTransferRepository.exportFileName),
			)
		}
	}

	private fun shareData() {
		viewModelScope.launch {
			val exportFile = dataTransferRepository.getOrCreateDatabaseExport()
			sendEvent(DataExportScreenEvent.LaunchShareIntent(exportFile))
		}
	}

	private fun handleExportedUri(uri: Uri?) {
		if (uri == null) {
			_dataExportState.update { it.copy(progress = DataExportProgress.NotStarted) }
			return
		} else {
			_dataExportState.update { it.copy(progress = DataExportProgress.Exporting) }
		}

		viewModelScope.launch {
			dataTransferRepository.exportData(uri)
			_dataExportState.update { it.copy(progress = DataExportProgress.Complete) }
		}
	}
}