package ca.josephroque.bowlingcompanion.feature.settings.developer

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ca.josephroque.bowlingcompanion.feature.settings.ui.developer.DeveloperSettings
import ca.josephroque.bowlingcompanion.feature.settings.ui.developer.DeveloperSettingsTopBar

@Composable
fun DeveloperSettingsRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: DeveloperSettingsViewModel = hiltViewModel(),
) {
	when (viewModel.events.collectAsState().value) {
		DeveloperSettingsScreenEvent.Dismissed -> onBackPressed()
		null -> Unit
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