package ca.josephroque.bowlingcompanion.feature.settings.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun Header(
	@StringRes titleResourceId: Int,
	modifier: Modifier = Modifier,
	color: Color = Color.Unspecified,
) {
	Text(
		text = stringResource(titleResourceId).uppercase(),
		color = color,
		style = MaterialTheme.typography.labelMedium,
		modifier = modifier
			.fillMaxWidth()
			.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
	)
}

@Composable
internal fun Footer(
	@StringRes titleResourceId: Int,
	modifier: Modifier = Modifier,
	color: Color = Color.Unspecified,
) {
	Text(
		text = stringResource(titleResourceId),
		color = color,
		style = MaterialTheme.typography.bodySmall,
		modifier = modifier
			.fillMaxWidth()
			.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
	)
}
