package ca.josephroque.bowlingcompanion.feature.leaguedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.SeriesItemSize
import ca.josephroque.bowlingcompanion.core.model.SeriesListItem
import ca.josephroque.bowlingcompanion.core.model.SeriesSortOrder
import ca.josephroque.bowlingcompanion.feature.leaguedetails.navigation.LEAGUE_ID
import ca.josephroque.bowlingcompanion.feature.leaguedetails.ui.LeagueDetailsTopBarUiState
import ca.josephroque.bowlingcompanion.feature.leaguedetails.ui.LeagueDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.leaguedetails.ui.LeagueDetailsUiState
import ca.josephroque.bowlingcompanion.feature.serieslist.ui.SeriesListChartItem
import ca.josephroque.bowlingcompanion.feature.serieslist.ui.SeriesListUiAction
import ca.josephroque.bowlingcompanion.feature.serieslist.ui.SeriesListUiState
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LeagueDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	leaguesRepository: LeaguesRepository,
	private val seriesRepository: SeriesRepository,
	private val userDataRepository: UserDataRepository,
): ApproachViewModel<LeagueDetailsScreenEvent>() {
	private val leagueId = UUID.fromString(savedStateHandle[LEAGUE_ID])

	private val _seriesItemSize = userDataRepository.userData.map { it.seriesItemSize }
	private val _seriesToArchive: MutableStateFlow<SeriesListChartItem?> = MutableStateFlow(null)
	private val _isSeriesSortOrderShowing: MutableStateFlow<Boolean> = MutableStateFlow(false)
	private val _seriesSortOrder: MutableStateFlow<SeriesSortOrder> = MutableStateFlow(SeriesSortOrder.NEWEST_TO_OLDEST)

	private val _seriesChartModelProducers: MutableMap<UUID, ChartEntryModelProducer> = mutableMapOf()

	private val _config = combine(
		_seriesItemSize,
		_seriesToArchive,
		_isSeriesSortOrderShowing,
		_seriesSortOrder,
	) { seriesItemSize, seriesToArchive, isSeriesSortOrderShowing, seriesSortOrder ->
		Config(
			seriesItemSize = seriesItemSize,
			seriesToArchive = seriesToArchive,
			isSeriesSortOrderShowing = isSeriesSortOrderShowing,
			seriesSortOrder = seriesSortOrder,
		)
	}

	private val _seriesList = _seriesSortOrder.flatMapLatest { sortOrder ->
		seriesRepository.getSeriesList(leagueId, sortOrder)
	}

	val uiState: StateFlow<LeagueDetailsScreenUiState> = combine(
		_config,
		leaguesRepository.getLeagueDetails(leagueId),
		_seriesList,
	) { config, league, series ->
		LeagueDetailsScreenUiState.Loaded(
			leagueDetails = LeagueDetailsUiState(
				topBar = LeagueDetailsTopBarUiState(
					leagueName = league.name,
					isSortOrderMenuExpanded = config.isSeriesSortOrderShowing,
					sortOrder = config.seriesSortOrder,
					seriesItemSize = config.seriesItemSize,
				),
				seriesList = SeriesListUiState(
					list = series.map {
						val chartModelProducer = _seriesChartModelProducers.getOrPut(it.properties.id) { ChartEntryModelProducer() }
						chartModelProducer.setEntries(
							it.scores.mapIndexed { index, value -> entryOf(index.toFloat(), value.toFloat()) }
						)
						it.withChart(chartModelProducer)
					},
					seriesToArchive = config.seriesToArchive,
					itemSize = config.seriesItemSize,
				)
			),
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = LeagueDetailsScreenUiState.Loading,
	)

	fun handleAction(action: LeagueDetailsScreenUiAction) {
		when (action) {
			is LeagueDetailsScreenUiAction.LeagueDetails -> handleLeagueDetailsAction(action.action)
		}
	}

	private fun handleLeagueDetailsAction(action: LeagueDetailsUiAction) {
		when (action) {
			LeagueDetailsUiAction.BackClicked -> sendEvent(LeagueDetailsScreenEvent.Dismissed)
			LeagueDetailsUiAction.AddSeriesClicked -> sendEvent(LeagueDetailsScreenEvent.AddSeries(leagueId))
			is LeagueDetailsUiAction.SeriesList -> handleSeriesListAction(action.action)
			LeagueDetailsUiAction.SortClicked -> _isSeriesSortOrderShowing.value = true
			LeagueDetailsUiAction.SortDismissed -> _isSeriesSortOrderShowing.value = false
			is LeagueDetailsUiAction.SortOrderClicked -> {
				_seriesSortOrder.value = action.sortOrder
				_isSeriesSortOrderShowing.value = false
			}
			is LeagueDetailsUiAction.SeriesItemSizeToggled -> viewModelScope.launch {
				userDataRepository.setSeriesItemSize(action.itemSize)
			}
		}
	}

	private fun handleSeriesListAction(action: SeriesListUiAction) {
		when (action) {
			is SeriesListUiAction.SeriesClicked -> sendEvent(LeagueDetailsScreenEvent.ShowSeriesDetails(action.id))
			is SeriesListUiAction.EditSeriesClicked -> sendEvent(LeagueDetailsScreenEvent.EditSeries(action.id))
			SeriesListUiAction.AddSeriesClicked -> sendEvent(LeagueDetailsScreenEvent.AddSeries(leagueId))
			is SeriesListUiAction.ArchiveSeriesClicked -> _seriesToArchive.value = action.series
			SeriesListUiAction.ConfirmArchiveClicked -> archiveSeries()
			SeriesListUiAction.DismissArchiveClicked -> _seriesToArchive.value = null
		}
	}

	private fun archiveSeries() {
		val seriesToArchive = _seriesToArchive.value ?: return
		viewModelScope.launch {
			seriesRepository.archiveSeries(seriesToArchive.id)
			_seriesToArchive.value = null
		}
	}
}

private fun SeriesListItem.withChart(chartModelProducer: ChartEntryModelProducer): SeriesListChartItem = SeriesListChartItem(
	id = properties.id,
	date = properties.date,
	preBowl = properties.preBowl,
	total = properties.total,
	numberOfGames = scores.size,
	lowestScore = scores.minOrNull() ?: 0,
	highestScore = scores.maxOrNull() ?: 0,
	scores = chartModelProducer,
)

private data class Config(
	val seriesItemSize: SeriesItemSize,
	val seriesToArchive: SeriesListChartItem?,
	val isSeriesSortOrderShowing: Boolean,
	val seriesSortOrder: SeriesSortOrder,
)