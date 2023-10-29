package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import ca.josephroque.bowlingcompanion.core.model.MatchPlayResult
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.components.DetailRow
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R
import java.util.UUID

@Composable
fun GameDetails(
	gameDetailsState: GameDetailsUiState,
	goToNext: (NextGameEditableElement) -> Unit,
	onOpenSeriesStats: () -> Unit,
	onOpenGameStats: () -> Unit,
	onManageGear: () -> Unit,
	onManageMatchPlay: () -> Unit,
	modifier: Modifier = Modifier,
) {
	when (gameDetailsState) {
		GameDetailsUiState.Loading -> Unit
		is GameDetailsUiState.Edit -> GameDetails(
			state = gameDetailsState,
			goToNext = goToNext,
			onOpenSeriesStats = onOpenSeriesStats,
			onOpenGameStats = onOpenGameStats,
			onManageGear = onManageGear,
			onManageMatchPlay = onManageMatchPlay,
			modifier = modifier,
		)
	}
}

@Composable
private fun GameDetails(
	state: GameDetailsUiState.Edit,
	goToNext: (NextGameEditableElement) -> Unit,
	onOpenSeriesStats: () -> Unit,
	onOpenGameStats: () -> Unit,
	onManageGear: () -> Unit,
	onManageMatchPlay: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		horizontalAlignment = Alignment.Start,
		verticalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier.fillMaxSize(),
	) {
		Header(
			bowlerName = state.bowlerName,
			leagueName = state.leagueName,
			nextElement = state.nextElement,
			goToNext = goToNext,
		)

		StatisticsButtons(
			gameIndex = state.currentGameIndex,
			openSeriesStats = onOpenSeriesStats,
			openGameStats = onOpenGameStats,
		)

		GearCard(
			selectedGear = state.selectedGear,
			manageGear = onManageGear,
			modifier = Modifier.padding(horizontal = 16.dp),
		)

		MatchPlayCard(
			opponentName = state.opponentName,
			opponentScore = state.opponentScore,
			result = state.matchPlayResult,
			manageMatchPlay = onManageMatchPlay,
			modifier = Modifier.padding(horizontal = 16.dp),
		)
	}
}

@Composable
private fun Header(
	bowlerName: String,
	leagueName: String,
	nextElement: NextGameEditableElement?,
	goToNext: (NextGameEditableElement) -> Unit,
	modifier: Modifier = Modifier,
) {
	DetailRow(modifier = modifier) {
		Column(
			horizontalAlignment = Alignment.Start,
			modifier = Modifier.weight(1f),
		) {
			Text(text = bowlerName, style = MaterialTheme.typography.bodyLarge)
			Text(text = leagueName, style = MaterialTheme.typography.bodyMedium)
		}

		if (nextElement != null) {
			IconButton(onClick = { goToNext(nextElement) }) {
				Icon(
					painter = painterResource(RCoreDesign.drawable.ic_chevron_right),
					contentDescription = when (nextElement) {
						is NextGameEditableElement.Roll -> stringResource(R.string.game_editor_next_roll, nextElement.rollIndex + 1)
						is NextGameEditableElement.Frame -> stringResource(R.string.game_editor_next_frame, nextElement.frameIndex + 1)
						is NextGameEditableElement.Game -> stringResource(R.string.game_editor_next_game, nextElement.gameIndex + 1)
					},
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		}
	}
}

sealed interface GameDetailsUiState {
	data object Loading: GameDetailsUiState
	data class Edit(
		val bowlerName: String,
		val leagueName: String,
		val currentGameIndex: Int,
		val selectedGear: List<GearListItem>,
		val opponentName: String?,
		val opponentScore: Int?,
		val matchPlayResult: MatchPlayResult?,
		val nextElement: NextGameEditableElement?,
	): GameDetailsUiState
}

sealed interface NextGameEditableElement {
	data class Roll(val rollIndex: Int): NextGameEditableElement
	data class Frame(val frameIndex: Int): NextGameEditableElement
	data class Game(val gameIndex: Int, val game: UUID): NextGameEditableElement
}

@Preview
@Composable
private fun GameDetailsPreview() {
	Surface {
		GameDetails(
			state = GameDetailsUiState.Edit(
				bowlerName = "Jordan",
				leagueName = "1 Sunday Nights 2019",
				currentGameIndex = 0,
				selectedGear = listOf(
					GearListItem(id = UUID.randomUUID(), name = "Yellow Ball", kind = GearKind.BOWLING_BALL, ownerName = "Joseph"),
					GearListItem(id = UUID.randomUUID(), name = "Green Towel", kind = GearKind.TOWEL, ownerName = "Sarah"),
				),
				opponentName = "Joseph",
				opponentScore = 145,
				matchPlayResult = MatchPlayResult.WON,
				nextElement = NextGameEditableElement.Roll(rollIndex = 1),
			),
			goToNext = {},
			onOpenSeriesStats = {},
			onOpenGameStats = {},
			onManageGear = {},
			onManageMatchPlay = {},
		)
	}
}