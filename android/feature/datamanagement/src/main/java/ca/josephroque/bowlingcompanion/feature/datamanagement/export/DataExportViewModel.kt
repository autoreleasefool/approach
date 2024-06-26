package ca.josephroque.bowlingcompanion.feature.datamanagement.export

import android.net.Uri
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.data.ExportedData
import ca.josephroque.bowlingcompanion.core.common.system.SystemInfoService
import ca.josephroque.bowlingcompanion.core.common.utils.toLocalDate
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.service.DataExportService
import ca.josephroque.bowlingcompanion.core.error.ErrorReporting
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.export.DataExportProgress
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.export.DataExportUiAction
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.export.DataExportUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

@HiltViewModel
class DataExportViewModel @Inject constructor(
	private val dataExportService: DataExportService,
	systemInfoService: SystemInfoService,
	private val analyticsClient: AnalyticsClient,
	private val errorReporting: ErrorReporting,
) : ApproachViewModel<DataExportScreenEvent>() {

	private val dataExportState = MutableStateFlow(
		DataExportUiState(
			versionName = systemInfoService.versionName,
			versionCode = systemInfoService.versionCode,
		),
	)

	val uiState: StateFlow<DataExportScreenUiState> = dataExportState
		.map { DataExportScreenUiState.Loaded(it) }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = DataExportScreenUiState.Loading,
		)

	init {
		viewModelScope.launch {
			dataExportService.getLatestExport().collect { file ->
				dataExportState.update {
					it.copy(
						lastExportDate = if (file == null) {
							null
						} else {
							Instant.fromEpochMilliseconds(
								file.lastModified(),
							).toLocalDate()
						},
					)
				}
			}
		}
	}

	fun handleAction(action: DataExportScreenUiAction) {
		when (action) {
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

	private fun saveData() {
		dataExportState.update {
			it.copy(
				progress = DataExportProgress.PickingDestination(dataExportService.exportDestination),
			)
		}

		analyticsClient.trackEvent(ExportedData)
	}

	private fun shareData() {
		viewModelScope.launch {
			try {
				val exportFile = dataExportService.getOrCreateExport()
				sendEvent(DataExportScreenEvent.LaunchShareIntent(exportFile))
			} catch (e: Exception) {
				errorReporting.captureException(e)
				dataExportState.update { it.copy(progress = DataExportProgress.Failed(e)) }
			}
		}

		analyticsClient.trackEvent(ExportedData)
	}

	private fun handleExportedUri(uri: Uri?) {
		if (uri == null) {
			dataExportState.update { it.copy(progress = DataExportProgress.NotStarted) }
			return
		} else {
			dataExportState.update { it.copy(progress = DataExportProgress.Exporting) }
		}

		viewModelScope.launch {
			try {
				dataExportService.exportDataToUri(uri = uri)
				dataExportState.update { it.copy(progress = DataExportProgress.Complete) }
			} catch (e: Exception) {
				errorReporting.captureException(e)
				dataExportState.update { it.copy(progress = DataExportProgress.Failed(e)) }
			}
		}
	}
}
