package ca.josephroque.bowlingcompanion.feature.opponentslist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.OpponentListItem
import java.util.UUID

@Composable
internal fun OpponentItemRow(
	opponent: OpponentListItem,
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
			text = opponent.name,
			style = MaterialTheme.typography.titleMedium,
		)

		Spacer(Modifier.weight(1F))

		when (opponent.kind) {
			BowlerKind.OPPONENT -> Unit
			BowlerKind.PLAYABLE -> Icon(
				Icons.Filled.Person,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSurface,
				modifier = Modifier.size(24.dp),
			)
		}
	}
}

@Preview
@Composable
private fun OpponentItemPreview() {
	Surface {
		OpponentItemRow(
			opponent = OpponentListItem(id = UUID.randomUUID(), name = "Joseph", kind = BowlerKind.PLAYABLE),
			onClick = {},
			modifier = Modifier.padding(16.dp),
		)
	}
}