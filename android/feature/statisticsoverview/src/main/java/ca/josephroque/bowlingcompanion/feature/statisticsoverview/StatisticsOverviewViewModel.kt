package ca.josephroque.bowlingcompanion.feature.statisticsoverview

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.StatisticsRepository
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.SourcePickerUiState
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.StatisticsOverviewUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.StatisticsOverviewUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsOverviewViewModel @Inject constructor(
	private val statisticsRepository: StatisticsRepository,
): ApproachViewModel<StatisticsOverviewScreenEvent>() {

	private val _source: MutableStateFlow<TrackableFilter.Source?> = MutableStateFlow(null)
	private val _sourceSummaries = _source.map {
		if (it == null) {
			statisticsRepository.getDefaultSource()
		} else {
			statisticsRepository.getSourceDetails(it)
		}
	}

	private val _isShowingSourcePicker: MutableStateFlow<Boolean> = MutableStateFlow(false)

	val uiState = combine(
		_sourceSummaries,
		_isShowingSourcePicker,
	) { source, isShowingSourcePicker ->
		StatisticsOverviewScreenUiState.Loaded(
			statisticsOverview = StatisticsOverviewUiState(
				sourcePicker = SourcePickerUiState(
					isShowing = isShowingSourcePicker,
					source = source,
				),
			)
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = StatisticsOverviewScreenUiState.Loading,
	)

	fun handleAction(action: StatisticsOverviewScreenUiAction) {
		when (action) {
			is StatisticsOverviewScreenUiAction.UpdatedBowler -> {
				_isShowingSourcePicker.value = true
				_source.value = action.bowler?.let { TrackableFilter.Source.Bowler(it) }
			}
			is StatisticsOverviewScreenUiAction.UpdatedLeague -> {
				_isShowingSourcePicker.value = true
				_source.value = action.league?.let { TrackableFilter.Source.League(it) }
			}
			is StatisticsOverviewScreenUiAction.UpdatedSeries -> {
				_isShowingSourcePicker.value = true
				_source.value = action.series?.let { TrackableFilter.Source.Series(it) }
			}
			is StatisticsOverviewScreenUiAction.UpdatedGame -> {
				_isShowingSourcePicker.value = true
				_source.value = action.game?.let { TrackableFilter.Source.Game(it) }
			}
			is StatisticsOverviewScreenUiAction.StatisticsOverviewAction ->
				handleStatisticsOverviewAction(action.action)
		}
	}

	private fun handleStatisticsOverviewAction(action: StatisticsOverviewUiAction) {
		when (action) {
			StatisticsOverviewUiAction.SourcePickerDismissed -> {
				_isShowingSourcePicker.value = false
			}
			StatisticsOverviewUiAction.ViewMoreClicked -> {
				_isShowingSourcePicker.value = true
			}
			StatisticsOverviewUiAction.ApplyFilterClicked -> {
				val source = _source.value ?: return
				_isShowingSourcePicker.value = false
				sendEvent(StatisticsOverviewScreenEvent.ShowStatistics(
					TrackableFilter(source = source)
				))
			}
			is StatisticsOverviewUiAction.SourcePickerBowlerClicked -> showBowlerPicker()
			is StatisticsOverviewUiAction.SourcePickerLeagueClicked -> showLeaguePicker()
			is StatisticsOverviewUiAction.SourcePickerSeriesClicked -> showSeriesPicker()
			is StatisticsOverviewUiAction.SourcePickerGameClicked -> showGamePicker()
		}
	}

	private fun showBowlerPicker() {
		viewModelScope.launch {
			val source = _sourceSummaries.first()
			_isShowingSourcePicker.value = false
			sendEvent(StatisticsOverviewScreenEvent.EditBowler(source?.bowler?.id))
		}
	}

	private fun showLeaguePicker() {
		viewModelScope.launch {
			val source = _sourceSummaries.first()

			// Only show league picker if bowler is selected
			if (source?.bowler == null) return@launch
			_isShowingSourcePicker.value = false
			sendEvent(StatisticsOverviewScreenEvent.EditLeague(source.bowler.id, source.league?.id))
		}
	}

	private fun showSeriesPicker() {
		viewModelScope.launch {
			val source = _sourceSummaries.first()

			// Only show series picker if league is selected
			if (source?.league == null) return@launch
			_isShowingSourcePicker.value = false
			sendEvent(StatisticsOverviewScreenEvent.EditSeries(source.series?.id))
		}
	}

	private fun showGamePicker() {
		viewModelScope.launch {
			val source = _sourceSummaries.first()
			// Only show game picker if series is selected
			if (source?.series == null) return@launch
			_isShowingSourcePicker.value = false
			sendEvent(StatisticsOverviewScreenEvent.EditGame(source.game?.id))
		}
	}
}