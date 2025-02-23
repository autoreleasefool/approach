package ca.josephroque.bowlingcompanion.feature.sharing.ui.series

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ca.josephroque.bowlingcompanion.core.model.ShareableSeries

@Composable
fun ShareableSeriesImage(
	series: ShareableSeries,
	modifier: Modifier = Modifier,
) {
	Text(series.properties.bowlerName)
}