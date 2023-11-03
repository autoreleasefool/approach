package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.RoundIconButton
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.components.DetailCard
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.components.icon
import java.util.UUID

@Composable
internal fun GearCard(
	state: GearCardUiState,
	manageGear: () -> Unit,
	modifier: Modifier = Modifier,
) {
	DetailCard(
		title = stringResource(R.string.game_editor_gear_title),
		action = {
			RoundIconButton(onClick = manageGear) {
				Icon(
					Icons.Default.Edit,
					contentDescription = stringResource(RCoreDesign.string.action_manage),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		},
		modifier = modifier,
	) {
		Text(
			text = stringResource(R.string.game_editor_gear_description),
			style = MaterialTheme.typography.bodySmall,
			modifier = Modifier
				.padding(horizontal = 8.dp)
				.padding(bottom = 8.dp),
		)

		state.selectedGear.forEachIndexed{ index, gear ->
			GearItemRow(gear = gear)
			if (index != state.selectedGear.lastIndex) {
				Divider(modifier = Modifier.padding(start = 8.dp))
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
			modifier = Modifier.weight(1f),
		)

		Icon(
			painter = gear.kind.icon(),
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onSurface,
			modifier = Modifier.size(24.dp),
		)
	}
}

data class GearCardUiState(
	val selectedGear: List<GearListItem> = emptyList(),
)

@Preview
@Composable
private fun GearCardPreview() {
	GearCard(
		state = GearCardUiState(
			selectedGear = listOf(
				GearListItem(id = UUID.randomUUID(), name = "Yellow Ball", kind = GearKind.BOWLING_BALL, ownerName = "Joseph"),
				GearListItem(id = UUID.randomUUID(), name = "Green Towel", kind = GearKind.TOWEL, ownerName = "Sarah"),
			),
		),
		manageGear = {},
	)
}