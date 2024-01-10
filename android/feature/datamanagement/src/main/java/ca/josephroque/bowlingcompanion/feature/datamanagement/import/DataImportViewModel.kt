package ca.josephroque.bowlingcompanion.feature.datamanagement.import

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.DataTransferRepository
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.import.DataImportUiAction
import ca.josephroque.bowlingcompanion.feature.datamanagement.ui.import.DataImportUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataImportViewModel @Inject constructor(
//	private val dataTransferRepository: DataTransferRepository,
): ApproachViewModel<DataImportScreenEvent>() {

	private val _dataImportState = MutableStateFlow(DataImportUiState())

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
			is DataImportScreenUiAction.ReceivedVersionInfo -> updateVersionInfo(action.versionName, action.versionCode)
			is DataImportScreenUiAction.DataImport -> handleDataImportAction(action.action)
		}
	}

	private fun handleDataImportAction(action: DataImportUiAction) {
		when (action) {
			DataImportUiAction.BackClicked -> sendEvent(DataImportScreenEvent.Dismissed)
			DataImportUiAction.StartImportClicked -> importData()
		}
	}

	private fun importData() {
		viewModelScope.launch {
//			dataTransferRepository.importDatabase()
//			sendEvent(DataImportScreenEvent.Dismissed)
		}
	}

	private fun updateVersionInfo(versionName: String, versionCode: String) {
		_dataImportState.update { it.copy(versionName = versionName, versionCode = versionCode) }
	}
}