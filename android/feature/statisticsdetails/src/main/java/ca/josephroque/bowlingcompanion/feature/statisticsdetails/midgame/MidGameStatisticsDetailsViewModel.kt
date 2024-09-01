package ca.josephroque.bowlingcompanion.feature.statisticsdetails.midgame

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.dispatcher.di.ApplicationScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.StatisticsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.StatisticsDetailsSourceType
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.MidGameStatisticsDetailsTopBarUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MidGameStatisticsDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	statisticsRepository: StatisticsRepository,
	private val userDataRepository: UserDataRepository,
	@ApplicationScope private val scope: CoroutineScope,
) : ApproachViewModel<MidGameStatisticsDetailsScreenEvent>() {
	private val sourceType = Route.MidGameStatisticsDetails.getSourceType(savedStateHandle)!!
	private val sourceId = Route.MidGameStatisticsDetails.getSourceId(savedStateHandle)
		?: UUID.randomUUID()
	private val initialFilterSource = when (sourceType) {
		StatisticsDetailsSourceType.BOWLER -> TrackableFilter.Source.Bowler(BowlerID(sourceId))
		StatisticsDetailsSourceType.LEAGUE -> TrackableFilter.Source.League(LeagueID(sourceId))
		StatisticsDetailsSourceType.SERIES -> TrackableFilter.Source.Series(SeriesID(sourceId))
		StatisticsDetailsSourceType.GAME -> TrackableFilter.Source.Game(GameID(sourceId))
	}

	private val filter: MutableStateFlow<TrackableFilter> =
		MutableStateFlow(TrackableFilter(source = initialFilterSource))

	private val sourceSummaries = filter.map {
		statisticsRepository.getSourceDetails(it.source)
	}

	private data class StatisticsSettings(
		val isHidingZeroStatistics: Boolean,
		val isHidingStatisticDescriptions: Boolean,
	)
	private val statisticsSettings: Flow<StatisticsSettings> =
		userDataRepository.userData.map {
			StatisticsSettings(
				isHidingZeroStatistics = !it.isShowingZeroStatistics,
				isHidingStatisticDescriptions = it.isHidingStatisticDescriptions,
			)
		}

	private val statisticsList = combine(
		filter,
		statisticsSettings,
	) { filter, _ ->
		statisticsRepository.getStatisticsList(filter)
	}

	private val statisticsListState: Flow<StatisticsDetailsListUiState> = combine(
		filter,
		sourceSummaries,
		statisticsList,
		statisticsSettings,
	) { filter, sourceSummaries, statistics, settings ->
		StatisticsDetailsListUiState(
			filter = filter,
			filterSources = sourceSummaries,
			statistics = statistics,
			isHidingZeroStatistics = settings.isHidingZeroStatistics,
			isHidingStatisticDescriptions = settings.isHidingStatisticDescriptions,
			isShowingTitle = false,
		)
	}

	val uiState: StateFlow<MidGameStatisticsDetailsScreenUiState> = statisticsListState.map {
		MidGameStatisticsDetailsScreenUiState.Loaded(it)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = MidGameStatisticsDetailsScreenUiState.Loading,
	)

	fun handleAction(action: MidGameStatisticsDetailsScreenUiAction) {
		when (action) {
			MidGameStatisticsDetailsScreenUiAction.OnDismissed ->
				hasSeenAllStatistics()
			is MidGameStatisticsDetailsScreenUiAction.TopBar ->
				handleTopBarAction(action.action)
			is MidGameStatisticsDetailsScreenUiAction.List ->
				handleListAction(action.action)
		}
	}

	private fun handleTopBarAction(action: MidGameStatisticsDetailsTopBarUiAction) {
		when (action) {
			MidGameStatisticsDetailsTopBarUiAction.BackClicked ->
				sendEvent(MidGameStatisticsDetailsScreenEvent.Dismissed)
		}
	}

	private fun handleListAction(action: StatisticsDetailsListUiAction) {
		when (action) {
			is StatisticsDetailsListUiAction.StatisticClicked -> Unit
			is StatisticsDetailsListUiAction.HidingZeroStatisticsToggled ->
				toggleHidingZeroStatistics(action.newValue)
			is StatisticsDetailsListUiAction.HidingStatisticDescriptionsToggled ->
				toggleHidingStatisticDescriptions(action.newValue)
			is StatisticsDetailsListUiAction.TapToViewChartTipDismissed -> Unit
		}
	}

	private fun toggleHidingZeroStatistics(newValue: Boolean) {
		viewModelScope.launch {
			userDataRepository.setIsHidingZeroStatistics(newValue)
		}
	}

	private fun toggleHidingStatisticDescriptions(newValue: Boolean) {
		viewModelScope.launch {
			userDataRepository.setIsHidingStatisticDescriptions(newValue)
		}
	}

	private fun hasSeenAllStatistics() {
		// TODO: Do we need to use @ApplicationScope here
		scope.launch {
			userDataRepository.setAllStatisticIDsSeen()
		}
	}
}
