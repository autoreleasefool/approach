package ca.josephroque.bowlingcompanion.feature.sharing

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingSource
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.SeriesSharingUiAction
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.SeriesSharingUiState
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.SharingAppearance
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class SharingViewModel @Inject constructor(
// TODO: Add Analytics to Sharing
// 	private val analyticsClient: AnalyticsClient,
) : ApproachViewModel<SharingScreenEvent>() {
	private val seriesSharingState: MutableStateFlow<SeriesSharingUiState> = MutableStateFlow(
		SeriesSharingUiState(
			appearance = SharingAppearance.Light,
		),
	)

	val uiState: StateFlow<SharingScreenUiState> = seriesSharingState
		.mapNotNull { SharingScreenUiState.SharingSeries(it) }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = SharingScreenUiState.Loading,
		)

	fun handleAction(action: SharingScreenUiAction) {
		when (action) {
			is SharingScreenUiAction.DidStartSharing -> loadSource(action.source)
			is SharingScreenUiAction.SeriesSharingAction -> handleSeriesSharingAction(action.action)
		}
	}

	private fun handleSeriesSharingAction(action: SeriesSharingUiAction) {
		when (action) {
			is SeriesSharingUiAction.IsDateCheckedToggled ->
				toggleIsDateChecked(isDateChecked = action.isDateChecked)
			is SeriesSharingUiAction.IsSummaryCheckedToggled ->
				toggleIsSummaryChecked(isSummaryChecked = action.isSummaryChecked)
			is SeriesSharingUiAction.IsBowlerCheckedToggled ->
				toggleIsBowlerChecked(isBowlerChecked = action.isBowlerChecked)
			is SeriesSharingUiAction.IsLeagueCheckedToggled ->
				toggleIsLeagueChecked(isLeagueChecked = action.isLeagueChecked)
			is SeriesSharingUiAction.IsHighScoreCheckedToggled ->
				toggleIsHighScoreChecked(isHighScoreChecked = action.isHighScoreChecked)
			is SeriesSharingUiAction.IsLowScoreCheckedToggled ->
				toggleIsLowScoreChecked(isLowScoreChecked = action.isLowScoreChecked)
			is SeriesSharingUiAction.ChartRangeMinimumChanged ->
				updateChartRangeMinimum(minimum = action.minimum)
			is SeriesSharingUiAction.ChartRangeMaximumChanged ->
				updateChartRangeMaximum(maximum = action.maximum)
			is SeriesSharingUiAction.AppearanceChanged ->
				updateAppearance(appearance = action.appearance)
		}
	}

	private fun loadSource(source: SharingSource) {
	}

	private fun toggleIsDateChecked(isDateChecked: Boolean) {
		seriesSharingState.update { it.copy(isDateChecked = isDateChecked) }
	}

	private fun toggleIsSummaryChecked(isSummaryChecked: Boolean) {
		seriesSharingState.update { it.copy(isSummaryChecked = isSummaryChecked) }
	}

	private fun toggleIsBowlerChecked(isBowlerChecked: Boolean) {
		seriesSharingState.update { it.copy(isBowlerChecked = isBowlerChecked) }
	}

	private fun toggleIsLeagueChecked(isLeagueChecked: Boolean) {
		seriesSharingState.update { it.copy(isLeagueChecked = isLeagueChecked) }
	}

	private fun toggleIsHighScoreChecked(isHighScoreChecked: Boolean) {
		seriesSharingState.update { it.copy(isHighScoreChecked = isHighScoreChecked) }
	}

	private fun toggleIsLowScoreChecked(isLowScoreChecked: Boolean) {
		seriesSharingState.update { it.copy(isLowScoreChecked = isLowScoreChecked) }
	}

	private fun updateChartRangeMinimum(minimum: Int) {
		seriesSharingState.update {
			it.copy(chartRange = IntRange(start = minimum, endInclusive = it.chartRange.last))
		}
	}

	private fun updateChartRangeMaximum(maximum: Int) {
		seriesSharingState.update {
			it.copy(chartRange = IntRange(start = it.chartRange.first, endInclusive = maximum))
		}
	}

	private fun updateAppearance(appearance: SharingAppearance) {
		seriesSharingState.update { it.copy(appearance = appearance) }
	}
}
