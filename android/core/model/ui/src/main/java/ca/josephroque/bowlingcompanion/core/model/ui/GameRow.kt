package ca.josephroque.bowlingcompanion.core.model.ui

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

@Composable
fun GameRow(index: Int, score: Int, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier
			.fillMaxWidth()
			.then(
				if (onClick != null) {
					Modifier
						.clickable(onClick = onClick)
						.padding(16.dp)
				} else {
					Modifier
				},
			),
	) {
		Text(
			text = stringResource(
				ca.josephroque.bowlingcompanion.core.designsystem.R.string.game_with_ordinal,
				index + 1,
			),
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.weight(1f),
		)

		Text(
			text = score.toString(),
			style = MaterialTheme.typography.bodyLarge,
			maxLines = 1,
		)
	}
}

@Preview
@Composable
private fun GameRowPreview() {
	Surface {
		GameRow(
			index = 0,
			score = 234,
			onClick = {},
		)
	}
}
