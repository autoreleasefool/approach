package ca.josephroque.bowlingcompanion.feature.statisticswidget.editor

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultKey
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultViewModel
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.editor.StatisticsWidgetEditor
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.editor.StatisticsWidgetEditorTopBar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun StatisticsWidgetEditorRoute(
	onBackPressed: () -> Unit,
	onPickBowler: (BowlerID?, ResourcePickerResultKey) -> Unit,
	onPickLeague: (BowlerID, LeagueID?, ResourcePickerResultKey) -> Unit,
	onPickStatistic: (StatisticID, NavResultCallback<StatisticID>) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: StatisticsWidgetEditorViewModel = hiltViewModel(),
	resultViewModel: ResourcePickerResultViewModel = hiltViewModel(),
) {
	val statisticsWidgetEditorScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	LaunchedEffect(Unit) {
		resultViewModel.getSelectedIds(STATISTICS_WIDGET_BOWLER_PICKER_RESULT_KEY) { BowlerID(it) }
			.onEach {
				viewModel.handleAction(
					StatisticsWidgetEditorScreenUiAction.UpdatedBowler(it.firstOrNull()),
				)
			}
			.launchIn(this)

		resultViewModel.getSelectedIds(STATISTICS_WIDGET_LEAGUE_PICKER_RESULT_KEY) { LeagueID(it) }
			.onEach {
				viewModel.handleAction(
					StatisticsWidgetEditorScreenUiAction.UpdatedLeague(it.firstOrNull()),
				)
			}
			.launchIn(this)
	}

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						StatisticsWidgetEditorScreenEvent.Dismissed -> onBackPressed()
						is StatisticsWidgetEditorScreenEvent.EditBowler ->
							onPickBowler(it.bowlerId, STATISTICS_WIDGET_BOWLER_PICKER_RESULT_KEY)
						is StatisticsWidgetEditorScreenEvent.EditLeague ->
							onPickLeague(it.bowlerId, it.leagueId, STATISTICS_WIDGET_LEAGUE_PICKER_RESULT_KEY)
						is StatisticsWidgetEditorScreenEvent.EditStatistic ->
							onPickStatistic(it.statistic.id) @JvmSerializableLambda { id ->
								viewModel.handleAction(StatisticsWidgetEditorScreenUiAction.UpdatedStatistic(id))
							}
					}
				}
		}
	}

	StatisticsWidgetEditorScreen(
		state = statisticsWidgetEditorScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatisticsWidgetEditorScreen(
	state: StatisticsWidgetEditorScreenUiState,
	onAction: (StatisticsWidgetEditorScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			StatisticsWidgetEditorTopBar(
				isSaveEnabled = (state as? StatisticsWidgetEditorScreenUiState.Loaded)
					?.statisticsWidgetEditor
					?.source != null,
				scrollBehavior = scrollBehavior,
				onAction = { onAction(StatisticsWidgetEditorScreenUiAction.StatisticsWidgetEditor(it)) },
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			StatisticsWidgetEditorScreenUiState.Loading -> Unit
			is StatisticsWidgetEditorScreenUiState.Loaded -> StatisticsWidgetEditor(
				state = state.statisticsWidgetEditor,
				onAction = { onAction(StatisticsWidgetEditorScreenUiAction.StatisticsWidgetEditor(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}
