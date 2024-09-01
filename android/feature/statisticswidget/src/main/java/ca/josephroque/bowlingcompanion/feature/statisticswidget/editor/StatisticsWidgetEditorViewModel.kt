package ca.josephroque.bowlingcompanion.feature.statisticswidget.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.widget.WidgetCreated
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.StatisticsWidgetsRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.statistics.Statistic
import ca.josephroque.bowlingcompanion.core.statistics.allStatistics
import ca.josephroque.bowlingcompanion.core.statistics.charts.utils.getModelEntries
import ca.josephroque.bowlingcompanion.core.statistics.charts.utils.hasModelEntries
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidget
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetCreate
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetSource
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetTimeline
import ca.josephroque.bowlingcompanion.core.statistics.trackable.overall.GameAverageStatistic
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.editor.StatisticsWidgetEditorUiAction
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.editor.StatisticsWidgetEditorUiState
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class StatisticsWidgetEditorViewModel @Inject constructor(
	bowlersRepository: BowlersRepository,
	leaguesRepository: LeaguesRepository,
	savedStateHandle: SavedStateHandle,
	private val statisticsWidgetsRepository: StatisticsWidgetsRepository,
	private val analyticsClient: AnalyticsClient,
) : ApproachViewModel<StatisticsWidgetEditorScreenEvent>() {
	private val widgetId = UUID.randomUUID()
	private val context = Route.StatisticsWidgetEditor.getContext(savedStateHandle)!!
	private val initialSource: StatisticsWidgetInitialSource? =
		Route.StatisticsWidgetEditor.getInitialSource(
			savedStateHandle,
		)?.let {
			val split = it.split("_")
			when (split[0]) {
				"bowler" -> StatisticsWidgetInitialSource.Bowler(BowlerID.fromString(split[1]))
				else -> null
			}
		}
	private val priority = Route.StatisticsWidgetEditor.getPriority(savedStateHandle)!!

	private val source: MutableStateFlow<StatisticsWidgetSource?> = MutableStateFlow(
		initialSource?.let {
			when (it) {
				is StatisticsWidgetInitialSource.Bowler -> StatisticsWidgetSource.Bowler(it.bowlerId)
			}
		},
	)

	private val timeline: MutableStateFlow<StatisticsWidgetTimeline> = MutableStateFlow(
		StatisticsWidgetTimeline.THREE_MONTHS,
	)

	private val statistic: MutableStateFlow<Statistic> = MutableStateFlow(
		GameAverageStatistic(),
	)

	private val league: Flow<LeagueSummary?> = source.flatMapLatest { source ->
		when (source) {
			is StatisticsWidgetSource.League ->
				leaguesRepository
					.getLeagueSummary(source.leagueId)
			is StatisticsWidgetSource.Bowler -> flowOf(null)
			null -> flowOf(null)
		}
	}

	private val bowler: Flow<BowlerSummary?> = source.flatMapLatest { source ->
		when (source) {
			is StatisticsWidgetSource.Bowler ->
				bowlersRepository
					.getBowlerSummary(source.bowlerId)
			is StatisticsWidgetSource.League ->
				leaguesRepository
					.getLeagueBowler(source.leagueId)
			null -> flowOf(null)
		}
	}

	private val widget: Flow<StatisticsWidget?> = combine(
		source,
		timeline,
		statistic,
	) { source, timeline, statistic ->
		val widgetSource = source ?: return@combine null
		StatisticsWidget(
			id = widgetId,
			context = context,
			priority = priority,
			timeline = timeline,
			statistic = statistic.id,
			source = widgetSource,
		)
	}

	private val preview: Flow<StatisticsWidgetEditorUiState.ChartContent?> = widget.map {
		it ?: return@map null
		val chart = statisticsWidgetsRepository.getStatisticsWidgetChart(it)
		val modelProducer = if (chart.hasModelEntries()) {
			ChartEntryModelProducer(chart.getModelEntries())
		} else {
			ChartEntryModelProducer()
		}

		StatisticsWidgetEditorUiState.ChartContent(
			chart = chart,
			modelProducer = modelProducer,
		)
	}

	val uiState: StateFlow<StatisticsWidgetEditorScreenUiState> = combine(
		widget,
		statistic,
		bowler,
		league,
		preview,
	) { widget, statistic, bowler, league, preview ->
		StatisticsWidgetEditorScreenUiState.Loaded(
			StatisticsWidgetEditorUiState(
				source = widget?.source,
				timeline = widget?.timeline ?: timeline.value,
				statistic = statistic,
				bowler = bowler,
				league = league,
				widget = widget,
				preview = preview,
			),
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = StatisticsWidgetEditorScreenUiState.Loading,
	)

	fun handleAction(action: StatisticsWidgetEditorScreenUiAction) {
		when (action) {
			is StatisticsWidgetEditorScreenUiAction.StatisticsWidgetEditor ->
				handleStatisticsWidgetEditorAction(action.action)
			is StatisticsWidgetEditorScreenUiAction.UpdatedBowler ->
				updateSourceBowler(bowlerId = action.bowler)
			is StatisticsWidgetEditorScreenUiAction.UpdatedLeague ->
				updateSourceLeague(leagueId = action.league)
			is StatisticsWidgetEditorScreenUiAction.UpdatedStatistic ->
				statistic.value = allStatistics().first { it.id == action.statistic }
		}
	}

	private fun handleStatisticsWidgetEditorAction(action: StatisticsWidgetEditorUiAction) {
		when (action) {
			StatisticsWidgetEditorUiAction.BackClicked -> sendEvent(
				StatisticsWidgetEditorScreenEvent.Dismissed,
			)
			StatisticsWidgetEditorUiAction.SaveClicked -> saveWidget()
			is StatisticsWidgetEditorUiAction.TimelineSelected -> updateTimeline(action.timeline)
			StatisticsWidgetEditorUiAction.StatisticClicked -> sendEvent(
				StatisticsWidgetEditorScreenEvent.EditStatistic(statistic.value),
			)
			StatisticsWidgetEditorUiAction.BowlerClicked -> showBowlerPicker()
			StatisticsWidgetEditorUiAction.LeagueClicked -> showLeaguePicker()
		}
	}

	private fun saveWidget() {
		val source = source.value ?: return

		viewModelScope.launch {
			val timeline = timeline.value
			val statistic = statistic.value
			val widget = when (source) {
				is StatisticsWidgetSource.Bowler -> StatisticsWidgetCreate(
					bowlerId = source.bowlerId,
					leagueId = null,
					id = UUID.randomUUID(),
					context = context,
					priority = priority,
					timeline = timeline,
					statistic = statistic.id,
				)
				is StatisticsWidgetSource.League -> StatisticsWidgetCreate(
					bowlerId = source.bowlerId,
					leagueId = source.leagueId,
					id = UUID.randomUUID(),
					context = context,
					priority = priority,
					timeline = timeline,
					statistic = statistic.id,
				)
			}

			statisticsWidgetsRepository.insertStatisticWidget(widget)
			sendEvent(StatisticsWidgetEditorScreenEvent.Dismissed)

			analyticsClient.trackEvent(
				WidgetCreated(
					context = context,
					source = source.toString(),
					statistic = statistic.id.toString(),
					timeline = timeline.toString(),
				),
			)
		}
	}

	private fun updateSourceBowler(bowlerId: BowlerID?) {
		source.value = bowlerId?.let { StatisticsWidgetSource.Bowler(it) }
	}

	private fun updateSourceLeague(leagueId: LeagueID?) {
		source.value = when (val existingValue = source.value) {
			is StatisticsWidgetSource.Bowler -> if (leagueId == null) {
				StatisticsWidgetSource.Bowler(existingValue.bowlerId)
			} else {
				StatisticsWidgetSource.League(bowlerId = existingValue.bowlerId, leagueId = leagueId)
			}
			is StatisticsWidgetSource.League -> if (leagueId == null) {
				StatisticsWidgetSource.Bowler(existingValue.bowlerId)
			} else {
				StatisticsWidgetSource.League(bowlerId = existingValue.bowlerId, leagueId = leagueId)
			}
			null -> null
		}
	}

	private fun updateTimeline(timeline: StatisticsWidgetTimeline) {
		this.timeline.value = timeline
	}

	private fun showBowlerPicker() {
		viewModelScope.launch {
			val bowlerId = bowler.firstOrNull()?.id
			sendEvent(StatisticsWidgetEditorScreenEvent.EditBowler(bowlerId))
		}
	}

	private fun showLeaguePicker() {
		viewModelScope.launch {
			val bowlerId = bowler.first()?.id ?: return@launch
			val leagueId = league.firstOrNull()?.id
			sendEvent(StatisticsWidgetEditorScreenEvent.EditLeague(bowlerId, leagueId))
		}
	}
}
