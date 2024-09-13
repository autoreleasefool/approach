package ca.josephroque.bowlingcompanion.feature.statisticswidget.layout

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
import ca.josephroque.bowlingcompanion.feature.statisticswidget.editor.StatisticsWidgetInitialSource
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.editor.StatisticsWidgetLayoutEditor
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.editor.StatisticsWidgetLayoutEditorTopBar
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.editor.StatisticsWidgetLayoutEditorTopBarUiState
import kotlinx.coroutines.launch

@Composable
internal fun StatisticsWidgetLayoutEditorRoute(
	onBackPressed: () -> Unit,
	onAddWidget: (String, StatisticsWidgetInitialSource?, Int) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: StatisticsWidgetLayoutEditorViewModel = hiltViewModel(),
) {
	val statisticsWidgetLayoutScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						StatisticsWidgetLayoutEditorScreenEvent.Dismissed ->
							onBackPressed()
						is StatisticsWidgetLayoutEditorScreenEvent.AddWidget ->
							onAddWidget(it.context, it.initialSource, it.priority)
					}
				}
		}
	}

	StatisticsWidgetLayoutEditorScreen(
		state = statisticsWidgetLayoutScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatisticsWidgetLayoutEditorScreen(
	state: StatisticsWidgetLayoutEditorScreenUiState,
	onAction: (StatisticsWidgetLayoutEditorScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		onAction(StatisticsWidgetLayoutEditorScreenUiAction.LoadWidgets)
	}

	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			StatisticsWidgetLayoutEditorTopBar(
				onAction = { onAction(StatisticsWidgetLayoutEditorScreenUiAction.LayoutEditor(it)) },
				scrollBehavior = scrollBehavior,
				state = when (state) {
					StatisticsWidgetLayoutEditorScreenUiState.Loading ->
						StatisticsWidgetLayoutEditorTopBarUiState()
					is StatisticsWidgetLayoutEditorScreenUiState.Loaded ->
						state.topBar
				},
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			StatisticsWidgetLayoutEditorScreenUiState.Loading -> Unit
			is StatisticsWidgetLayoutEditorScreenUiState.Loaded -> StatisticsWidgetLayoutEditor(
				state = state.layoutEditor,
				onAction = { onAction(StatisticsWidgetLayoutEditorScreenUiAction.LayoutEditor(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}
