package ca.josephroque.bowlingcompanion.feature.sharing.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.ShareableSeriesImage

@Composable
fun ShareablePreviewImage(state: SharingData, modifier: Modifier = Modifier) {
	when (state) {
		is SharingData.Series -> ShareableSeriesImage(
			series = state.series,
			configuration = state.configuration,
			modifier = modifier,
		)
		SharingData.Game -> TODO()
		SharingData.Statistic -> TODO()
	}
}
