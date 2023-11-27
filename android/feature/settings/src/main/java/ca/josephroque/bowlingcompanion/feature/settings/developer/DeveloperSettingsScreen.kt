package ca.josephroque.bowlingcompanion.feature.settings.developer

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.settings.ui.developer.DeveloperSettings
import ca.josephroque.bowlingcompanion.feature.settings.ui.developer.DeveloperSettingsTopBar
import kotlinx.coroutines.launch

@Composable
fun DeveloperSettingsRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: DeveloperSettingsViewModel = hiltViewModel(),
) {
	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						DeveloperSettingsScreenEvent.Dismissed -> onBackPressed()
					}
				}
		}
	}

	DeveloperSettingsScreen(
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@Composable
private fun DeveloperSettingsScreen(
	onAction: (DeveloperSettingsScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			DeveloperSettingsTopBar(onAction = { onAction(DeveloperSettingsScreenUiAction.DeveloperSettingsAction(it)) })
		}
	) { padding ->
		DeveloperSettings(modifier = modifier.padding(padding))
	}
}