package ca.josephroque.bowlingcompanion.feature.settings.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.feature.settings.ui.statistics.StatisticsSettingsUiAction
import ca.josephroque.bowlingcompanion.feature.settings.ui.statistics.StatisticsSettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsSettingsViewModel @Inject constructor(
	private val userDataRepository: UserDataRepository,
): ViewModel() {

	private val _settingsState: Flow<StatisticsSettingsUiState> = userDataRepository.userData.map {
		StatisticsSettingsUiState(
			isCountingH2AsH = !it.isCountingH2AsHDisabled,
			isCountingSplitWithBonusAsSplit = !it.isCountingSplitWithBonusAsSplitDisabled,
			isHidingZeroStatistics = !it.isShowingZeroStatistics,
			isHidingWidgetsInBowlersList = it.isHidingWidgetsInBowlersList,
			isHidingWidgetsInLeaguesList = it.isHidingWidgetsInLeaguesList,
		)
	}

	val uiState: StateFlow<StatisticsSettingsScreenUiState> = _settingsState.map {
		StatisticsSettingsScreenUiState.Loaded(it)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = StatisticsSettingsScreenUiState.Loading,
	)

	private val _events: MutableStateFlow<StatisticsSettingsScreenEvent?> = MutableStateFlow(null)
	val events = _events.asStateFlow()

	fun handleAction(action: StatisticsSettingsScreenUiAction) {
		when (action) {
			is StatisticsSettingsScreenUiAction.StatisticsSettingsAction -> handleStatisticsSettingsAction(action.action)
		}
	}

	private fun handleStatisticsSettingsAction(action: StatisticsSettingsUiAction) {
		when (action) {
			StatisticsSettingsUiAction.BackClicked -> _events.value = StatisticsSettingsScreenEvent.Dismissed
			is StatisticsSettingsUiAction.ToggleIsCountingH2AsH -> toggleIsCountingH2AsH(action.newValue)
			is StatisticsSettingsUiAction.ToggleIsCountingSplitWithBonusAsSplit -> toggleIsCountingSplitWithBonusAsSplit(action.newValue)
			is StatisticsSettingsUiAction.ToggleIsHidingZeroStatistics -> toggleIsHidingZeroStatistics(action.newValue)
			is StatisticsSettingsUiAction.ToggleIsHidingWidgetsInBowlersList -> toggleIsHidingWidgetsInBowlersList(action.newValue)
			is StatisticsSettingsUiAction.ToggleIsHidingWidgetsInLeaguesList -> toggleIsHidingWidgetsInLeaguesList(action.newValue)
		}
	}

	private fun toggleIsCountingH2AsH(newValue: Boolean?) {
		viewModelScope.launch {
			val currentValue = !userDataRepository.userData.first().isCountingH2AsHDisabled
			userDataRepository.setIsCountingH2AsH(newValue ?: !currentValue)
		}
	}

	private fun toggleIsCountingSplitWithBonusAsSplit(newValue: Boolean?) {
		viewModelScope.launch {
			val currentValue = userDataRepository.userData.first().isCountingSplitWithBonusAsSplitDisabled
			userDataRepository.setIsCountingSplitWithBonusAsSplit(newValue ?: !currentValue)
		}
	}

	private fun toggleIsHidingZeroStatistics(newValue: Boolean?) {
		viewModelScope.launch {
			val currentValue = !userDataRepository.userData.first().isShowingZeroStatistics
			userDataRepository.setIsHidingZeroStatistics(newValue ?: !currentValue)
		}
	}

	private fun toggleIsHidingWidgetsInBowlersList(newValue: Boolean?) {
		viewModelScope.launch {
			val currentValue = userDataRepository.userData.first().isHidingWidgetsInBowlersList
			userDataRepository.setIsHidingWidgetsInBowlersList(newValue ?: !currentValue)
		}
	}

	private fun toggleIsHidingWidgetsInLeaguesList(newValue: Boolean?) {
		viewModelScope.launch {
			val currentValue = userDataRepository.userData.first().isHidingWidgetsInLeaguesList
			userDataRepository.setIsHidingWidgetsInLeaguesList(newValue ?: !currentValue)
		}
	}
}

