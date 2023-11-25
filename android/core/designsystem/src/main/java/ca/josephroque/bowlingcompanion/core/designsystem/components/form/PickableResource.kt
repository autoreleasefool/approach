package ca.josephroque.bowlingcompanion.core.designsystem.components.form

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickableResourceCard(
	resourceName: String,
	selectedName: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
) {
	Card(
		onClick = onClick,
		modifier = modifier,
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.padding(16.dp)
		) {
			Text(
				text = resourceName,
				style = MaterialTheme.typography.titleMedium,
				color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
				modifier = Modifier.weight(1f),
			)

			Text(
				text = selectedName,
				style = MaterialTheme.typography.bodyMedium,
				color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
			)

			Icon(
				painter = painterResource(R.drawable.ic_chevron_right),
				contentDescription = null,
				tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
			)
		}
	}
}