package ca.josephroque.bowlingcompanion.core.designsystem.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun Link(
	@StringRes titleResourceId: Int,
	@DrawableRes iconResourceId: Int,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
			.fillMaxWidth()
			.clickable(onClick = onClick)
			.padding(16.dp),
	) {
		Text(
			text = stringResource(id = titleResourceId),
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.weight(1f),
		)

		Icon(
			painter = painterResource(id = iconResourceId),
			contentDescription = null,
			tint = MaterialTheme.colorScheme.primary,
		)
	}
}