package ca.josephroque.bowlingcompanion.feature.sharing.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.ShareableGame
import ca.josephroque.bowlingcompanion.core.model.stub.ScoringStub
import ca.josephroque.bowlingcompanion.core.scoresheet.FramePosition
import ca.josephroque.bowlingcompanion.core.scoresheet.ScorePosition
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheet
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetConfiguration
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetUiState
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingAppearance

@Composable
fun ShareableGamesImage(
	games: List<ShareableGame>,
	configuration: GamesSharingConfigurationUiState,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier
			.background(
				when (configuration.appearance) {
					SharingAppearance.Light -> Color.White
					SharingAppearance.Dark -> Color.Black
				},
			),
	) {
		ScoreSheet(
			state = ScoreSheetUiState(
				configuration = ScoreSheetConfiguration(
					framePosition = setOf(FramePosition.TOP),
					scorePosition = setOf(ScorePosition.START, ScorePosition.END),
					style = ScoreSheetConfiguration.Style.PLAIN,
				),
				selection = ScoreSheetUiState.Selection(frameIndex = -1, rollIndex = -1),
				game = ScoringStub.stub(),
			),
			onAction = {},
			modifier = Modifier.height(100.dp),
		)
	}
}

private class AppearancePreviewParameterProvider : PreviewParameterProvider<SharingAppearance> {
	override val values: Sequence<SharingAppearance> = sequenceOf(SharingAppearance.Light, SharingAppearance.Dark)
}

@Preview
@Composable
private fun ShareableGamesImagePreview(
	@PreviewParameter(AppearancePreviewParameterProvider::class) appearance: SharingAppearance,
) {
	Surface {
		ShareableGamesImage(
			games = emptyList(),
			configuration = GamesSharingConfigurationUiState(
				appearance = appearance,
			),
		)
	}
}
