package ca.josephroque.bowlingcompanion.feature.leaguedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.series.SeriesViewed
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.RecentlyUsedRepository
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.SeriesItemSize
import ca.josephroque.bowlingcompanion.core.model.SeriesListItem
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.core.model.SeriesSortOrder
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.leaguedetails.ui.LeagueDetailsTopBarUiState
import ca.josephroque.bowlingcompanion.feature.leaguedetails.ui.LeagueDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.leaguedetails.ui.LeagueDetailsUiState
import ca.josephroque.bowlingcompanion.feature.serieslist.ui.SeriesListChartItem
import ca.josephroque.bowlingcompanion.feature.serieslist.ui.SeriesListUiAction
import ca.josephroque.bowlingcompanion.feature.serieslist.ui.SeriesListUiState
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class LeagueDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	leaguesRepository: LeaguesRepository,
	private val seriesRepository: SeriesRepository,
	private val userDataRepository: UserDataRepository,
	private val recentlyUsedRepository: RecentlyUsedRepository,
	private val analyticsClient: AnalyticsClient,
) : ApproachViewModel<LeagueDetailsScreenEvent>() {
	private val leagueId = Route.LeagueDetails.getLeague(savedStateHandle)!!

	private val seriesItemSize = userDataRepository.userData.map { it.seriesItemSize }
	private val seriesToArchive: MutableStateFlow<SeriesListChartItem?> = MutableStateFlow(null)
	private val isSeriesSortOrderShowing: MutableStateFlow<Boolean> = MutableStateFlow(false)
	private val seriesSortOrder: MutableStateFlow<SeriesSortOrder> =
		MutableStateFlow(SeriesSortOrder.NEWEST_TO_OLDEST)

	private val seriesChartModelProducers: MutableMap<UUID, ChartEntryModelProducer> = mutableMapOf()

	private val config = combine(
		seriesItemSize,
		seriesToArchive,
		isSeriesSortOrderShowing,
		seriesSortOrder,
	) { seriesItemSize, seriesToArchive, isSeriesSortOrderShowing, seriesSortOrder ->
		Config(
			seriesItemSize = seriesItemSize,
			seriesToArchive = seriesToArchive,
			isSeriesSortOrderShowing = isSeriesSortOrderShowing,
			seriesSortOrder = seriesSortOrder,
		)
	}

	private val seriesList = seriesSortOrder.flatMapLatest { sortOrder ->
		seriesRepository.getSeriesList(leagueId, sortOrder, null)
	}

	val uiState: StateFlow<LeagueDetailsScreenUiState> = combine(
		config,
		leaguesRepository.getLeagueDetails(leagueId),
		seriesList,
	) { config, league, series ->
		val (preBowlSeries, regularSeries) = buildSeriesLists(series, config.seriesSortOrder)

		LeagueDetailsScreenUiState.Loaded(
			leagueDetails = LeagueDetailsUiState(
				topBar = LeagueDetailsTopBarUiState(
					leagueName = league.name,
					isSortOrderMenuVisible = series.isNotEmpty(),
					isSortOrderMenuExpanded = config.isSeriesSortOrderShowing,
					sortOrder = config.seriesSortOrder,
					isSeriesItemSizeVisible = series.isNotEmpty(),
					seriesItemSize = config.seriesItemSize,
				),
				seriesList = SeriesListUiState(
					preBowlSeries = preBowlSeries,
					regularSeries = regularSeries,
					seriesToArchive = config.seriesToArchive,
					itemSize = config.seriesItemSize,
				),
			),
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = LeagueDetailsScreenUiState.Loading,
	)

	init {
		viewModelScope.launch {
			recentlyUsedRepository.didRecentlyUseLeague(leagueId)
		}
	}

	fun handleAction(action: LeagueDetailsScreenUiAction) {
		when (action) {
			is LeagueDetailsScreenUiAction.SeriesAdded -> showSeriesDetails(action.seriesId)
			is LeagueDetailsScreenUiAction.LeagueDetails -> handleLeagueDetailsAction(action.action)
		}
	}

	private fun handleLeagueDetailsAction(action: LeagueDetailsUiAction) {
		when (action) {
			LeagueDetailsUiAction.BackClicked -> sendEvent(LeagueDetailsScreenEvent.Dismissed)
			LeagueDetailsUiAction.AddSeriesClicked -> sendEvent(LeagueDetailsScreenEvent.AddSeries(leagueId))
			is LeagueDetailsUiAction.SeriesList -> handleSeriesListAction(action.action)
			LeagueDetailsUiAction.SortClicked -> isSeriesSortOrderShowing.value = true
			LeagueDetailsUiAction.SortDismissed -> isSeriesSortOrderShowing.value = false
			is LeagueDetailsUiAction.SortOrderClicked -> {
				seriesSortOrder.value = action.sortOrder
				isSeriesSortOrderShowing.value = false
			}
			is LeagueDetailsUiAction.SeriesItemSizeToggled -> viewModelScope.launch {
				userDataRepository.setSeriesItemSize(action.itemSize)
			}
		}
	}

	private fun handleSeriesListAction(action: SeriesListUiAction) {
		when (action) {
			is SeriesListUiAction.SeriesClicked -> showSeriesDetails(action.id)
			is SeriesListUiAction.EditSeriesClicked -> sendEvent(
				LeagueDetailsScreenEvent.EditSeries(action.id),
			)
			SeriesListUiAction.UsePreBowlClicked -> sendEvent(LeagueDetailsScreenEvent.UsePreBowl(leagueId))
			SeriesListUiAction.AddSeriesClicked -> sendEvent(LeagueDetailsScreenEvent.AddSeries(leagueId))
			is SeriesListUiAction.ArchiveSeriesClicked -> seriesToArchive.value = action.series
			SeriesListUiAction.ConfirmArchiveClicked -> archiveSeries()
			SeriesListUiAction.DismissArchiveClicked -> seriesToArchive.value = null
		}
	}

	private fun showSeriesDetails(seriesId: UUID) {
		sendEvent(LeagueDetailsScreenEvent.ShowSeriesDetails(seriesId))
		analyticsClient.trackEvent(SeriesViewed)
	}

	private fun archiveSeries() {
		val seriesToArchive = seriesToArchive.value ?: return
		viewModelScope.launch {
			seriesRepository.archiveSeries(seriesToArchive.id)
			this@LeagueDetailsViewModel.seriesToArchive.value = null
		}
	}

	private fun buildSeriesLists(
		list: List<SeriesListItem>,
		sortOrder: SeriesSortOrder,
	): Pair<List<SeriesListChartItem>, List<SeriesListChartItem>> {
		return when (sortOrder) {
			SeriesSortOrder.NEWEST_TO_OLDEST -> {
				val preBowlSeries = list.filter {
					when (it.properties.preBowl) {
						SeriesPreBowl.PRE_BOWL -> it.properties.appliedDate == null
						SeriesPreBowl.REGULAR -> false
					}
				}
				val preBowlSeriesIds = preBowlSeries.map { it.properties.id }.toSet()
				val regularSeries = list.filter { !preBowlSeriesIds.contains(it.properties.id) }

				preBowlSeries.map(::buildSeriesListChartItem) to regularSeries.map(::buildSeriesListChartItem)
			}
			SeriesSortOrder.OLDEST_TO_NEWEST,
			SeriesSortOrder.HIGHEST_TO_LOWEST,
			SeriesSortOrder.LOWEST_TO_HIGHEST,
			-> {
				emptyList<SeriesListChartItem>() to list.map(::buildSeriesListChartItem)
			}
		}
	}

	private fun buildSeriesListChartItem(item: SeriesListItem): SeriesListChartItem {
		val chartModelProducer = seriesChartModelProducers.getOrPut(item.properties.id) {
			ChartEntryModelProducer(
				item.scores.mapIndexed { index, value -> entryOf(index.toFloat(), value.toFloat()) },
			)
		}

		return if (item.scores.all { it == 0 } || item.scores.size == 1) {
			item.withoutChart()
		} else {
			item.withChart(chartModelProducer)
		}
	}
}

private fun SeriesListItem.withoutChart(): SeriesListChartItem = SeriesListChartItem(
	id = properties.id,
	date = properties.date,
	appliedDate = properties.appliedDate,
	preBowl = properties.preBowl,
	total = properties.total,
	numberOfGames = scores.size,
	lowestScore = scores.minOrNull() ?: 0,
	highestScore = scores.maxOrNull() ?: 0,
	scores = null,
)

private fun SeriesListItem.withChart(
	chartModelProducer: ChartEntryModelProducer,
): SeriesListChartItem = SeriesListChartItem(
	id = properties.id,
	date = properties.date,
	appliedDate = properties.appliedDate,
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
