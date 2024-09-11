package ca.josephroque.bowlingcompanion.core.designsystem.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

data class HeaderAction(@StringRes val actionResourceId: Int, val onClick: () -> Unit)

@Composable
fun SectionHeader(
	title: String,
	modifier: Modifier = Modifier,
	headerAction: HeaderAction? = null,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
	) {
		Text(
			text = title,
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier
				.weight(1f)
				.alignBy(LastBaseline),
		)

		headerAction?.let {
			TextButton(
				onClick = it.onClick,
				modifier = Modifier.alignBy(LastBaseline),
			) {
				Text(
					text = stringResource(it.actionResourceId),
					style = MaterialTheme.typography.bodyMedium,
				)
			}
		}
	}
}

@Composable
fun SectionFooter(footer: String, modifier: Modifier = Modifier) {
	Text(
		text = footer,
		style = MaterialTheme.typography.bodySmall,
		modifier = modifier.fillMaxWidth(),
	)
}
