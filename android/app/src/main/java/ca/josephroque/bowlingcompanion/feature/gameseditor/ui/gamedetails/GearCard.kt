package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.components.icon
import ca.josephroque.bowlingcompanion.core.model.GearListItem

@Composable
internal fun GearCard(
	selectedGear: List<GearListItem>,
	manageGear: () -> Unit,
	modifier: Modifier = Modifier,
) {
	OutlinedCard(modifier = modifier) {
		Text(
			text = stringResource(R.string.game_editor_gear_title),
			style = MaterialTheme.typography.bodyMedium,
		)

		Text(
			text = stringResource(R.string.game_editor_gear_description),
			style = MaterialTheme.typography.bodySmall,
		)

		selectedGear.forEachIndexed{ index, gear ->
			GearItemRow(gear = gear)
			if (index != selectedGear.lastIndex) {
				Divider(modifier = Modifier.padding(start = 16.dp))
			}
		}

		Row {
			Spacer(modifier = Modifier.weight(1f))
			TextButton(onClick = manageGear) {
				Text(text = stringResource(R.string.action_manage))
			}
		}
	}
}

@Composable
private fun GearItemRow(
	modifier: Modifier = Modifier,
	gear: GearListItem,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier = modifier
			.fillMaxWidth()
			.padding(8.dp)
	) {
		// TODO: add avatar

		Text(
			text = gear.name,
			style = MaterialTheme.typography.bodyMedium,
		)

		Icon(
			painter = gear.kind.icon(),
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onSurface,
			modifier = Modifier.size(20.dp),
		)
	}
}