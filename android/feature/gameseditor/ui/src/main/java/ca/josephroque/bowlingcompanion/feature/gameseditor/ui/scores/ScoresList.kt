package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scores

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetList

@Composable
fun ScoresList(
	state: ScoresListUiState,
	onAction: (ScoresListUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	ScoreSheetList(
		state = state.scoreSheetList,
		onAction = { onAction(ScoresListUiAction.ScoreSheet(it)) },
		modifier = modifier,
	)
}
