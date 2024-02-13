package ca.josephroque.bowlingcompanion.feature.statisticsdetails

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.statistics.StatisticViewed
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.StatisticsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.StatisticsDetailsSourceType
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.statistics.allStatistics
import ca.josephroque.bowlingcompanion.core.statistics.charts.utils.getModelEntries
import ca.josephroque.bowlingcompanion.core.statistics.statisticInstanceFromID
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart.StatisticsDetailsChartUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart.StatisticsDetailsChartUiState
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.list.StatisticsDetailsListUiState
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StatisticsDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	statisticsRepository: StatisticsRepository,
	private val userDataRepository: UserDataRepository,
	private val analyticsClient: AnalyticsClient,
): ApproachViewModel<StatisticsDetailsScreenEvent>(), DefaultLifecycleObserver {
	private val _sourceType = Route.StatisticsDetails.getSourceType(savedStateHandle)!!
	private val _sourceId = Route.StatisticsDetails.getSourceId(savedStateHandle) ?: UUID.randomUUID()
	private val _initialFilterSource = when (_sourceType) {
		StatisticsDetailsSourceType.BOWLER -> TrackableFilter.Source.Bowler(_sourceId)
		StatisticsDetailsSourceType.LEAGUE -> TrackableFilter.Source.League(_sourceId)
		StatisticsDetailsSourceType.SERIES -> TrackableFilter.Source.Series(_sourceId)
		StatisticsDetailsSourceType.GAME -> TrackableFilter.Source.Game(_sourceId)
	}

	private val _chartEntryModelProducer = ChartEntryModelProducer()

	private val _filter: MutableStateFlow<TrackableFilter> =
		MutableStateFlow(TrackableFilter(source = _initialFilterSource))

	private val _sourceSummaries = _filter.map {
		statisticsRepository.getSourceDetails(it.source)
	}

	private val _headerPeekHeight = MutableStateFlow(0f)

	@OptIn(ExperimentalMaterial3Api::class)
	private val _bottomSheetValue = MutableStateFlow(SheetValue.PartiallyExpanded)

	private data class StatisticsSettings(
		val isHidingZeroStatistics: Boolean,
		val isHidingStatisticDescriptions: Boolean,
	)
	private val _statisticsSettings: Flow<StatisticsSettings> =
		userDataRepository.userData.map {
			StatisticsSettings(
				isHidingZeroStatistics = !it.isShowingZeroStatistics,
				isHidingStatisticDescriptions = it.isHidingStatisticDescriptions,
			)
		}

	private val _statisticsList = combine(
		_filter,
		_statisticsSettings,
	) { filter, _ ->
		statisticsRepository.getStatisticsList(filter)
	}

	private val _selectedStatistic: MutableStateFlow<StatisticID?> = MutableStateFlow(
		allStatistics(source = _initialFilterSource).firstOrNull()?.id
	)

	private val _chartContent: Flow<StatisticsDetailsChartUiState.ChartContent?> = combine(
		_filter,
		_selectedStatistic,
	) { filter, selectedStatistic ->
		if (selectedStatistic == null) return@combine null
		val chart = statisticsRepository.getStatisticsChart(
			statistic = statisticInstanceFromID(selectedStatistic),
			filter = filter,
		)

		StatisticsDetailsChartUiState.ChartContent(
			chart = chart,
			modelProducer = _chartEntryModelProducer,
		)
	}

	private val _statisticsChartState: Flow<StatisticsDetailsChartUiState> = combine(
		_filter,
		_selectedStatistic,
		_chartContent
	) { filter, selectedStatistic, chartContent ->
		if (chartContent == null || selectedStatistic == null)
			StatisticsDetailsChartUiState(
				filter = filter,
				isLoadingNextChart = true,
				isFilterTooNarrow = false,
				chartContent = null,
				supportsAggregation = false,
			)
		else
			StatisticsDetailsChartUiState(
				filter = filter,
				isLoadingNextChart = false,
				isFilterTooNarrow = false,
				chartContent = chartContent,
				supportsAggregation = statisticInstanceFromID(selectedStatistic).supportsAggregation,
			)
	}

	private val _statisticsListState: Flow<StatisticsDetailsListUiState> = combine(
		_filter,
		_sourceSummaries,
		_statisticsList,
		_selectedStatistic,
		_statisticsSettings,
	) { filter, sourceSummaries, statistics, selectedStatistic, settings ->
		StatisticsDetailsListUiState(
			filter = filter,
			filterSources = sourceSummaries,
			statistics = statistics,
			highlightedEntry = selectedStatistic,
			isHidingZeroStatistics = settings.isHidingZeroStatistics,
			isHidingStatisticDescriptions = settings.isHidingStatisticDescriptions,
		)
	}

	@OptIn(ExperimentalMaterial3Api::class)
	val uiState: StateFlow<StatisticsDetailsScreenUiState> = combine(
		_statisticsListState,
		_statisticsChartState,
		_headerPeekHeight,
		_bottomSheetValue,
	) { statisticsList, statisticsChart, headerPeekHeight, bottomSheetValue ->
		StatisticsDetailsScreenUiState.Loaded(
			list = statisticsList,
			chart = statisticsChart,
			headerPeekHeight = headerPeekHeight,
			bottomSheetValue = bottomSheetValue,
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = StatisticsDetailsScreenUiState.Loading,
	)

	init {
		viewModelScope.launch {
			_chartContent.collect {
				if (it != null) {
					_chartEntryModelProducer.setEntries(it.chart.getModelEntries())
				}
			}
		}
	}

	@OptIn(ExperimentalMaterial3Api::class)
	override fun onResume(owner: LifecycleOwner) {
		_bottomSheetValue.value = SheetValue.PartiallyExpanded
	}

	fun handleAction(action: StatisticsDetailsScreenUiAction) {
		when (action) {
			is StatisticsDetailsScreenUiAction.Chart -> handleChartAction(action.action)
			is StatisticsDetailsScreenUiAction.List -> handleListAction(action.action)
			is StatisticsDetailsScreenUiAction.TopBar -> handleTopBarAction(action.action)
			is StatisticsDetailsScreenUiAction.BottomSheet -> handleBottomSheetAction(action.action)
		}
	}

	private fun handleListAction(action: StatisticsDetailsListUiAction) {
		when (action) {
			is StatisticsDetailsListUiAction.StatisticClicked ->
				showStatisticChart(statistic = action.id)
			is StatisticsDetailsListUiAction.HidingZeroStatisticsToggled ->
				toggleHidingZeroStatistics(action.newValue)
			is StatisticsDetailsListUiAction.HidingStatisticDescriptionsToggled ->
				toggleHidingStatisticDescriptions(action.newValue)
			is StatisticsDetailsListUiAction.HeaderHeightMeasured -> setHeaderPeekHeight(action.height)
		}
	}

	private fun handleTopBarAction(action: StatisticsDetailsTopBarUiAction) {
		when (action) {
			StatisticsDetailsTopBarUiAction.BackClicked -> sendEvent(StatisticsDetailsScreenEvent.Dismissed)
		}
	}

	private fun handleChartAction(action: StatisticsDetailsChartUiAction) {
		when (action) {
			is StatisticsDetailsChartUiAction.AggregationChanged ->
				toggleAggregation(action.newValue)
		}
	}

	@OptIn(ExperimentalMaterial3Api::class)
	private fun handleBottomSheetAction(action: StatisticsDetailsBottomSheetUiAction) {
		when (action) {
			is StatisticsDetailsBottomSheetUiAction.SheetValueChanged ->
				_bottomSheetValue.value = action.value
		}
	}

	private fun setHeaderPeekHeight(height: Float) {
		_headerPeekHeight.value = height
	}

	suspend fun hasSeenAllStatistics() {
		userDataRepository.setAllStatisticIDsSeen()
	}

	@OptIn(ExperimentalMaterial3Api::class)
	private fun showStatisticChart(statistic: StatisticID) {
		_bottomSheetValue.value = SheetValue.PartiallyExpanded
		_selectedStatistic.value = statistic

		viewModelScope.launch {
			val userData = userDataRepository.userData.first()
			analyticsClient.trackEvent(StatisticViewed(
				statisticName = statistic.name,
				countsH2AsH = !userData.isCountingH2AsHDisabled,
				countsS2AsS = !userData.isCountingSplitWithBonusAsSplitDisabled,
			))
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

	private fun toggleAggregation(newValue: Boolean) {
		_filter.update {
			it.copy(aggregation = when (newValue) {
				true -> TrackableFilter.AggregationFilter.ACCUMULATE
				false -> TrackableFilter.AggregationFilter.PERIODIC
			})
		}
	}
}

fun TrackableFilter.Source.sourceType(): StatisticsDetailsSourceType = when (this) {
	is TrackableFilter.Source.Bowler -> StatisticsDetailsSourceType.BOWLER
	is TrackableFilter.Source.League -> StatisticsDetailsSourceType.LEAGUE
	is TrackableFilter.Source.Series -> StatisticsDetailsSourceType.SERIES
	is TrackableFilter.Source.Game -> StatisticsDetailsSourceType.GAME
}