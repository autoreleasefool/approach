package ca.josephroque.bowlingcompanion.feature.settings.analytics

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.feature.settings.ui.analytics.AnalyticsSettings
import ca.josephroque.bowlingcompanion.feature.settings.ui.analytics.AnalyticsSettingsTopBar

@Composable
internal fun AnalyticsSettingsRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AnalyticsSettingsViewModel = hiltViewModel(),
) {
	val analyticsSettingsState by viewModel.uiState.collectAsStateWithLifecycle()

	when (viewModel.events.collectAsState().value) {
		AnalyticsSettingsScreenEvent.Dismissed -> onBackPressed()
		null -> Unit
	}

	AnalyticsSettingsScreen(
		state = analyticsSettingsState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@Composable
internal fun AnalyticsSettingsScreen(
	state: AnalyticsSettingsScreenUiState,
	onAction: (AnalyticsSettingsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			AnalyticsSettingsTopBar(onAction = { onAction(AnalyticsSettingsScreenUiAction.AnalyticsSettingsAction(it)) })
		}
	) { padding ->
		when (state) {
			AnalyticsSettingsScreenUiState.Loading -> Unit
			is AnalyticsSettingsScreenUiState.Loaded -> AnalyticsSettings(
				state = state.analyticsSettings,
				onAction = { onAction(AnalyticsSettingsScreenUiAction.AnalyticsSettingsAction(it)) },
				modifier = modifier.padding(padding),
			)
		}
	}
}