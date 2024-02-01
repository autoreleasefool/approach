package ca.josephroque.bowlingcompanion.feature.statisticsoverview.sourcepicker

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.dispatcher.di.ApplicationScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.StatisticsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.sourcepicker.SourcePickerUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.sourcepicker.SourcePickerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SourcePickerViewModel @Inject constructor(
	private val statisticsRepository: StatisticsRepository,
	private val userDataRepository: UserDataRepository,
	@ApplicationScope private val externalScope: CoroutineScope,
): ApproachViewModel<SourcePickerScreenEvent>() {
	private var _didLoadDefaultSource = false
	private val _source: MutableStateFlow<TrackableFilter.Source?> = MutableStateFlow(null)
	private val _sourceSummaries = _source.map {
		if (it == null) {
			statisticsRepository.getDefaultSource()
		} else {
			statisticsRepository.getSourceDetails(it)
		}
	}

	val uiState = _sourceSummaries
		.map { source ->
			SourcePickerScreenUiState.Loaded(
				sourcePicker = SourcePickerUiState(
					source = source,
				),
			)
		}.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = SourcePickerScreenUiState.Loading,
		)

	fun handleAction(action: SourcePickerScreenUiAction) {
		when (action) {
			is SourcePickerScreenUiAction.DidAppear -> loadDefaultSource()
			is SourcePickerScreenUiAction.UpdatedBowler -> setFilterBowler(action.bowler)
			is SourcePickerScreenUiAction.UpdatedLeague -> setFilterLeague(action.league)
			is SourcePickerScreenUiAction.UpdatedSeries -> setFilterSeries(action.series)
			is SourcePickerScreenUiAction.UpdatedGame -> setFilterGame(action.game)
			is SourcePickerScreenUiAction.SourcePicker -> handleSourcePickerAction(action.action)
		}
	}

	private fun handleSourcePickerAction(action: SourcePickerUiAction) {
		when (action) {
			SourcePickerUiAction.Dismissed -> sendEvent(SourcePickerScreenEvent.Dismissed)
			SourcePickerUiAction.ApplyFilterClicked -> showDetailedStatistics()
			is SourcePickerUiAction.BowlerClicked -> showBowlerPicker()
			is SourcePickerUiAction.LeagueClicked -> showLeaguePicker()
			is SourcePickerUiAction.SeriesClicked -> showSeriesPicker()
			is SourcePickerUiAction.GameClicked -> showGamePicker()
		}
	}

	private fun setFilterBowler(bowlerId: UUID?) {
		bowlerId?.let {
			_source.value = TrackableFilter.Source.Bowler(it)
		}
	}

	private fun setFilterLeague(leagueId: UUID?) {
		leagueId?.let {
			_source.value = TrackableFilter.Source.League(it)
		}
	}

	private fun setFilterSeries(seriesId: UUID?) {
		seriesId?.let {
			_source.value = TrackableFilter.Source.Series(it)
		}
	}

	private fun setFilterGame(gameId: UUID?) {
		gameId?.let {
			_source.value = TrackableFilter.Source.Game(it)
		}
	}

	private fun showDetailedStatistics() {
		val source = _source.value ?: return
		sendEvent(SourcePickerScreenEvent.ShowStatistics(TrackableFilter(source = source)))

		externalScope.launch {
			userDataRepository.setLastTrackableFilterSource(source)
		}
	}

	private fun loadDefaultSource() {
		if (_didLoadDefaultSource) return
		_didLoadDefaultSource = true
		viewModelScope.launch {
			val defaultSource = userDataRepository.userData.first().lastTrackableFilter
			if (_source.value == null) {
				_source.value = defaultSource
			}
		}
	}

	private fun showBowlerPicker() {
		viewModelScope.launch {
			val source = _sourceSummaries.first()
			sendEvent(SourcePickerScreenEvent.EditBowler(source?.bowler?.id))
		}
	}

	private fun showLeaguePicker() {
		viewModelScope.launch {
			val source = _sourceSummaries.first()

			// Only show league picker if bowler is selected
			val bowler = source?.bowler ?: return@launch

			sendEvent(SourcePickerScreenEvent.EditLeague(bowler.id, source.league?.id))
		}
	}

	private fun showSeriesPicker() {
		viewModelScope.launch {
			val source = _sourceSummaries.first()

			// Only show series picker if league is selected
			val league = source?.league ?: return@launch

			sendEvent(SourcePickerScreenEvent.EditSeries(league.id, source.series?.id))
		}
	}

	private fun showGamePicker() {
		viewModelScope.launch {
			val source = _sourceSummaries.first()

			// Only show game picker if series is selected
			val series = source?.series ?: return@launch

			sendEvent(SourcePickerScreenEvent.EditGame(series.id, source.game?.id))
		}
	}
}