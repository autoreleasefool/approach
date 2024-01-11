package ca.josephroque.bowlingcompanion.feature.datamanagement.export

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.DataTransferRepository
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.export.DataExportUiAction
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.export.DataExportUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class DataExportViewModel @Inject constructor(
	private val dataTransferRepository: DataTransferRepository,
): ApproachViewModel<DataExportScreenEvent>() {
	private val _dataExport: Flow<DataExportUiState> =
		dataTransferRepository.getLatestDatabaseExport()
			.map { exportFile ->
				exportFile?.lastModified()
					?.let { Instant.fromEpochMilliseconds(it) }
					?.toLocalDate()
			}
			.map { DataExportUiState(it) }

	val uiState: StateFlow<DataExportScreenUiState> = _dataExport
		.map {
			DataExportScreenUiState.Loaded(it)
		}.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = DataExportScreenUiState.Loading,
		)

	fun handleAction(action: DataExportScreenUiAction) {
		when (action) {
			is DataExportScreenUiAction.DataExportAction -> handleDataExportAction(action.dataExport)
		}
	}

	private fun handleDataExportAction(action: DataExportUiAction) {
		when (action) {
			DataExportUiAction.BackClicked -> sendEvent(DataExportScreenEvent.Dismissed)
			DataExportUiAction.SaveClicked -> saveData()
			DataExportUiAction.ShareClicked -> shareData()
		}
	}

	private fun saveData() {
		viewModelScope.launch {
			val exportFile = dataTransferRepository.getOrCreateDatabaseExport()
			sendEvent(DataExportScreenEvent.LaunchCreateDocumentIntent(exportFile))
		}
	}

	private fun shareData() {
		viewModelScope.launch {
			val exportFile = dataTransferRepository.getOrCreateDatabaseExport()
			sendEvent(DataExportScreenEvent.LaunchShareIntent(exportFile))
		}
	}
}

private fun Instant.toLocalDate(): LocalDate =
	toLocalDateTime(TimeZone.currentSystemDefault()).date