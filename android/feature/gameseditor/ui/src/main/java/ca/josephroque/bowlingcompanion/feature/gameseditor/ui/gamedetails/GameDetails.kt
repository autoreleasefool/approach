package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import ca.josephroque.bowlingcompanion.core.model.MatchPlayResult
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import java.util.UUID

@Composable
fun GameDetails(
	state: GameDetailsUiState,
	onAction: (GameDetailsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		horizontalAlignment = Alignment.Start,
		verticalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState())
			.padding(bottom = 16.dp),
	) {
		Header(
			state = state.header,
			onAction = onAction,
			modifier = Modifier
				.onGloballyPositioned {
					onAction(GameDetailsUiAction.HeaderHeightMeasured(it.size.height.toFloat()))
				}
				.padding(horizontal = 16.dp)
				.padding(top = 8.dp),
		)

		Divider(thickness = 8.dp)

		StatisticsButtons(
			gameIndex = state.currentGameIndex,
			onAction = onAction,
			modifier = Modifier.padding(horizontal = 16.dp),
		)

		Divider(thickness = 8.dp)

		GearCard(
			state = state.gear,
			onAction = onAction,
			modifier = Modifier.padding(horizontal = 16.dp),
		)

		Divider(thickness = 8.dp)

		AlleyCard(
			state = state.alley,
			onAction = onAction,
			modifier = Modifier.padding(horizontal = 16.dp),
		)

		Divider(thickness = 8.dp)

		MatchPlayCard(
			state = state.matchPlay,
			onAction = onAction,
			modifier = Modifier.padding(horizontal = 16.dp),
		)

		Divider(thickness = 8.dp)

		ScoringMethodCard(
			state = state.scoringMethod,
			onAction = onAction,
			modifier = Modifier.padding(horizontal = 16.dp),
		)

		Divider(thickness = 8.dp)

		GamePropertiesCard(
			state = state.gameProperties,
			onAction = onAction,
		)
	}
}

@Preview
@Composable
private fun GameDetailsPreview() {
	Surface {
		GameDetails(
			state = GameDetailsUiState(
				gameId = UUID.randomUUID(),
				currentGameIndex = 0,
				header = GameDetailsUiState.HeaderUiState(
					bowlerName = "Jordan",
					leagueName = "1 Sunday Nights 2019",
					nextElement = NextGameEditableElement.Roll(rollIndex = 1)
				),
				gear = GameDetailsUiState.GearCardUiState(
					selectedGear = listOf(
						GearListItem(
							id = UUID.randomUUID(),
							name = "Yellow Ball",
							kind = GearKind.BOWLING_BALL,
							ownerName = "Joseph",
							avatar = Avatar.default()
						),
						GearListItem(
							id = UUID.randomUUID(),
							name = "Green Towel",
							kind = GearKind.TOWEL,
							ownerName = "Sarah",
							avatar = Avatar.default()
						),
					),
				),
				matchPlay = GameDetailsUiState.MatchPlayCardUiState(
					opponentName = "Joseph",
					opponentScore = 145,
					result = MatchPlayResult.WON,
				),
				scoringMethod = GameDetailsUiState.ScoringMethodCardUiState(
					score = 234,
					scoringMethod = GameScoringMethod.BY_FRAME,
				),
				gameProperties = GameDetailsUiState.GamePropertiesCardUiState(
					locked = GameLockState.UNLOCKED,
					gameExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
					seriesExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
					leagueExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
					seriesPreBowl = SeriesPreBowl.REGULAR,
				),
			),
			onAction = {},
		)
	}
}