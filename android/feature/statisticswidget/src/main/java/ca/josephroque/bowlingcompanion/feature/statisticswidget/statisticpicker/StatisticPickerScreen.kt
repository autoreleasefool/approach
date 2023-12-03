package ca.josephroque.bowlingcompanion.feature.statisticswidget.statisticpicker

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
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.statisticpicker.StatisticPicker
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.statisticpicker.StatisticPickerTopBar
import kotlinx.coroutines.launch

@Composable
internal fun StatisticPickerRoute(
	onDismissWithResult: (Int) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: StatisticPickerViewModel = hiltViewModel()
) {
	val statisticPickerState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						is StatisticPickerScreenEvent.Dismissed -> onDismissWithResult(it.result)
					}
				}
		}
	}

	StatisticPickerScreen(
		state = statisticPickerState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatisticPickerScreen(
	state: StatisticPickerScreenUiState,
	onAction: (StatisticPickerScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			StatisticPickerTopBar(
				onAction = { onAction(StatisticPickerScreenUiAction.StatisticPicker(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		StatisticPicker(
			state = state.statisticPicker,
			onAction = { onAction(StatisticPickerScreenUiAction.StatisticPicker(it)) },
			modifier = Modifier.padding(padding),
		)
	}
}
