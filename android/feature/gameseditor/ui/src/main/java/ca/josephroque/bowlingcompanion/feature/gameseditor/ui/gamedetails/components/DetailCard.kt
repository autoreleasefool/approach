package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R

@Composable
internal fun DetailCard(
	modifier: Modifier = Modifier,
	title: String?,
	action: (@Composable () -> Unit)? = null,
	content: @Composable ColumnScope.() -> Unit,
) {
	Card(
		modifier = modifier
			.fillMaxWidth(),
	) {
		if (title != null || action != null) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.fillMaxWidth()
					.padding(8.dp),
			) {
				if (title != null) {
					Surface(
						shape = RoundedCornerShape(corner = CornerSize(8.dp)),
						color = MaterialTheme.colorScheme.surface,
					) {
						Text(
							text = title,
							style = MaterialTheme.typography.titleMedium,
							modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
						)
					}
				}

				Spacer(modifier = Modifier.weight(1f))

				action?.invoke()
			}
		}

		Column(
			modifier = Modifier
				.padding(horizontal = 8.dp)
				.padding(bottom = 8.dp),
		) {
			content()
		}
	}
}

@Preview
@Composable
private fun DetailCardPreview() {
	DetailCard(
		title = stringResource(R.string.game_editor_gear_title),
		action = {
			IconButton(onClick = {}) {
				Icon(
					Icons.Default.Edit,
					contentDescription = stringResource(RCoreDesign.string.action_manage),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		},
		modifier = Modifier.padding(horizontal = 16.dp),
	) {
		Text(
			text = "Here is some text to display in the preview",
			style = MaterialTheme.typography.bodySmall,
		)

		Text(
			text = "And this is some more text that actually extends the full width of the screen and even onto the next line if you can believe it",
			style = MaterialTheme.typography.bodySmall,
		)
	}
}