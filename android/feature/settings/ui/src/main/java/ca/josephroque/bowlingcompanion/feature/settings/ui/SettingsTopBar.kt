package ca.josephroque.bowlingcompanion.feature.settings.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar(
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		scrollBehavior = scrollBehavior,
		colors = TopAppBarDefaults.topAppBarColors(),
		title = {
			Text(
				text = stringResource(R.string.settings_title),
				style = MaterialTheme.typography.titleLarge,
			)
		}
	)
}