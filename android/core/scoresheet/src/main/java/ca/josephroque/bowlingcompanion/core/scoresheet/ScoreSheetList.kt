package ca.josephroque.bowlingcompanion.core.scoresheet

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.modifiers.endBorder
import ca.josephroque.bowlingcompanion.core.model.stub.BowlerSummaryStub
import ca.josephroque.bowlingcompanion.core.model.stub.LeagueSummaryStub
import ca.josephroque.bowlingcompanion.core.model.stub.ScoringStub
import kotlinx.coroutines.launch

@Composable
fun ScoreSheetList(
	state: ScoreSheetListUiState,
	onAction: (ScoreSheetUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	BoxWithConstraints(
		modifier = modifier
			.fillMaxWidth(),
	) {
		val cellWidth = maxWidth / 3f
		
		val defaultConfiguration = state.bowlerScores
			.firstOrNull()?.firstOrNull()?.scoreSheet?.configuration
			?: return@BoxWithConstraints
		
		LazyColumn {
			itemsIndexed(
				items = state.bowlerScores,
				key = { index, _ -> index },
			) { gameIndex, seriesScores ->
				val scope = rememberCoroutineScope()
				val scrollStates = seriesScores.map { rememberLazyListState() }
				val scrollState = rememberScrollableState { delta ->
					scope.launch {
						scrollStates.forEach {
							it.scrollBy(-delta)
						}
					}
					delta
				}

				Text(
					text = stringResource(
						ca.josephroque.bowlingcompanion.core.designsystem.R.string.game_with_ordinal,
						gameIndex + 1,
					),
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold,
					modifier = Modifier
						.padding(vertical = 8.dp, horizontal = 16.dp)
						.padding(top = if (gameIndex == 0) 0.dp else 8.dp),
				)

				Column(
					modifier = Modifier
						.background(colorResource(defaultConfiguration.style.backgroundColor))
						.scrollable(
							scrollState,
							Orientation.Horizontal,
							flingBehavior = ScrollableDefaults.flingBehavior(),
						),
				) {
					seriesScores.forEachIndexed { index, bowlerScore ->
						val (bowler, league, scoreSheet) = bowlerScore

						Row(
							modifier = Modifier.height(100.dp),
						) {
							Column(
								horizontalAlignment = Alignment.End,
								modifier = Modifier
									.background(colorResource(scoreSheet.configuration.style.backgroundColor))
									.endBorder(4.dp, colorResource(scoreSheet.configuration.style.borderColor))
									.width(cellWidth)
									.fillMaxHeight()
									.padding(horizontal = 16.dp, vertical = 8.dp),
							) {
								Spacer(modifier = Modifier.weight(1f))

								Text(
									text = bowler.name,
									style = MaterialTheme.typography.titleMedium,
									color = colorResource(scoreSheet.configuration.style.textColorOnBackground),
									textAlign = TextAlign.End,
									maxLines = 1,
									overflow = TextOverflow.Ellipsis,
								)

								Text(
									text = league.name,
									style = MaterialTheme.typography.bodyMedium,
									color = colorResource(scoreSheet.configuration.style.textColorOnBackground),
									fontStyle = FontStyle.Italic,
									textAlign = TextAlign.End,
									maxLines = 2,
									overflow = TextOverflow.Ellipsis,
								)

								Spacer(modifier = Modifier.weight(1f))
							}

							LazyScoreSheet(
								state = scoreSheet,
								listState = scrollStates[index],
								cellWidth = cellWidth,
								onAction = onAction,
								userScrollEnabled = false,
							)
						}

						if (index < state.bowlerScores.size - 1) {
							Box(
								modifier = Modifier
									.fillMaxWidth()
									.height(4.dp)
									.background(colorResource(scoreSheet.configuration.style.borderColor)),
							)
						}
					}
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
					listOf(
						ScoreSheetListItem(
							BowlerSummaryStub.list()[0],
							LeagueSummaryStub.list()[0],
							ScoreSheetUiState(
								game = ScoringStub.stub(),
								selection = ScoreSheetUiState.Selection.none(),
							),
						),
						ScoreSheetListItem(
							BowlerSummaryStub.list()[1],
							LeagueSummaryStub.list()[1],
							ScoreSheetUiState(
								game = ScoringStub.stub(),
								selection = ScoreSheetUiState.Selection.none(),
							),
						),
					),
					listOf(
						ScoreSheetListItem(
							BowlerSummaryStub.list()[0],
							LeagueSummaryStub.list()[0],
							ScoreSheetUiState(
								game = ScoringStub.stub(),
								selection = ScoreSheetUiState.Selection.none(),
							),
						),
						ScoreSheetListItem(
							BowlerSummaryStub.list()[1],
							LeagueSummaryStub.list()[1],
							ScoreSheetUiState(
								game = ScoringStub.stub(),
								selection = ScoreSheetUiState.Selection.none(),
							),
						),
					),
				),
			),
			onAction = {},
		)
	}
}
