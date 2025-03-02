package ca.josephroque.bowlingcompanion.feature.sharing.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.ShareableSeries
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.SeriesSharingConfiguration
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.SeriesSharingConfigurationUiState
import kotlinx.datetime.LocalDate

@Composable
fun Sharing(
	state: SharingUiState,
	onAction: (SharingUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier,
	) {
		when (state) {
			is SharingUiState.SharingGame -> TODO()
			is SharingUiState.SharingStatistic -> TODO()
			is SharingUiState.SharingSeries -> SeriesSharingConfiguration(
				state = state.seriesSharing,
				onAction = { onAction(SharingUiAction.SeriesSharingAction(it)) },
				modifier = modifier,
			)
		}

		HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

		ShareablePreviewImage(
			state = state.sharingData,
			modifier = Modifier.clip(RoundedCornerShape(16.dp)),
		)

		ShareButton {
			onAction(SharingUiAction.ShareButtonClicked)
		}
	}
}

@Preview
@Composable
private fun SharingPreview() {
	Surface {
		Sharing(
			state = SharingUiState.SharingSeries(
				seriesSharing = SeriesSharingConfigurationUiState(),
				series = SharingData.Series(
					series = ShareableSeries(
						properties = ShareableSeries.Properties(
							id = SeriesID.randomID(),
							date = LocalDate(2025, 2, 22),
							total = 900,
							bowlerName = "Joseph",
							leagueName = "Majors",
						),
						scores = listOf(300, 200, 400),
					),
					configuration = SeriesSharingConfigurationUiState(),
				)
			),
			onAction = {},
		)
	}
}