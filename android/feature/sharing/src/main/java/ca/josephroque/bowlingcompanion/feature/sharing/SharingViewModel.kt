package ca.josephroque.bowlingcompanion.feature.sharing

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingAppearance
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingData
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingSource
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.SeriesSharingConfigurationUiAction
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.SeriesSharingConfigurationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class SharingViewModel @Inject constructor(
// TODO: Add Analytics to Sharing
// 	private val analyticsClient: AnalyticsClient,
	private val seriesRepository: SeriesRepository,
) : ApproachViewModel<SharingScreenEvent>() {

	private val seriesSharingState: MutableStateFlow<SeriesSharingConfigurationUiState> = MutableStateFlow(
		SeriesSharingConfigurationUiState(),
	)

	private val sharingSource: MutableStateFlow<SharingSource?> = MutableStateFlow(null)

	private val sharingData = sharingSource
		.mapNotNull { source ->
			when (source) {
				is SharingSource.Series ->
					seriesRepository.getShareableSeries(source.seriesId)
						.map { SharingData.Series(it) }
				is SharingSource.Game -> flowOf(SharingData.Game)
				is SharingSource.Statistic -> flowOf(SharingData.Statistic)
				null -> null
			}
		}
		.flatMapLatest { it }

	val uiState: StateFlow<SharingScreenUiState> = combine(
		sharingData,
		seriesSharingState,
	) { sharingData, seriesSharingState ->
		when (sharingData) {
			is SharingData.Series -> SharingScreenUiState.SharingSeries(seriesSharingState, sharingData)
			is SharingData.Game -> SharingScreenUiState.SharingGame
			is SharingData.Statistic -> SharingScreenUiState.SharingStatistic
		}
	}
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = SharingScreenUiState.Loading,
		)

	fun handleAction(action: SharingScreenUiAction) {
		when (action) {
			SharingScreenUiAction.ShareButtonClicked -> TODO()
			is SharingScreenUiAction.DidStartSharing -> {
				loadSource(action.source)
				setDefaultAppearance(isSystemInDarkTheme = action.isSystemInDarkTheme)
			}
			is SharingScreenUiAction.SeriesSharingAction -> handleSeriesSharingAction(action.action)
			is SharingScreenUiAction.GameSharingAction -> TODO()
			is SharingScreenUiAction.StatisticSharingAction -> TODO()
		}
	}

	private fun handleSeriesSharingAction(action: SeriesSharingConfigurationUiAction) {
		when (action) {
			is SeriesSharingConfigurationUiAction.IsDateCheckedToggled ->
				toggleIsDateChecked(isDateChecked = action.isDateChecked)
			is SeriesSharingConfigurationUiAction.IsSummaryCheckedToggled ->
				toggleIsSummaryChecked(isSummaryChecked = action.isSummaryChecked)
			is SeriesSharingConfigurationUiAction.IsBowlerCheckedToggled ->
				toggleIsBowlerChecked(isBowlerChecked = action.isBowlerChecked)
			is SeriesSharingConfigurationUiAction.IsLeagueCheckedToggled ->
				toggleIsLeagueChecked(isLeagueChecked = action.isLeagueChecked)
			is SeriesSharingConfigurationUiAction.IsHighScoreCheckedToggled ->
				toggleIsHighScoreChecked(isHighScoreChecked = action.isHighScoreChecked)
			is SeriesSharingConfigurationUiAction.IsLowScoreCheckedToggled ->
				toggleIsLowScoreChecked(isLowScoreChecked = action.isLowScoreChecked)
			is SeriesSharingConfigurationUiAction.ChartRangeMinimumChanged ->
				updateChartRangeMinimum(minimum = action.minimum)
			is SeriesSharingConfigurationUiAction.ChartRangeMaximumChanged ->
				updateChartRangeMaximum(maximum = action.maximum)
			is SeriesSharingConfigurationUiAction.AppearanceChanged ->
				updateAppearance(appearance = action.appearance)
		}
	}

	private fun loadSource(source: SharingSource) {
		sharingSource.value = source
	}

	private fun setDefaultAppearance(isSystemInDarkTheme: Boolean) {
		val appearance = if (isSystemInDarkTheme) SharingAppearance.Dark else SharingAppearance.Light
		seriesSharingState.update { it.copy(appearance = appearance) }
		// TODO: Update GameSharingState and StatisticSharingState
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
