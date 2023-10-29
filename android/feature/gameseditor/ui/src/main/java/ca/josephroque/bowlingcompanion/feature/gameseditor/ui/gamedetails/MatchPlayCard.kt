package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.RoundIconButton
import ca.josephroque.bowlingcompanion.core.model.MatchPlayResult
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.components.contentDescription
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.components.icon
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.components.DetailCard

@Composable
internal fun MatchPlayCard(
	modifier: Modifier = Modifier,
	opponentName: String?,
	opponentScore: Int?,
	result: MatchPlayResult?,
	manageMatchPlay: () -> Unit,
) {
	DetailCard(
		title = stringResource(R.string.game_editor_match_play_title),
		action = {
			RoundIconButton(onClick = manageMatchPlay) {
				Icon(
					Icons.Default.Edit,
					contentDescription = stringResource(RCoreDesign.string.action_manage),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		},
		modifier = modifier,
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 8.dp),
		) {
			Icon(
				painter = painterResource(if (opponentName == null) R.drawable.ic_person_none else R.drawable.ic_person),
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSurface,
			)

			Label(
				title = stringResource(R.string.game_editor_match_play_opponent),
				value = opponentName,
				placeholder = stringResource(R.string.game_editor_match_play_no_opponent),
				modifier = Modifier.weight(1f),
			)

			Label(
				title = stringResource(R.string.game_editor_match_play_score),
				value = opponentScore?.toString(),
				placeholder = stringResource(R.string.game_editor_match_play_score_not_recorded),
				horizontalAlignment = Alignment.End,
			)
		}

		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			modifier = Modifier
				.fillMaxWidth()
				.padding(8.dp),
		) {
			Icon(
				painter = result.icon(),
				contentDescription = result.contentDescription(),
				tint = MaterialTheme.colorScheme.onSurface,
			)

			Label(
				title = stringResource(R.string.game_editor_match_play_result),
				value = result?.contentDescription(),
				placeholder = stringResource(R.string.game_editor_match_play_result_not_recorded),
				modifier = Modifier.weight(1f),
			)
		}
	}
}

@Composable
private fun Label(
	modifier: Modifier = Modifier,
	title: String,
	value: String?,
	placeholder: String,
	horizontalAlignment: Alignment.Horizontal = Alignment.Start
) {
	Column(
		horizontalAlignment = horizontalAlignment,
		modifier = modifier,
	) {
		if (value == null) {
			Text(
				text = placeholder,
				style = MaterialTheme.typography.bodyLarge,
				fontStyle = FontStyle.Italic,
			)
		} else {
			Text(
				text = title,
				style = MaterialTheme.typography.labelSmall,
			)

			Text(
				text = value,
				style = MaterialTheme.typography.bodyLarge,
			)
		}
	}
}

@Preview
@Composable
private fun MatchPlayCardPreview() {
	MatchPlayCard(
		opponentName = "Joseph",
		opponentScore = 250,
		result = MatchPlayResult.WON,
		manageMatchPlay = {},
	)
}