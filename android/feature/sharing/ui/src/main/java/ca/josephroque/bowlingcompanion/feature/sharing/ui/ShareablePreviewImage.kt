package ca.josephroque.bowlingcompanion.feature.sharing.ui

import androidx.compose.runtime.Composable
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.ShareableSeriesImage

@Composable
fun ShareablePreviewImage(
	state: SharingData,
) {
	when (state) {
		is SharingData.Series -> ShareableSeriesImage(series = state.series)
		SharingData.Game -> TODO()
		SharingData.Statistic -> TODO()
	}
}