package ca.josephroque.bowlingcompanion.feature.settings.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.R

@Composable
internal fun NavigationItem(
	@StringRes titleResourceId: Int,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	@StringRes descriptionResourceId: Int? = null,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier
			.fillMaxWidth()
			.clickable(onClick = onClick)
			.padding(16.dp),
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(4.dp),
			modifier = Modifier.weight(1f),
		) {
			Text(
				text = stringResource(titleResourceId),
				style = MaterialTheme.typography.titleMedium,
				modifier = Modifier.fillMaxWidth(),
			)

			descriptionResourceId?.let {
				Text(
					text = stringResource(it),
					style = MaterialTheme.typography.bodySmall,
					modifier = Modifier.fillMaxWidth(),
				)
			}
		}

		Icon(
			painter = painterResource(R.drawable.ic_chevron_right),
			contentDescription = null,
			tint = MaterialTheme.colorScheme.onSurfaceVariant,
			modifier = Modifier.size(24.dp),
		)
	}
}