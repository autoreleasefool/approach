package ca.josephroque.bowlingcompanion.feature.overview.ui.quickplay.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.overview.ui.R

@Composable
fun QuickPlayOnboarding(
	onAction: (QuickPlayOnboardingUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		horizontalAlignment = Alignment.Start,
		verticalArrangement = Arrangement.spacedBy(8.dp),
		modifier = modifier.padding(horizontal = 16.dp),
	) {
		ActionRow(
			title = stringResource(R.string.quick_play_start_with_a_bowler),
			subtitle = stringResource(R.string.quick_play_start_with_a_bowler_description),
			icon = {
				Icon(
					Icons.Default.Person,
					contentDescription = null,
				)
			},
		)

		ActionRow(
			title = stringResource(R.string.quick_play_choose_bowlers),
			subtitle = stringResource(R.string.quick_play_choose_bowlers_description),
			icon = {
				Icon(
					painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_add_person),
					contentDescription = null,
					tint = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.purple_500),
				)
			},
		)

		ActionRow(
			title = stringResource(R.string.quick_play_tap_to_edit),
			icon = {
				Icon(
					Icons.Default.Edit,
					contentDescription = null,
					tint = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.blue_500),
				)
			},
		)

		ActionRow(
			title = stringResource(R.string.quick_play_swipe_to_delete),
			icon = {
				Icon(
					Icons.Default.Delete,
					contentDescription = null,
					tint = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive),
				)
			},
		)

		ActionRow(
			title = stringResource(R.string.quick_play_long_press_to_reorder),
			icon = {
				Icon(
					Icons.Default.Menu,
					contentDescription = null,
					tint = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.purple_100)
				)
			},
		)

		Button(
			onClick = { onAction(QuickPlayOnboardingUiAction.DoneClicked) },
			modifier = Modifier.fillMaxWidth(),
		) {
			Text(text = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_done))
		}
	}
}

@Composable
private fun ActionRow(
	title: String,
	modifier: Modifier = Modifier,
	subtitle: String? = null,
	icon: @Composable () -> Unit = {},
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier
			.padding(vertical = 8.dp),
	) {
		icon()

		Column(
			horizontalAlignment = Alignment.Start,
			verticalArrangement = Arrangement.spacedBy(2.dp),
		) {
			Text(
				text = title,
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.Bold,
			)

			if (subtitle != null) {
				Text(
					text = subtitle,
					style = MaterialTheme.typography.bodySmall,
				)
			}
		}
	}
}

@Preview
@Composable
private fun QuickPlayOnboardingPreview() {
	Surface {
		QuickPlayOnboarding(onAction = {})
	}
}