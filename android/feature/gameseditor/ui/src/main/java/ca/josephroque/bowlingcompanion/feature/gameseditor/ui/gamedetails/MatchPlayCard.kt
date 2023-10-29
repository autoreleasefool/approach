package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.MatchPlayResult
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.components.contentDescription
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.components.icon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MatchPlayCard(
	modifier: Modifier = Modifier,
	opponentName: String?,
	opponentScore: Int?,
	result: MatchPlayResult?,
	manageMatchPlay: () -> Unit,
) {
	OutlinedCard(
		modifier = modifier,
		onClick = manageMatchPlay,
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			modifier = Modifier
				.fillMaxWidth(),
		) {
			Icon(
				painter = painterResource(if (opponentName == null) R.drawable.ic_person_none else R.drawable.ic_person),
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSurface,
			)

			Column(
				horizontalAlignment = Alignment.Start,
				modifier = Modifier.weight(1f),
			) {
				Text(
					text = opponentName ?: stringResource(R.string.game_editor_match_play_no_opponent),
					style = MaterialTheme.typography.bodyLarge,
					fontStyle = FontStyle.Italic,
				)

				Text(
					text = pluralStringResource(R.plurals.game_editor_match_play_score, count = opponentScore ?: 0, opponentScore ?: 0),
					style = MaterialTheme.typography.bodySmall,
				)
			}

			Icon(
				painter = result.icon(),
				contentDescription = result.contentDescription(),
				tint = MaterialTheme.colorScheme.onSurface,
			)
		}
	}
}