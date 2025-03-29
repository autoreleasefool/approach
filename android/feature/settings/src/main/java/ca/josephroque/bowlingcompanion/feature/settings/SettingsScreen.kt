package ca.josephroque.bowlingcompanion.feature.settings

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
import ca.josephroque.bowlingcompanion.feature.settings.ui.Settings
import ca.josephroque.bowlingcompanion.feature.settings.ui.SettingsTopBar
import kotlinx.coroutines.launch

@Composable
internal fun SettingsRoute(
	openOpponents: () -> Unit,
	openStatisticsSettings: () -> Unit,
	openAcknowledgements: () -> Unit,
	openAnalyticsSettings: () -> Unit,
	openDataImportSettings: () -> Unit,
	openDataExportSettings: () -> Unit,
	openDeveloperSettings: () -> Unit,
	openArchives: () -> Unit,
	openFeatureFlags: () -> Unit,
	openAchievements: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SettingsViewModel = hiltViewModel(),
) {
	val settingsState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						SettingsScreenEvent.NavigateToAnalyticsSettings -> openAnalyticsSettings()
						SettingsScreenEvent.NavigateToAcknowledgements -> openAcknowledgements()
						SettingsScreenEvent.NavigateToDataExportSettings -> openDataExportSettings()
						SettingsScreenEvent.NavigateToDataImportSettings -> openDataImportSettings()
						SettingsScreenEvent.NavigateToDeveloperSettings -> openDeveloperSettings()
						SettingsScreenEvent.NavigateToOpponents -> openOpponents()
						SettingsScreenEvent.NavigateToStatisticsSettings -> openStatisticsSettings()
						SettingsScreenEvent.NavigateToArchives -> openArchives()
						SettingsScreenEvent.NavigateToFeatureFlags -> openFeatureFlags()
						SettingsScreenEvent.NavigateToAchievements -> openAchievements()
					}
				}
		}
	}

	SettingsScreen(
		state = settingsState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
	state: SettingsScreenUiState,
	onAction: (SettingsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			SettingsTopBar(
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			is SettingsScreenUiState.Loading -> Unit
			is SettingsScreenUiState.Loaded -> Settings(
				state = state.settings,
				onAction = { onAction(SettingsScreenUiAction.SettingsAction(it)) },
				modifier = Modifier.padding(padding),
			)
		}
	}
}
