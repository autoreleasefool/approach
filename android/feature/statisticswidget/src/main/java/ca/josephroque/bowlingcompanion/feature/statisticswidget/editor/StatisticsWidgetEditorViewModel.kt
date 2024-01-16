package ca.josephroque.bowlingcompanion.feature.statisticswidget.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.StatisticsWidgetsRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.statistics.Statistic
import ca.josephroque.bowlingcompanion.core.statistics.allStatistics
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetCreate
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetSource
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetTimeline
import ca.josephroque.bowlingcompanion.core.statistics.trackable.overall.GameAverageStatistic
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.editor.StatisticsWidgetEditorUiAction
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.editor.StatisticsWidgetEditorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StatisticsWidgetEditorViewModel @Inject constructor(
	bowlersRepository: BowlersRepository,
	leaguesRepository: LeaguesRepository,
	savedStateHandle: SavedStateHandle,
	private val statisticsWidgetsRepository: StatisticsWidgetsRepository,
): ApproachViewModel<StatisticsWidgetEditorScreenEvent>() {
	private val context = Route.StatisticsWidgetEditor.getContext(savedStateHandle)!!
	private val initialSource: StatisticsWidgetInitialSource? = Route.StatisticsWidgetEditor.getInitialSource(savedStateHandle)?.let {
		val split = it.split("_")
		when (split[0]) {
			"bowler" -> StatisticsWidgetInitialSource.Bowler(UUID.fromString(split[1]))
			else -> null
		}
	}
	private val priority = Route.StatisticsWidgetEditor.getPriority(savedStateHandle)!!

	private val _source: MutableStateFlow<StatisticsWidgetSource?> = MutableStateFlow(
		initialSource?.let {
			when (it) {
				is StatisticsWidgetInitialSource.Bowler -> StatisticsWidgetSource.Bowler(it.bowlerId)
			}
		}
	)

	private val _timeline: MutableStateFlow<StatisticsWidgetTimeline> = MutableStateFlow(
		StatisticsWidgetTimeline.THREE_MONTHS
	)

	private val _statistic: MutableStateFlow<Statistic> = MutableStateFlow(
		GameAverageStatistic()
	)

	private val _league: Flow<LeagueSummary?> = _source.flatMapLatest { source ->
		when (source) {
			is StatisticsWidgetSource.League -> leaguesRepository
				.getLeagueSummary(source.leagueId)
			is StatisticsWidgetSource.Bowler -> flowOf(null)
			null -> flowOf(null)
		}
	}

	private val _bowler: Flow<BowlerSummary?> = _source.flatMapLatest { source ->
		when (source) {
			is StatisticsWidgetSource.Bowler -> bowlersRepository
				.getBowlerSummary(source.bowlerId)
			is StatisticsWidgetSource.League -> leaguesRepository
				.getLeagueBowler(source.leagueId)
			null -> flowOf(null)
		}
	}

	val uiState: StateFlow<StatisticsWidgetEditorScreenUiState> = combine(
		_source,
		_timeline,
		_statistic,
		_bowler,
		_league,
	) { source, timeline, statistic, bowler, league ->
		StatisticsWidgetEditorScreenUiState.Loaded(
			StatisticsWidgetEditorUiState(
				source = source,
				timeline = timeline,
				statistic = statistic,
				bowler = bowler,
				league = league,
			)
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = StatisticsWidgetEditorScreenUiState.Loading,
	)

	fun handleAction(action: StatisticsWidgetEditorScreenUiAction) {
		when (action) {
			is StatisticsWidgetEditorScreenUiAction.StatisticsWidgetEditor -> handleStatisticsWidgetEditorAction(action.action)
			is StatisticsWidgetEditorScreenUiAction.UpdatedBowler -> updateSourceBowler(bowlerId = action.bowler)
			is StatisticsWidgetEditorScreenUiAction.UpdatedLeague -> updateSourceLeague(leagueId = action.league)
			is StatisticsWidgetEditorScreenUiAction.UpdatedStatistic -> _statistic.value = allStatistics().first { it.id == action.statistic }
		}
	}

	private fun handleStatisticsWidgetEditorAction(action: StatisticsWidgetEditorUiAction) {
		when (action) {
			StatisticsWidgetEditorUiAction.BackClicked -> sendEvent(StatisticsWidgetEditorScreenEvent.Dismissed)
			StatisticsWidgetEditorUiAction.SaveClicked -> saveWidget()
			is StatisticsWidgetEditorUiAction.TimelineSelected -> updateTimeline(action.timeline)
			StatisticsWidgetEditorUiAction.StatisticClicked -> sendEvent(StatisticsWidgetEditorScreenEvent.EditStatistic(_statistic.value))
			StatisticsWidgetEditorUiAction.BowlerClicked -> showBowlerPicker()
			StatisticsWidgetEditorUiAction.LeagueClicked -> showLeaguePicker()
		}
	}

	private fun saveWidget() {
		val source = _source.value ?: return

		viewModelScope.launch {
			val widget = when (source) {
				is StatisticsWidgetSource.Bowler -> StatisticsWidgetCreate(
					bowlerId = source.bowlerId,
					leagueId = null,
					id = UUID.randomUUID(),
					context = context,
					priority = priority,
					timeline = _timeline.value,
					statistic = _statistic.value.id,
				)
				is StatisticsWidgetSource.League -> StatisticsWidgetCreate(
					bowlerId = source.bowlerId,
					leagueId = source.leagueId,
					id = UUID.randomUUID(),
					context = context,
					priority = priority,
					timeline = _timeline.value,
					statistic = _statistic.value.id,
				)
			}

			statisticsWidgetsRepository.insertStatisticWidget(widget)
			sendEvent(StatisticsWidgetEditorScreenEvent.Dismissed)
		}
	}

	private fun updateSourceBowler(bowlerId: UUID?) {
		_source.value = bowlerId?.let { StatisticsWidgetSource.Bowler(it) }
	}

	private fun updateSourceLeague(leagueId: UUID?) {
		_source.value = when (val existingValue = _source.value) {
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
		_timeline.value = timeline
	}

	private fun showBowlerPicker() {
		viewModelScope.launch {
			val bowlerId = _bowler.firstOrNull()?.id
			sendEvent(StatisticsWidgetEditorScreenEvent.EditBowler(bowlerId))
		}
	}

	private fun showLeaguePicker() {
		viewModelScope.launch {
			val bowlerId = _bowler.first()?.id ?: return@launch
			val leagueId = _league.firstOrNull()?.id
			sendEvent(StatisticsWidgetEditorScreenEvent.EditLeague(bowlerId, leagueId))
		}
	}
}