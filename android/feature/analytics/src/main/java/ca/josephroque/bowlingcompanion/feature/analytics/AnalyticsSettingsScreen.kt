package ca.josephroque.bowlingcompanion.feature.analytics

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.feature.analytics.ui.AnalyticsSettings
import ca.josephroque.bowlingcompanion.feature.analytics.ui.AnalyticsSettingsTopBar
import ca.josephroque.bowlingcompanion.feature.analytics.ui.AnalyticsSettingsUiState

@Composable
internal fun AnalyticsSettingsRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AnalyticsSettingsViewModel = hiltViewModel(),
) {
	val analyticsSettingsState by viewModel.uiState.collectAsStateWithLifecycle()

	AnalyticsSettingsScreen(
		analyticsSettingsState = analyticsSettingsState,
		onBackPressed = onBackPressed,
		onToggleOptInStatus = viewModel::toggleAnalyticsOptInStatus,
		modifier = modifier,
	)
}

@Composable
internal fun AnalyticsSettingsScreen(
	analyticsSettingsState: AnalyticsSettingsUiState,
	onBackPressed: () -> Unit,
	onToggleOptInStatus: (Boolean?) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			AnalyticsSettingsTopBar(onBackPressed = onBackPressed)
		}
	) { padding ->
		when (analyticsSettingsState) {
			AnalyticsSettingsUiState.Loading ->  Unit
			is AnalyticsSettingsUiState.Success -> AnalyticsSettings(
				state = analyticsSettingsState,
				onToggleOptInStatus = onToggleOptInStatus,
				modifier = modifier.padding(padding)
			)
		}
	}
}