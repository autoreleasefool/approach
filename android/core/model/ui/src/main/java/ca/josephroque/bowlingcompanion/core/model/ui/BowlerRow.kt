package ca.josephroque.bowlingcompanion.core.model.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import ca.josephroque.bowlingcompanion.core.model.ui.utils.formatAsAverage

@Composable
fun BowlerRow(
	name: String,
	modifier: Modifier = Modifier,
	average: Double? = null,
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
		Icon(
			Icons.Filled.Person,
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onSurface,
			modifier = Modifier.size(24.dp),
		)

		Text(
			text = name,
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.weight(1f)
		)

		average?.let {
			Text(
				text = it.formatAsAverage(),
				style = MaterialTheme.typography.bodyLarge,
				maxLines = 1
			)
		}
	}
}

@Preview
@Composable
private fun BowlerCardPreview() {
	Surface {
		BowlerRow(
			name = "Joseph",
			average = 120.0,
			onClick = {},
		)
	}
}