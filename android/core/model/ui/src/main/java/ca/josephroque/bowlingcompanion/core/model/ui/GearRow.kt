package ca.josephroque.bowlingcompanion.core.model.ui

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

@Composable
fun GearRow(
	name: String,
	ownerName: String?,
	kind: GearKind,
	avatar: Avatar,
	modifier: Modifier = Modifier,
	onClick: (() -> Unit)? = null,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier
			.fillMaxWidth()
			.then(if (onClick != null)
				Modifier
					.clickable(onClick = onClick)
					.padding(16.dp)
			else Modifier),
	) {
		AvatarImage(avatar = avatar, modifier = Modifier.size(24.dp))

		Column(
			horizontalAlignment = Alignment.Start,
			modifier = Modifier.weight(1f),
		) {
			Text(
				text = name,
				style = MaterialTheme.typography.titleMedium,
			)

			ownerName?.let {
				Text(
					text = stringResource(R.string.gear_owned_by, it),
					style = MaterialTheme.typography.bodySmall,
				)
			}
		}

		Icon(
			painter = kind.icon(),
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
		GearRow(
			name = "Yellow Ball",
			kind = GearKind.BOWLING_BALL,
			ownerName = "Joseph",
			avatar = Avatar.default(),
			onClick = {},
		)
	}
}