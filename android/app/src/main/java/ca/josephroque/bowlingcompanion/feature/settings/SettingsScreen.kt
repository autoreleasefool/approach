package ca.josephroque.bowlingcompanion.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.feature.settings.ui.NavigationItem

@Composable
internal fun SettingsRoute(
	openOpponents: () -> Unit,
	openStatisticsSettings: () -> Unit,
	openAcknowledgements: () -> Unit,
	openAnalyticsSettings: () -> Unit,
	openDataSettings: () -> Unit,
	openDeveloperSettings: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SettingsViewModel = hiltViewModel(),
) {
	val settingsState by viewModel.settingsState.collectAsStateWithLifecycle()

	SettingsScreen(
		settingsState = settingsState,
		openOpponents = openOpponents,
	)
}

@Composable
internal fun SettingsScreen(
	settingsState: SettingsUiState,
	openOpponents: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			SettingsTopBar()
		}
	) { padding ->
		Column(
			modifier = modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
				.padding(padding),
		) {
			NavigationItem(
				titleResourceId = R.string.settings_item_opponents_title,
				descriptionResourceId = R.string.settings_item_opponents_description,
				onClick = openOpponents,
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsTopBar() {
	TopAppBar(
		colors = TopAppBarDefaults.topAppBarColors(),
		title = {
			Text(
				text = stringResource(R.string.settings_title),
				style = MaterialTheme.typography.titleLarge,
			)
		}
	)
}