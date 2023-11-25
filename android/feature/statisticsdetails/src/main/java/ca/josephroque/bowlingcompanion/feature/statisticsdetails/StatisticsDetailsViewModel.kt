package ca.josephroque.bowlingcompanion.feature.statisticsdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.StatisticsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.statistics.TrackableFilter
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation.SOURCE_ID
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.navigation.SOURCE_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StatisticsDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	statisticsRepository: StatisticsRepository,
	private val userDataRepository: UserDataRepository,
): ViewModel() {
	private val _sourceType = savedStateHandle.get<String>(SOURCE_TYPE)
		?.let { SourceType.valueOf(it) } ?: SourceType.BOWLER

	private val _sourceId = savedStateHandle.get<String>(SOURCE_ID)
		?.let { UUID.fromString(it) } ?: UUID.randomUUID()

	private val _filter: MutableStateFlow<TrackableFilter> =
		MutableStateFlow(TrackableFilter(source = when (_sourceType) {
			SourceType.BOWLER -> TrackableFilter.Source.Bowler(_sourceId)
			SourceType.LEAGUE -> TrackableFilter.Source.League(_sourceId)
			SourceType.SERIES -> TrackableFilter.Source.Series(_sourceId)
			SourceType.GAME -> TrackableFilter.Source.Game(_sourceId)
		}))

	private val _statisticsList = _filter.map { statisticsRepository.getStatisticsList(it) }

	private val _highlightedEntry: MutableStateFlow<Int?> = MutableStateFlow(null)

	private val _statistics: Flow<StatisticsDetailsListUiState> = combine(
		_statisticsList,
		_highlightedEntry,
		userDataRepository.userData,
	) { statistics, highlightedEntry, userData ->
		StatisticsDetailsListUiState(
			statistics = statistics,
			highlightedEntry = highlightedEntry,
			isHidingZeroStatistics = !userData.isShowingZeroStatistics,
		)
	}

	val uiState: StateFlow<StatisticsDetailsScreenUiState> = _statistics.map {
		StatisticsDetailsScreenUiState.Loaded(list = it)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = StatisticsDetailsScreenUiState.Loading,
	)

	private val _events: MutableStateFlow<StatisticsDetailsScreenEvent?> = MutableStateFlow(null)
	val events = _events.asStateFlow()

	fun handleAction(action: StatisticsDetailsScreenUiAction) {
		when (action) {
			is StatisticsDetailsScreenUiAction.StatisticsDetailsListAction -> handleListAction(action.action)
		}
	}

	private fun handleListAction(action: StatisticsDetailsListUiAction) {
		when (action) {
			is StatisticsDetailsListUiAction.StatisticClicked ->
				showStatisticChart(statistic = action.title)
			is StatisticsDetailsListUiAction.HidingZeroStatisticsToggled ->
				toggleHidingZeroStatistics(action.newValue)
			is StatisticsDetailsListUiAction.BackClicked ->
				_events.value = StatisticsDetailsScreenEvent.Dismissed
		}
	}

	private fun showStatisticChart(statistic: Int) {
		_highlightedEntry.value = statistic
	}

	private fun toggleHidingZeroStatistics(newValue: Boolean?) {
		viewModelScope.launch {
			val currentValue = !userDataRepository.userData.first().isShowingZeroStatistics
			userDataRepository.setIsHidingZeroStatistics(newValue ?: !currentValue)
		}
	}
}

enum class SourceType {
	BOWLER,
	LEAGUE,
	SERIES,
	GAME,
}

fun TrackableFilter.Source.sourceType(): SourceType = when (this) {
	is TrackableFilter.Source.Bowler -> SourceType.BOWLER
	is TrackableFilter.Source.League -> SourceType.LEAGUE
	is TrackableFilter.Source.Series -> SourceType.SERIES
	is TrackableFilter.Source.Game -> SourceType.GAME
}