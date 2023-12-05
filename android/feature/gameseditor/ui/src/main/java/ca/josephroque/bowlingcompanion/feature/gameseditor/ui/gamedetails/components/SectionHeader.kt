package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SectionHeader(
	title: String,
	modifier: Modifier = Modifier,
	subtitle: String? = null,
	action: (@Composable () -> Unit)? = null,
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier.fillMaxWidth(),
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(4.dp),
			modifier = Modifier.weight(1f)
		) {
			Text(
				text = title,
				style = MaterialTheme.typography.titleMedium,
			)

			subtitle?.let {
				Text(
					text = it,
					style = MaterialTheme.typography.bodySmall,
				)
			}
		}

		action?.invoke()
	}
}