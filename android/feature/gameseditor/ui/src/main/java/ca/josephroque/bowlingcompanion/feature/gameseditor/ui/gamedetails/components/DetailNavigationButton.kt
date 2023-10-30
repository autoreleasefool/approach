package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign

@Composable
internal fun DetailNavigationButton(
	modifier: Modifier = Modifier,
	title: String,
	subtitle: String? = null,
	icon: (@Composable () -> Unit)? = null,
	onClick: () -> Unit,
) {
	Surface(
		modifier = modifier.heightIn(min = 56.dp),
		shape = RoundedCornerShape(corner = CornerSize(8.dp)),
		color = MaterialTheme.colorScheme.surface,
		onClick = onClick,
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(8.dp),
		) {
			icon?.invoke()

			Column(
				horizontalAlignment = Alignment.Start,
				modifier = Modifier.weight(1f),
			) {
				Text(
					text = title,
					style = MaterialTheme.typography.bodyLarge,
					color = MaterialTheme.colorScheme.onSurface,
				)

				if (subtitle != null) {
					Text(
						text = subtitle,
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}
			}

			Icon(
				painter = painterResource(RCoreDesign.drawable.ic_chevron_right),
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSurface,
			)
		}
	}
}