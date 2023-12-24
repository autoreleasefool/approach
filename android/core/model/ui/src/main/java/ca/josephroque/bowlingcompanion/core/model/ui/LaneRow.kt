package ca.josephroque.bowlingcompanion.core.model.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import ca.josephroque.bowlingcompanion.core.model.LanePosition
import ca.josephroque.bowlingcompanion.core.model.ui.R.*

@Composable
fun LaneRow(
	lane: LaneListItem,
	modifier: Modifier = Modifier,
	onClick: (() -> Unit)? = null,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier = modifier
			.fillMaxWidth()
			.then(if (onClick != null)
				Modifier
					.clickable(onClick = onClick)
					.padding(16.dp)
			else Modifier),
	) {
		Text(
			text = lane.label,
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.weight(1f),
		)

		when (lane.position) {
			LanePosition.LEFT_WALL -> Icon(
				painter = painterResource(drawable.ic_lane_position_left_wall),
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.size(24.dp),
			)
			LanePosition.RIGHT_WALL -> Icon(
				painter = painterResource(drawable.ic_lane_position_right_wall),
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.size(24.dp),
			)
			LanePosition.NO_WALL -> Unit
		}
	}
}