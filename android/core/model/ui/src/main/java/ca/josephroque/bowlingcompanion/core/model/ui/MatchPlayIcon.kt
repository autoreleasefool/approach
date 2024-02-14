package ca.josephroque.bowlingcompanion.core.model.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.model.MatchPlayResult

@Composable
fun MatchPlayResult?.icon(): Painter = when (this) {
	MatchPlayResult.WON -> painterResource(R.drawable.ic_match_play_win)
	MatchPlayResult.LOST -> painterResource(R.drawable.ic_match_play_lose)
	MatchPlayResult.TIED -> painterResource(R.drawable.ic_match_play_draw)
	null -> painterResource(R.drawable.ic_match_play_none)
}

@Composable
fun MatchPlayResult?.contentDescription(): String = when (this) {
	MatchPlayResult.WON -> stringResource(R.string.match_play_result_won)
	MatchPlayResult.LOST -> stringResource(R.string.match_play_result_lost)
	MatchPlayResult.TIED -> stringResource(R.string.match_play_result_tied)
	null -> stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.none)
}
