package ca.josephroque.bowlingcompanion.feature.gameslist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import java.util.UUID

@Composable
internal fun GameItemRow(
	game: GameListItem,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier
			.fillMaxWidth()
			.clickable(onClick = onClick)
			.padding(16.dp),
	) {
		Text(
			text = stringResource(R.string.game_with_ordinal, game.index + 1),
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.weight(1f),
		)

		Text(
			text = game.score.toString(),
			style = MaterialTheme.typography.bodyLarge,
			maxLines = 1,
		)
	}
}

@Preview
@Composable
private fun GameItemPreview() {
	Surface {
		GameItemRow(
			game = GameListItem(
				id = UUID.randomUUID(),
				index = 0,
				score = 234,
			),
			onClick = {},
		)
	}
}