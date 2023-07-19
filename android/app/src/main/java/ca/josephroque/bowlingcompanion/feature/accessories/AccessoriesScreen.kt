package ca.josephroque.bowlingcompanion.feature.accessories

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ca.josephroque.bowlingcompanion.R

@Composable
internal fun AccessoriesRoute(
	modifier: Modifier = Modifier,
) {
	Text(
		stringResource(R.string.destination_accessories),
		modifier = modifier,
	)
}

@Preview
@Composable
internal fun AccessoriesPreview() {
	AccessoriesRoute()
}