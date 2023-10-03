package ca.josephroque.bowlingcompanion.feature.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.R

@Composable
internal fun SettingsRoute(
	modifier: Modifier = Modifier,
) {
	Text(
		text = stringResource(R.string.destination_settings),
		style = MaterialTheme.typography.displayLarge,
		modifier = modifier,
	)
}