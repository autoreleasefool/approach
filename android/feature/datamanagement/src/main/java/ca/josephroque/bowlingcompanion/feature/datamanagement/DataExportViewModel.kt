package ca.josephroque.bowlingcompanion.feature.datamanagement

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.DataTransferRepository
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.DataExportUiAction
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.DataExportUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
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
	private val _dataExport: Flow<DataExportUiState> = flow {
		val existingBackup = dataTransferRepository.getExistingDatabaseBackup()
		val lastModified = existingBackup?.lastModified()
			?.let { Instant.fromEpochMilliseconds(it) }
			?.toLocalDate()

		emit(DataExportUiState(lastModified))
	}

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
			DataExportUiAction.ExportClicked -> exportData()
		}
	}

	private fun exportData() {
		viewModelScope.launch {
			val backupFile = dataTransferRepository.getOrCreateDatabaseBackup()
			sendEvent(DataExportScreenEvent.LaunchShareIntent(backupFile))
		}
	}
}

private fun Instant.toLocalDate(): LocalDate =
	toLocalDateTime(TimeZone.currentSystemDefault()).date