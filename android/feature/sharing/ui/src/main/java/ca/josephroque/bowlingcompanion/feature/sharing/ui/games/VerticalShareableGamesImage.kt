package ca.josephroque.bowlingcompanion.feature.sharing.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.modifiers.drawToLayer
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.ShareableGame
import ca.josephroque.bowlingcompanion.core.model.stub.ScoringStub
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetUiState
import ca.josephroque.bowlingcompanion.core.scoresheet.VerticalScoreSheet
import ca.josephroque.bowlingcompanion.feature.sharing.ui.R
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingAppearance
import ca.josephroque.bowlingcompanion.feature.sharing.ui.components.ChartLabel
import kotlinx.datetime.LocalDate

@Composable
fun VerticalShareableGamesImage(
	game: ShareableGame,
	configuration: GamesSharingConfigurationUiState,
	graphicsLayer: GraphicsLayer,
	modifier: Modifier = Modifier,
) {
	val scrollState = rememberScrollState()

	Column(
		modifier = modifier
			.horizontalScroll(scrollState)
			.drawToLayer(graphicsLayer)
			.background(colorResource(configuration.scoreSheetConfiguration.style.backgroundColor)),
	) {
		Header(
			game,
			configuration,
			modifier = Modifier
				.padding(horizontal = 16.dp)
				.padding(top = 16.dp, bottom = 4.dp),
		)

		Text(
			text = stringResource(R.string.sharing_made_with_tryapproach),
			color = colorResource(configuration.scoreSheetConfiguration.style.textColorOnBackground),
			style = MaterialTheme.typography.labelSmall,
			modifier = Modifier
				.padding(horizontal = 16.dp)
				.padding(vertical = 4.dp),
		)

		VerticalScoreSheet(
			state = ScoreSheetUiState(
				configuration = configuration.scoreSheetConfiguration.copy(
					gameIndexPosition = emptySet(),
				),
				selection = ScoreSheetUiState.Selection(frameIndex = -1, rollIndex = -1),
				game = game.score,
			),
			onAction = {},
		)
	}
}

@Composable
private fun Header(
	game: ShareableGame,
	configuration: GamesSharingConfigurationUiState,
	modifier: Modifier = Modifier,
) {
	configuration.title(game)?.let { title ->
		Column(
			verticalArrangement = Arrangement.spacedBy(4.dp),
			horizontalAlignment = Alignment.Start,
			modifier = modifier,
		) {
			Text(
				text = title,
				color = colorResource(configuration.scoreSheetConfiguration.style.textColorOnBackground),
				style = MaterialTheme.typography.headlineLarge,
				fontWeight = FontWeight.SemiBold,
			)

			Row(
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				configuration.labels(listOf(game)).forEach {
					ChartLabel(
						content = it,
						appearance = configuration.plainChartLabel(),
					)
				}
			}
		}
	}
}

private class VerticalAppearancePreviewParameterProvider : PreviewParameterProvider<SharingAppearance> {
	override val values: Sequence<SharingAppearance> = sequenceOf(SharingAppearance.Light, SharingAppearance.Dark)
}

@Preview
@Composable
private fun VerticalShareableGamesImagePreview(
	@PreviewParameter(VerticalAppearancePreviewParameterProvider::class) appearance: SharingAppearance,
) {
	val gameId = GameID.randomID()
	Surface {
		VerticalShareableGamesImage(
			game = ShareableGame(
				id = gameId,
				index = 0,
				bowlerName = "Joseph",
				leagueName = "Majors",
				seriesDate = LocalDate(2024, 6, 15),
				alleyName = "Alley 1",
				score = ScoringStub.stub(),
			),
			configuration = GamesSharingConfigurationUiState(
				isBowlerNameChecked = true,
				isLeagueNameChecked = false,
				isSeriesDateChecked = false,
				isSeriesDetailChecked = true,
				appearance = appearance,
				isGameIncluded = listOf(
					GamesSharingConfigurationUiState.IncludedGame(
						gameId = gameId,
						index = 0,
						isGameIncluded = true,
					),
				),
			),
			graphicsLayer = rememberGraphicsLayer(),
		)
	}
}
