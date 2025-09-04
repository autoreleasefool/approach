package ca.josephroque.bowlingcompanion.feature.sharing.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.modifiers.drawToLayer
import ca.josephroque.bowlingcompanion.core.designsystem.modifiers.topBorder
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.ShareableGame
import ca.josephroque.bowlingcompanion.core.model.stub.ScoringStub
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheet
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetUiState
import ca.josephroque.bowlingcompanion.feature.sharing.ui.R
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingAppearance
import ca.josephroque.bowlingcompanion.feature.sharing.ui.components.ChartLabel
import ca.josephroque.bowlingcompanion.feature.sharing.ui.components.ChartLabelAppearance
import kotlinx.datetime.LocalDate

@Composable
fun HorizontalShareableGamesImage(
	games: List<ShareableGame>,
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
		if (games.isNotEmpty()) {
			Header(
				games,
				configuration,
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.padding(top = 16.dp, bottom = 8.dp),
			)
		}

		games
			.filter { configuration.includeGame(it) }
			.forEach {
				HorizontalSingleShareableGameImage(
					game = it,
					configuration = configuration,
					modifier = Modifier
						.topBorder(
							4.dp,
							colorResource(configuration.scoreSheetConfiguration.style.borderColor),
						)
						.padding(top = 4.dp),
				)
			}
	}
}

@Composable
fun HorizontalSingleShareableGameImage(
	game: ShareableGame,
	configuration: GamesSharingConfigurationUiState,
	modifier: Modifier = Modifier,
) {
	Box(modifier = modifier) {
		ScoreSheet(
			state = ScoreSheetUiState(
				configuration = configuration.scoreSheetConfiguration,
				selection = ScoreSheetUiState.Selection(frameIndex = -1, rollIndex = -1),
				game = game.score,
			),
			onAction = {},
			modifier = Modifier.height(100.dp),
		)
	}
}

@Composable
private fun Header(
	games: List<ShareableGame>,
	configuration: GamesSharingConfigurationUiState,
	modifier: Modifier = Modifier,
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(4.dp),
		horizontalAlignment = Alignment.Start,
		modifier = modifier,
	) {
		configuration.title(games.first())?.let {
			Text(
				text = it,
				color = colorResource(configuration.scoreSheetConfiguration.style.textColorOnBackground),
				style = MaterialTheme.typography.headlineLarge,
				fontWeight = FontWeight.SemiBold,
			)
		}

		Row(
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			configuration.labels(games, includeSeriesDetails = true).forEach {
				ChartLabel(
					content = it,
					appearance = configuration.plainChartLabel(),
				)
			}
		}
	}
}

@Composable
fun GamesSharingConfigurationUiState.plainChartLabel() = ChartLabelAppearance(
	style = ChartLabelAppearance.Style.PLAIN,
	foregroundColor = chartLabelForegroundColor(),
	backgroundColor = chartLabelBackgroundColor(),
)

@Composable
fun GamesSharingConfigurationUiState.chartLabelForegroundColor(): Int {
	val backgroundColor = colorResource(style.backgroundColor)
	return if (backgroundColor.luminance() > 0.5f) {
		R.color.chart_label_foreground_light
	} else {
		R.color.chart_label_foreground_dark
	}
}

@Composable
fun GamesSharingConfigurationUiState.chartLabelBackgroundColor(): Int {
	val backgroundColor = colorResource(style.backgroundColor)
	return if (backgroundColor.luminance() > 0.5f) {
		R.color.chart_label_background_light
	} else {
		R.color.chart_label_background_dark
	}
}

private class AppearancePreviewParameterProvider : PreviewParameterProvider<SharingAppearance> {
	override val values: Sequence<SharingAppearance> = sequenceOf(SharingAppearance.Light, SharingAppearance.Dark)
}

@Preview
@Composable
private fun HorizontalShareableGamesImagePreview(
	@PreviewParameter(AppearancePreviewParameterProvider::class) appearance: SharingAppearance,
) {
	val gameIds = listOf(GameID.randomID(), GameID.randomID())
	Surface {
		HorizontalShareableGamesImage(
			games = gameIds.mapIndexed { index, id ->
				ShareableGame(
					id = id,
					index = index,
					bowlerName = "Joseph",
					leagueName = "Majors",
					seriesDate = LocalDate(2024, 6, 15),
					alleyName = "Alley 1",
					score = ScoringStub.stub(),
				)
			},
			configuration = GamesSharingConfigurationUiState(
				appearance = appearance,
				isGameIncluded = gameIds.mapIndexed { index, id ->
					GamesSharingConfigurationUiState.IncludedGame(
						gameId = id,
						index = index,
						isGameIncluded = true,
					)
				},
			),
			graphicsLayer = rememberGraphicsLayer(),
		)
	}
}
