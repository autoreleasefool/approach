package ca.josephroque.bowlingcompanion.core.scoresheet

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.modifiers.endBorder
import ca.josephroque.bowlingcompanion.core.model.stub.BowlerSummaryStub
import ca.josephroque.bowlingcompanion.core.model.stub.LeagueSummaryStub
import ca.josephroque.bowlingcompanion.core.model.stub.ScoringGameStub

@Composable
fun ScoreSheetList(
	state: ScoreSheetListUiState,
	onAction: (ScoreSheetUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	BoxWithConstraints(
		modifier = modifier.fillMaxWidth(),
	) {
		val cellWidth = maxWidth / 3f

		Column {
			state.bowlerScores.forEachIndexed { index, bowlerScore ->
				val (bowler, league, scoreSheet) = bowlerScore

				Row(
					modifier = Modifier
						.height(100.dp)
						.padding(horizontal = 16.dp)
						.clip(RoundedCornerShape(16.dp)),
				) {
					Column(
						horizontalAlignment = Alignment.End,
						modifier = Modifier
							.background(colorResource(scoreSheet.configuration.style.backgroundColor))
							.endBorder(4.dp, colorResource(scoreSheet.configuration.style.borderColor))
							.width(cellWidth)
							.fillMaxHeight()
							.padding(horizontal = 16.dp, vertical = 8.dp)
					) {
						Spacer(modifier = Modifier.weight(1f))

						Text(
							text = bowler.name,
							style = MaterialTheme.typography.titleMedium,
							color = colorResource(scoreSheet.configuration.style.textColorOnBackground),
							textAlign = TextAlign.End,
						)

						Text(
							text = league.name,
							style = MaterialTheme.typography.bodyMedium,
							color = colorResource(scoreSheet.configuration.style.textColorOnBackground),
							fontStyle = FontStyle.Italic,
							textAlign = TextAlign.End,
						)

						Spacer(modifier = Modifier.weight(1f))
					}

					ScoreSheetRow(
						state = scoreSheet,
						onAction = onAction,
						cellWidth = cellWidth,
						modifier = Modifier.horizontalScroll(rememberScrollState()),
					)
				}

				if (index < state.bowlerScores.size - 1) {
					Box(
						modifier = Modifier
							.fillMaxWidth()
							.height(16.dp)
							.background(colorResource(scoreSheet.configuration.style.borderColor)),
					)
				}
			}
		}
	}
}

@Preview
@Composable
private fun ScoreSheetListPreview() {
	Surface {
		ScoreSheetList(
			state = ScoreSheetListUiState(
				bowlerScores = listOf(
					Triple(
						BowlerSummaryStub.list()[0],
						LeagueSummaryStub.list()[0],
						ScoreSheetUiState(
							game = ScoringGameStub.stub(),
						),
					),
					Triple(
						BowlerSummaryStub.list()[1],
						LeagueSummaryStub.list()[1],
						ScoreSheetUiState(
							game = ScoringGameStub.stub(),
						),
					),
				),
			),
			onAction = {},
		)
	}
}