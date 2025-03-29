package ca.josephroque.bowlingcompanion.feature.settings.ui.statistics

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton
import ca.josephroque.bowlingcompanion.feature.settings.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsSettingsTopBar(onAction: (StatisticsSettingsUiAction) -> Unit, scrollBehavior: TopAppBarScrollBehavior) {
	TopAppBar(
		scrollBehavior = scrollBehavior,
		title = {
			Text(
				text = stringResource(R.string.statistics_settings_title),
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = {
			BackButton(onClick = { onAction(StatisticsSettingsUiAction.BackClicked) })
		},
	)
}
