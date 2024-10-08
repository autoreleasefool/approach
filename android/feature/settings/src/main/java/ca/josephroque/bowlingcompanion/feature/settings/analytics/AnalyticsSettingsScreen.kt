package ca.josephroque.bowlingcompanion.feature.settings.analytics

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
import ca.josephroque.bowlingcompanion.feature.settings.ui.analytics.AnalyticsSettings
import ca.josephroque.bowlingcompanion.feature.settings.ui.analytics.AnalyticsSettingsTopBar
import kotlinx.coroutines.launch

@Composable
internal fun AnalyticsSettingsRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AnalyticsSettingsViewModel = hiltViewModel(),
) {
	val analyticsSettingsState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						AnalyticsSettingsScreenEvent.Dismissed -> onBackPressed()
					}
				}
		}
	}

	AnalyticsSettingsScreen(
		state = analyticsSettingsState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AnalyticsSettingsScreen(
	state: AnalyticsSettingsScreenUiState,
	onAction: (AnalyticsSettingsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
	Scaffold(
		topBar = {
			AnalyticsSettingsTopBar(
				onAction = { onAction(AnalyticsSettingsScreenUiAction.AnalyticsSettingsAction(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			AnalyticsSettingsScreenUiState.Loading -> Unit
			is AnalyticsSettingsScreenUiState.Loaded -> AnalyticsSettings(
				state = state.analyticsSettings,
				onAction = { onAction(AnalyticsSettingsScreenUiAction.AnalyticsSettingsAction(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}
