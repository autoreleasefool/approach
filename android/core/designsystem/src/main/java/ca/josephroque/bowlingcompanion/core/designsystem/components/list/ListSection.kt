package ca.josephroque.bowlingcompanion.core.designsystem.components.list

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.HeaderAction
import ca.josephroque.bowlingcompanion.core.designsystem.components.SectionFooter
import ca.josephroque.bowlingcompanion.core.designsystem.components.SectionHeader

fun LazyListScope.header(
	@StringRes titleResourceId: Int,
	modifier: Modifier = Modifier,
	action: HeaderAction? = null,
) {
	item {
		ListSectionHeader(
			title = stringResource(titleResourceId),
			action = action,
			modifier = modifier,
		)
	}
}

fun LazyListScope.header(
	title: String,
	modifier: Modifier = Modifier,
	action: HeaderAction? = null,
) {
	item {
		ListSectionHeader(
			title = title,
			action = action,
			modifier = modifier,
		)
	}
}

@Composable
fun ListSectionHeader(
	@StringRes titleResourceId: Int,
	modifier: Modifier = Modifier,
	action: HeaderAction? = null,
) {
	ListSectionHeader(
		title = stringResource(titleResourceId),
		modifier = modifier,
		action = action,
	)
}

@Composable
fun ListSectionHeader(title: String, modifier: Modifier = Modifier, action: HeaderAction? = null) {
	SectionHeader(
		title = title,
		modifier = modifier,
		headerAction = action,
	)
}

fun LazyListScope.footer(@StringRes footerResourceId: Int) {
	item { ListSectionFooter(footer = stringResource(footerResourceId)) }
}

fun LazyListScope.footer(footer: String) {
	item { ListSectionFooter(footer = footer) }
}

@Composable
fun ListSectionFooter(@StringRes footerResourceId: Int) {
	ListSectionFooter(footer = stringResource(footerResourceId))
}

@Composable
fun ListSectionFooter(footer: String, modifier: Modifier = Modifier) {
	SectionFooter(
		footer = footer,
		modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
	)
}
