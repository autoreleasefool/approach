package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scores

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetLazyList

@Composable
fun ScoresList(
	state: ScoresListUiState,
	onAction: (ScoresListUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val listState = rememberLazyListState()
	LaunchedEffect(state.scoreSheetList.bowlerScores.size, state.gameIndex) {
		listState.animateScrollToItem(state.gameIndex)
	}

	ScoreSheetLazyList(
		state = state.scoreSheetList,
		onAction = { onAction(ScoresListUiAction.ScoreSheet(it)) },
		modifier = modifier,
	)
}
