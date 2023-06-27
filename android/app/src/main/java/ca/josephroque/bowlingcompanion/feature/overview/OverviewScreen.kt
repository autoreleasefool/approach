package ca.josephroque.bowlingcompanion.feature.overview

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.R

@Composable
internal fun OverviewRoute(
	modifier: Modifier = Modifier,
) {
	Text(stringResource(R.string.destination_overview))
}