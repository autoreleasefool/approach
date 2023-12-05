package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
internal fun NavigationButton(
	title: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	subtitle: String? = null,
	icon: (@Composable () -> Unit)? = null,
	trailingIcon: (@Composable () -> Unit)? = null,
) {
	Surface(
		modifier = modifier
			.heightIn(min = 56.dp)
			.border(
				width = 1.dp,
				color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
				shape = RoundedCornerShape(8.dp),
			),
		shape = RoundedCornerShape(corner = CornerSize(8.dp)),
		color = MaterialTheme.colorScheme.surface,
		onClick = onClick,
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(16.dp),
			modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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

				subtitle?.let {
					Text(
						text = it,
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
					)
				}
			}

			trailingIcon?.invoke()
		}
	}
}

@Preview
@Composable
fun DetailNavigationButtonPreview() {
	Surface {
		NavigationButton(
			title = "Title",
			subtitle = "Subtitle",
			onClick = {},
		)
	}
}