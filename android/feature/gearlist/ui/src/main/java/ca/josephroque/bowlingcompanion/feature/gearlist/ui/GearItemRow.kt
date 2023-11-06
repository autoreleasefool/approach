package ca.josephroque.bowlingcompanion.feature.gearlist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import ca.josephroque.bowlingcompanion.core.model.ui.AvatarImage
import ca.josephroque.bowlingcompanion.core.model.ui.icon
import java.util.UUID

@Composable
internal fun GearItemRow(
	gear: GearListItem,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier = modifier
			.fillMaxWidth()
			.clickable(onClick = onClick)
			.padding(16.dp),
	) {
		AvatarImage(avatar = gear.avatar, modifier = Modifier.size(24.dp))

		Column(
			horizontalAlignment = Alignment.Start,
			modifier = Modifier.weight(1f),
		) {
			Text(
				text = gear.name,
				style = MaterialTheme.typography.titleMedium,
			)

			gear.ownerName?.let {
				Text(
					text = stringResource(R.string.gear_list_owned_by, it),
					style = MaterialTheme.typography.bodySmall,
				)
			}
		}

		Icon(
			painter = gear.kind.icon(),
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onSurface,
			modifier = Modifier.size(24.dp),
		)
	}
}

@Preview
@Composable
private fun GearItemRowPreview() {
	Surface {
		GearItemRow(
			gear = GearListItem(
				id = UUID.randomUUID(),
				name = "Yellow Ball",
				kind = GearKind.BOWLING_BALL,
				ownerName = "Joseph",
				avatar = Avatar.default(),
			),
			onClick = {},
		)
	}
}