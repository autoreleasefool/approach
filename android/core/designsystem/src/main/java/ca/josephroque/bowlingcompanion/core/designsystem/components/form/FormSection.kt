package ca.josephroque.bowlingcompanion.core.designsystem.components.form

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.HeaderAction
import ca.josephroque.bowlingcompanion.core.designsystem.components.SectionFooter
import ca.josephroque.bowlingcompanion.core.designsystem.components.SectionHeader

@Composable
fun FormSection(
	modifier: Modifier = Modifier,
	@StringRes titleResourceId: Int? = null,
	@StringRes footerResourceId: Int? = null,
	headerAction: HeaderAction? = null,
	content: @Composable () -> Unit,
) {
	Column(
		modifier = modifier,
	) {
		titleResourceId?.let {
			FormSectionHeader(titleResourceId = it, headerAction = headerAction)
		}

		content()

		footerResourceId?.let {
			FormSectionFooter(footerResourceId = it)
		}
	}
}

@Composable
fun FormSectionHeader(
	titleResourceId: Int,
	modifier: Modifier = Modifier,
	headerAction: HeaderAction? = null,
) {
	SectionHeader(
		title = stringResource(titleResourceId),
		modifier = modifier,
		headerAction = headerAction,
	)
}

@Composable
fun FormSectionFooter(footerResourceId: Int, modifier: Modifier = Modifier) {
	SectionFooter(
		footer = stringResource(footerResourceId),
		modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
	)
}
