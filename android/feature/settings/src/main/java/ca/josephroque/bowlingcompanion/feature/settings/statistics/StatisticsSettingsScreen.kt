package ca.josephroque.bowlingcompanion.feature.settings.statistics

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
import ca.josephroque.bowlingcompanion.feature.settings.ui.statistics.StatisticsSettings
import ca.josephroque.bowlingcompanion.feature.settings.ui.statistics.StatisticsSettingsTopBar
import kotlinx.coroutines.launch

@Composable
internal fun StatisticsSettingsRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: StatisticsSettingsViewModel = hiltViewModel(),
) {
	val statisticsSettingsState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						StatisticsSettingsScreenEvent.Dismissed -> onBackPressed()
					}
				}
		}
	}

	StatisticsSettingsScreen(
		state = statisticsSettingsState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatisticsSettingsScreen(
	state: StatisticsSettingsScreenUiState,
	onAction: (StatisticsSettingsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
	Scaffold(
		topBar = {
			StatisticsSettingsTopBar(
				onAction = { onAction(StatisticsSettingsScreenUiAction.StatisticsSettingsAction(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			StatisticsSettingsScreenUiState.Loading -> Unit
			is StatisticsSettingsScreenUiState.Loaded -> StatisticsSettings(
				state = state.statisticsSettings,
				onAction = { onAction(StatisticsSettingsScreenUiAction.StatisticsSettingsAction(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}