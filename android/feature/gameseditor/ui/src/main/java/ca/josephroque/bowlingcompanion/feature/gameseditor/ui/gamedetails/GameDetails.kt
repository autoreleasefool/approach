package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
	goToNext: (NextGameEditableElement) -> Unit,
	onOpenSeriesStats: () -> Unit,
	onOpenGameStats: () -> Unit,
	onManageGear: () -> Unit,
	onManageMatchPlay: () -> Unit,
	onManageScore: () -> Unit,
	onToggleLock: (Boolean?) -> Unit,
	onToggleExcludeFromStatistics: (Boolean?) -> Unit,
	onMeasureHeaderHeight: (Float) -> Unit,
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
			goToNext = goToNext,
			modifier = Modifier.onGloballyPositioned { onMeasureHeaderHeight(it.size.height.toFloat()) }
		)

		StatisticsButtons(
			gameIndex = state.currentGameIndex,
			openSeriesStats = onOpenSeriesStats,
			openGameStats = onOpenGameStats,
		)

		GearCard(
			state = state.gear,
			manageGear = onManageGear,
			modifier = Modifier.padding(horizontal = 16.dp),
		)

		MatchPlayCard(
			state = state.matchPlay,
			manageMatchPlay = onManageMatchPlay,
			modifier = Modifier.padding(horizontal = 16.dp),
		)

		ScoringMethodCard(
			state = state.scoringMethod,
			manageScore = onManageScore,
			modifier = Modifier.padding(horizontal = 16.dp),
		)

		GamePropertiesCard(
			state = state.gameProperties,
			onToggleLock = onToggleLock,
			onToggleExcludeFromStatistics = onToggleExcludeFromStatistics,
			modifier = Modifier.padding(horizontal = 16.dp),
		)
	}
}

data class GameDetailsUiState(
	val currentGameIndex: Int = 0,
	val header: HeaderUiState = HeaderUiState(),
	val gear: GearCardUiState = GearCardUiState(),
	val matchPlay: MatchPlayCardUiState = MatchPlayCardUiState(),
	val scoringMethod: ScoringMethodCardUiState = ScoringMethodCardUiState(),
	val gameProperties: GamePropertiesCardUiState = GamePropertiesCardUiState(),
)

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
			state = GameDetailsUiState(
				currentGameIndex = 0,
				header = HeaderUiState(
					bowlerName = "Jordan",
					leagueName = "1 Sunday Nights 2019",
					nextElement = NextGameEditableElement.Roll(rollIndex = 1)
				),
				gear = GearCardUiState(
					selectedGear = listOf(
						GearListItem(id = UUID.randomUUID(), name = "Yellow Ball", kind = GearKind.BOWLING_BALL, ownerName = "Joseph", avatar = Avatar.default()),
						GearListItem(id = UUID.randomUUID(), name = "Green Towel", kind = GearKind.TOWEL, ownerName = "Sarah", avatar = Avatar.default()),
					),
				),
				matchPlay = MatchPlayCardUiState(
					opponentName = "Joseph",
					opponentScore = 145,
					result = MatchPlayResult.WON,
				),
				scoringMethod = ScoringMethodCardUiState(
					score = 234,
					scoringMethod = GameScoringMethod.BY_FRAME,
				),
				gameProperties = GamePropertiesCardUiState(
					locked = GameLockState.UNLOCKED,
					gameExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
					seriesExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
					leagueExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
					seriesPreBowl = SeriesPreBowl.REGULAR,
				),
			),
			goToNext = {},
			onOpenSeriesStats = {},
			onOpenGameStats = {},
			onManageGear = {},
			onManageMatchPlay = {},
			onManageScore = {},
			onToggleLock = {},
			onToggleExcludeFromStatistics = {},
			onMeasureHeaderHeight = {},
		)
	}
}