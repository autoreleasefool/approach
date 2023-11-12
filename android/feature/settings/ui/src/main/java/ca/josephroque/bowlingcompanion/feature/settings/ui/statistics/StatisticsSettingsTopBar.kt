package ca.josephroque.bowlingcompanion.feature.settings.ui.statistics

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton
import ca.josephroque.bowlingcompanion.feature.settings.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsSettingsTopBar(
	onAction: (StatisticsSettingsUiAction) -> Unit,
) {
	TopAppBar(
		colors = TopAppBarDefaults.topAppBarColors(),
		title = {
			Text(
				text = stringResource(R.string.settings_item_analytics),
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = {
			BackButton(onClick = { onAction(StatisticsSettingsUiAction.BackClicked) })
		},
	)
}