package ca.josephroque.bowlingcompanion.core.designsystem.components.list

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

fun LazyListScope.header(
	@StringRes titleResourceId: Int,
	action: HeaderAction? = null,
) {
	item { ListSectionHeader(titleResourceId = titleResourceId, action = action) }
}

data class HeaderAction(
	@StringRes val actionResourceId: Int,
	val onClick: () -> Unit,
)

@Composable
fun ListSectionHeader(
	@StringRes titleResourceId: Int,
	modifier: Modifier = Modifier,
	action: HeaderAction? = null,
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
	) {
		Text(
			text = stringResource(titleResourceId),
			style = MaterialTheme.typography.titleLarge,
			modifier = modifier
				.weight(1f)
				.alignBy(LastBaseline)
		)

		action?.let {
			Text(
				text = stringResource(it.actionResourceId),
				style = MaterialTheme.typography.bodyMedium,
				modifier = modifier
					.clickable(onClick = it.onClick)
					.padding(8.dp)
					.alignBy(LastBaseline),
			)
		}
	}
}

fun LazyListScope.footer(@StringRes footerResourceId: Int) {
	item { ListSectionFooter(footer = stringResource(footerResourceId)) }
}

fun LazyListScope.footer(footer: String) {
	item { ListSectionFooter(footer = footer) }
}

@Composable
fun ListSectionFooter(
	footer: String,
	modifier: Modifier = Modifier,
) {
	Text(
		text = footer,
		style = MaterialTheme.typography.bodySmall,
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
	)
}