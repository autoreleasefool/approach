package ca.josephroque.bowlingcompanion.feature.statisticsoverview

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.dispatcher.di.ApplicationScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.StatisticsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.SourcePickerUiState
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.StatisticsOverviewUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.StatisticsOverviewUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
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
	private val userDataRepository: UserDataRepository,
	@ApplicationScope private val externalScope: CoroutineScope,
): ApproachViewModel<StatisticsOverviewScreenEvent>() {
	private var _didLoadDefaultSource = false
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
			is StatisticsOverviewScreenUiAction.LoadDefaultSource -> loadDefaultSource()
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
			StatisticsOverviewUiAction.SourcePickerDismissed -> setSourcePicker(isVisible = false)
			StatisticsOverviewUiAction.ViewMoreClicked -> setSourcePicker(isVisible = true)
			StatisticsOverviewUiAction.ApplyFilterClicked -> showDetailedStatistics()
			is StatisticsOverviewUiAction.SourcePickerBowlerClicked -> showBowlerPicker()
			is StatisticsOverviewUiAction.SourcePickerLeagueClicked -> showLeaguePicker()
			is StatisticsOverviewUiAction.SourcePickerSeriesClicked -> showSeriesPicker()
			is StatisticsOverviewUiAction.SourcePickerGameClicked -> showGamePicker()
		}
	}

	private fun showDetailedStatistics() {
		val source = _source.value ?: return
		setSourcePicker(isVisible = false)
		sendEvent(StatisticsOverviewScreenEvent.ShowStatistics(
			TrackableFilter(source = source)
		))

		externalScope.launch {
			userDataRepository.setLastTrackableFilterSource(source)
		}
	}

	private fun loadDefaultSource() {
		if (_didLoadDefaultSource) return
		_didLoadDefaultSource = true
		viewModelScope.launch {
			val defaultSource = userDataRepository.userData.first().lastTrackableFilter
			_source.value = defaultSource
		}
	}

	private fun setSourcePicker(isVisible: Boolean) {
		_isShowingSourcePicker.value = isVisible
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
			val bowler = source?.bowler ?: return@launch

			_isShowingSourcePicker.value = false
			sendEvent(StatisticsOverviewScreenEvent.EditLeague(bowler.id, source.league?.id))
		}
	}

	private fun showSeriesPicker() {
		viewModelScope.launch {
			val source = _sourceSummaries.first()

			// Only show series picker if league is selected
			val league = source?.league ?: return@launch

			_isShowingSourcePicker.value = false
			sendEvent(StatisticsOverviewScreenEvent.EditSeries(league.id, source.series?.id))
		}
	}

	private fun showGamePicker() {
		viewModelScope.launch {
			val source = _sourceSummaries.first()

			// Only show game picker if series is selected
			val series = source?.series ?: return@launch

			_isShowingSourcePicker.value = false
			sendEvent(StatisticsOverviewScreenEvent.EditGame(series.id, source.game?.id))
		}
	}
}