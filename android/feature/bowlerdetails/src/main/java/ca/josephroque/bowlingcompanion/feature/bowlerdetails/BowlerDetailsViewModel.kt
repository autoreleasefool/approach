package ca.josephroque.bowlingcompanion.feature.bowlerdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.league.LeagueViewed
import ca.josephroque.bowlingcompanion.core.common.utils.toLocalDate
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GearRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.RecentlyUsedRepository
import ca.josephroque.bowlingcompanion.core.data.repository.StatisticsWidgetsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerDetails
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.LeagueSortOrder
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.statistics.charts.utils.getModelEntries
import ca.josephroque.bowlingcompanion.core.statistics.charts.utils.hasModelEntries
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui.BowlerDetailsTopBarUiState
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui.BowlerDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui.BowlerDetailsUiState
import ca.josephroque.bowlingcompanion.feature.gearlist.ui.GearListUiState
import ca.josephroque.bowlingcompanion.feature.leagueslist.ui.LeaguesListUiAction
import ca.josephroque.bowlingcompanion.feature.leagueslist.ui.LeaguesListUiState
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayoutUiAction
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.StatisticsWidgetLayoutUiState
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@HiltViewModel
class BowlerDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	bowlersRepository: BowlersRepository,
	userDataRepository: UserDataRepository,
	statisticsWidgetsRepository: StatisticsWidgetsRepository,
	private val gearRepository: GearRepository,
	private val leaguesRepository: LeaguesRepository,
	private val recentlyUsedRepository: RecentlyUsedRepository,
	private val analyticsClient: AnalyticsClient,
) : ApproachViewModel<BowlerDetailsScreenEvent>() {
	private val bowlerId = Route.BowlerDetails.getBowler(savedStateHandle)!!
	private val statisticsWidgetContext: String
		get() = "bowler_details_$bowlerId"

	private val leagueToArchive: MutableStateFlow<LeagueListItem?> = MutableStateFlow(null)

	private val recurrence = MutableStateFlow<LeagueRecurrence?>(null)
	private val isLeagueSortOrderShowing = MutableStateFlow(false)
	private val leagueSortOrder = MutableStateFlow(LeagueSortOrder.MOST_RECENTLY_USED)

	private val isHidingWidgets = userDataRepository.userData
		.map { it.isHidingWidgetsInLeaguesList }

	private val leagues = combine(recurrence, leagueSortOrder) { recurrence, leagueSortOrder ->
		leaguesRepository.getLeaguesList(
			bowlerId,
			recurrence = recurrence,
			sortOrder = leagueSortOrder,
		)
	}.flatMapLatest { it }

	private val leaguesListState: Flow<LeaguesListUiState> = combine(
		leagues,
		leagueToArchive,
		recurrence,
		isHidingWidgets,
	) { leaguesList, leagueToArchive, recurrence, isHidingWidgets ->
		val filter = LeaguesListUiState.Filter(recurrence = recurrence)
		LeaguesListUiState(
			list = leaguesList,
			leagueToArchive = leagueToArchive,
			isShowingHeader = !isHidingWidgets && (leaguesList.isNotEmpty() || filter.isNotEmpty),
			filter = filter,
		)
	}

	private val widgets = isHidingWidgets
		.flatMapLatest {
			if (it) {
				flowOf(null)
			} else {
				statisticsWidgetsRepository.getStatisticsWidgets(statisticsWidgetContext)
			}
		}

	private val widgetCharts: MutableStateFlow<Map<UUID, StatisticsWidgetLayoutUiState.ChartContent>> =
		MutableStateFlow(emptyMap())

	private val widgetLayoutState = combine(
		widgets,
		widgetCharts,
	) { widgets, widgetCharts ->
		widgets?.let {
			StatisticsWidgetLayoutUiState(
				widgets = it,
				widgetCharts = widgetCharts,
			)
		}
	}

	private val bowlerDetails = bowlersRepository.getBowlerDetails(bowlerId)

	private val topBarState = combine(
		bowlerDetails,
		leaguesListState,
		isLeagueSortOrderShowing,
		leagueSortOrder,
	) { bowler, leaguesList, isLeagueSortOrderShowing, leagueSortOrder ->
		BowlerDetailsTopBarUiState(
			bowlerName = bowler.name,
			isSortOrderMenuVisible = leaguesList.list.isNotEmpty(),
			isSortOrderMenuExpanded = isLeagueSortOrderShowing,
			sortOrder = leagueSortOrder,
		)
	}

	val uiState: StateFlow<BowlerDetailsScreenUiState> = combine(
		leaguesListState,
		widgetLayoutState,
		topBarState,
		bowlersRepository.getBowlerDetails(bowlerId),
		gearRepository.getBowlerPreferredGear(bowlerId),
	) { leaguesList, widgets, topBarState, bowlerDetails, gearList ->
		BowlerDetailsScreenUiState.Loaded(
			bowler = BowlerDetailsUiState(
				bowler = bowlerDetails,
				leaguesList = leaguesList,
				gearList = GearListUiState(gearList, gearToDelete = null),
				topBar = topBarState,
				widgets = widgets,
			),
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = BowlerDetailsScreenUiState.Loading,
	)

	init {
		viewModelScope.launch {
			recentlyUsedRepository.didRecentlyUseBowler(bowlerId)
		}

		viewModelScope.launch {
			widgets.collect { widgets ->
				widgets?.forEach { widget ->
					launch {
						val chart = statisticsWidgetsRepository.getStatisticsWidgetChart(widget)

						widgetCharts.update {
							val widgetChart =
								it[widget.id]?.copy(chart = chart) ?: StatisticsWidgetLayoutUiState
									.ChartContent(
										chart,
										ChartEntryModelProducer(),
									)

							if (widgetChart.chart.hasModelEntries()) {
								widgetChart.modelProducer.setEntriesSuspending(widgetChart.chart.getModelEntries()).await()
							}

							it + (widget.id to widgetChart)
						}
					}
				}
			}
		}
	}

	fun handleAction(action: BowlerDetailsScreenUiAction) {
		when (action) {
			is BowlerDetailsScreenUiAction.PreferredGearSelected -> setBowlerPreferredGear(action.gear)
			is BowlerDetailsScreenUiAction.BowlerDetailsAction -> handleBowlerDetailsAction(action.action)
		}
	}

	private fun handleBowlerDetailsAction(action: BowlerDetailsUiAction) {
		when (action) {
			BowlerDetailsUiAction.BackClicked -> sendEvent(BowlerDetailsScreenEvent.Dismissed)
			BowlerDetailsUiAction.AddLeagueClicked -> sendEvent(BowlerDetailsScreenEvent.AddLeague(bowlerId))
			BowlerDetailsUiAction.EditStatisticsWidgetClicked -> sendEvent(
				BowlerDetailsScreenEvent.EditStatisticsWidget(statisticsWidgetContext, bowlerId),
			)
			BowlerDetailsUiAction.SortClicked -> isLeagueSortOrderShowing.value = true
			BowlerDetailsUiAction.SortDismissed -> isLeagueSortOrderShowing.value = false
			is BowlerDetailsUiAction.SortOrderClicked -> {
				leagueSortOrder.value = action.sortOrder
				isLeagueSortOrderShowing.value = false
			}
			BowlerDetailsUiAction.ManageGearClicked -> showPreferredGearPicker()
			is BowlerDetailsUiAction.GearClicked -> sendEvent(
				BowlerDetailsScreenEvent.ShowGearDetails(action.id),
			)
			is BowlerDetailsUiAction.LeaguesListAction -> handleLeaguesListAction(action.action)
			is BowlerDetailsUiAction.StatisticsWidgetLayout -> handleStatisticsWidgetLayoutAction(
				action.action,
			)
		}
	}

	private fun handleLeaguesListAction(action: LeaguesListUiAction) {
		when (action) {
			LeaguesListUiAction.AddLeagueClicked -> sendEvent(BowlerDetailsScreenEvent.AddLeague(bowlerId))
			is LeaguesListUiAction.LeagueClicked -> showLeagueDetails(action.league)
			is LeaguesListUiAction.LeagueEdited -> sendEvent(
				BowlerDetailsScreenEvent.EditLeague(action.league.id),
			)
			is LeaguesListUiAction.RecurrenceClicked -> filterByRecurrence(action.recurrence)
			is LeaguesListUiAction.LeagueArchived -> setLeagueArchivePrompt(action.league)
			is LeaguesListUiAction.ConfirmArchiveClicked -> archiveLeague()
			is LeaguesListUiAction.DismissArchiveClicked -> setLeagueArchivePrompt(null)
		}
	}

	private fun handleStatisticsWidgetLayoutAction(action: StatisticsWidgetLayoutUiAction) {
		when (action) {
			is StatisticsWidgetLayoutUiAction.WidgetClicked ->
				when (widgetCharts.value[action.widget.id]?.chart) {
					is StatisticChartContent.CountableChart,
					is StatisticChartContent.AveragingChart,
					is StatisticChartContent.PercentageChart,
					null,
					-> sendEvent(
						BowlerDetailsScreenEvent.ShowWidgetStatistics(
							action.widget.filter(Clock.System.now().toLocalDate()),
						),
					)
					is StatisticChartContent.ChartUnavailable ->
						sendEvent(BowlerDetailsScreenEvent.ShowWidgetUnavailableError)
					is StatisticChartContent.DataMissing ->
						sendEvent(BowlerDetailsScreenEvent.ShowWidgetNotEnoughDataError)
				}
			is StatisticsWidgetLayoutUiAction.ChangeLayoutClicked -> sendEvent(
				BowlerDetailsScreenEvent.EditStatisticsWidget(statisticsWidgetContext, bowlerId),
			)
		}
	}

	private fun filterByRecurrence(filter: LeagueRecurrence?) {
		recurrence.update {
			if (filter == null || it == filter) null else filter
		}
	}

	private fun setBowlerPreferredGear(gear: Set<UUID>) {
		viewModelScope.launch {
			gearRepository.setBowlerPreferredGear(bowlerId, gear)
		}
	}

	private fun setLeagueArchivePrompt(league: LeagueListItem?) {
		leagueToArchive.value = league
	}

	private fun archiveLeague() {
		val leagueToArchive = leagueToArchive.value ?: return
		viewModelScope.launch {
			leaguesRepository.archiveLeague(leagueToArchive.id)
			setLeagueArchivePrompt(null)
		}
	}

	private fun showPreferredGearPicker() {
		val selectedGear =
			(uiState.value as? BowlerDetailsScreenUiState.Loaded)?.bowler?.gearList?.list?.map {
				it.id
			}?.toSet()
				?: return
		sendEvent(BowlerDetailsScreenEvent.ShowPreferredGearPicker(selectedGear))
	}

	private fun showLeagueDetails(league: LeagueListItem) {
		when (league.recurrence) {
			LeagueRecurrence.REPEATING -> sendEvent(BowlerDetailsScreenEvent.ShowLeagueDetails(league.id))
			LeagueRecurrence.ONCE -> sendEvent(BowlerDetailsScreenEvent.ShowEventDetails(league.id))
		}
		analyticsClient.trackEvent(LeagueViewed)
	}
}
