package ca.josephroque.bowlingcompanion.feature.settings

import android.os.Build
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
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

@Composable
private fun SettingsScreen(
	state: SettingsScreenUiState,
	onAction: (SettingsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val context = LocalContext.current
	LaunchedEffect(Unit) {
		@Suppress("DEPRECATION")
		try {
			onAction(
				SettingsScreenUiAction.ReceivedVersionInfo(
					versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName,
					versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
						context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode.toString()
					else
						context.packageManager.getPackageInfo(context.packageName, 0).versionCode.toString(),
				)
			)
		} catch (ex: Exception) {
			onAction(
				SettingsScreenUiAction.ReceivedVersionInfo(
					versionName = "N/A",
					versionCode = "N/A",
				)
			)
		}
	}

	Scaffold(
		topBar = {
			SettingsTopBar()
		}
	) { padding ->
		when (state) {
			is SettingsScreenUiState.Loading -> Unit
			is SettingsScreenUiState.Loaded -> Settings(
				state = state.settings,
				onAction = { onAction(SettingsScreenUiAction.SettingsAction(it)) },
				modifier = modifier.padding(padding),
			)
		}
	}
}