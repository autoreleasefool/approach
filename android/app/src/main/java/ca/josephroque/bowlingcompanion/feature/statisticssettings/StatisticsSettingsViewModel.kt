package ca.josephroque.bowlingcompanion.feature.statisticssettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsSettingsViewModel @Inject constructor(
	private val userDataRepository: UserDataRepository,
): ViewModel() {
	val uiState: StateFlow<StatisticsSettingsUiState> = userDataRepository.userData.map {
		StatisticsSettingsUiState.Success(
			isCountingH2AsH = !it.isCountingH2AsHDisabled,
			isCountingSplitWithBonusAsSplit = !it.isCountingSplitWithBonusAsSplitDisabled,
			isHidingZeroStatistics = !it.isShowingZeroStatistics,
			isHidingWidgetsInBowlersList = it.isHidingWidgetsInBowlersList,
			isHidingWidgetsInLeaguesList = it.isHidingWidgetsInLeaguesList,
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = StatisticsSettingsUiState.Loading,
	)

	fun toggleIsCountingH2AsH(newValue: Boolean?) {
		viewModelScope.launch {
			val currentValue = !userDataRepository.userData.first().isCountingH2AsHDisabled
			userDataRepository.setIsCountingH2AsH(newValue ?: !currentValue)
		}
	}

	fun toggleIsCountingSplitWithBonusAsSplit(newValue: Boolean?) {
		viewModelScope.launch {
			val currentValue = userDataRepository.userData.first().isCountingSplitWithBonusAsSplitDisabled
			userDataRepository.setIsCountingSplitWithBonusAsSplit(newValue ?: !currentValue)
		}
	}

	fun toggleIsHidingZeroStatistics(newValue: Boolean?) {
		viewModelScope.launch {
			val currentValue = !userDataRepository.userData.first().isShowingZeroStatistics
			userDataRepository.setIsHidingZeroStatistics(newValue ?: !currentValue)
		}
	}

	fun toggleIsHidingWidgetsInBowlersList(newValue: Boolean?) {
		viewModelScope.launch {
			val currentValue = userDataRepository.userData.first().isHidingWidgetsInBowlersList
			userDataRepository.setIsHidingWidgetsInBowlersList(newValue ?: !currentValue)
		}
	}

	fun toggleIsHidingWidgetsInLeaguesList(newValue: Boolean?) {
		viewModelScope.launch {
			val currentValue = userDataRepository.userData.first().isHidingWidgetsInLeaguesList
			userDataRepository.setIsHidingWidgetsInLeaguesList(newValue ?: !currentValue)
		}
	}
}

sealed interface StatisticsSettingsUiState {
	data object Loading: StatisticsSettingsUiState
	data class Success(
		val isCountingH2AsH: Boolean,
		val isCountingSplitWithBonusAsSplit: Boolean,
		val isHidingZeroStatistics: Boolean,
		val isHidingWidgetsInBowlersList: Boolean,
		val isHidingWidgetsInLeaguesList: Boolean,
	): StatisticsSettingsUiState
}