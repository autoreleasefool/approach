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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.core.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.editor.StatisticsWidgetEditor
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.editor.StatisticsWidgetEditorTopBar
import java.util.UUID
import kotlinx.coroutines.launch

@Composable
fun StatisticsWidgetEditorRoute(
	onBackPressed: () -> Unit,
	onPickBowler: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onPickLeague: (UUID, UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onPickStatistic: (StatisticID, NavResultCallback<StatisticID>) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: StatisticsWidgetEditorViewModel = hiltViewModel(),
) {
	val statisticsWidgetEditorScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						StatisticsWidgetEditorScreenEvent.Dismissed -> onBackPressed()
						is StatisticsWidgetEditorScreenEvent.EditBowler ->
							onPickBowler(it.bowlerId) @JvmSerializableLambda { ids ->
								viewModel.handleAction(
									StatisticsWidgetEditorScreenUiAction.UpdatedBowler(ids.firstOrNull()),
								)
							}
						is StatisticsWidgetEditorScreenEvent.EditLeague ->
							onPickLeague(it.bowlerId, it.leagueId) @JvmSerializableLambda { ids ->
								viewModel.handleAction(
									StatisticsWidgetEditorScreenUiAction.UpdatedLeague(ids.firstOrNull()),
								)
							}
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
