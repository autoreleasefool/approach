package ca.josephroque.bowlingcompanion.core.designsystem.components.form

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

data class HeaderAction(
	@StringRes val actionResourceId: Int,
	val onClick: () -> Unit,
)

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
			Row(
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp, vertical = 8.dp),
			) {
				Text(
					text = stringResource(it),
					style = MaterialTheme.typography.titleMedium,
					modifier = Modifier
						.weight(1f)
						.alignBy(LastBaseline),
				)

				headerAction?.let {
					Text(
						text = stringResource(it.actionResourceId),
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier
							.clickable(onClick = it.onClick)
							.padding(8.dp)
							.alignBy(LastBaseline),
					)
				}
			}
		}

		content()

		footerResourceId?.let {
			Text(
				text = stringResource(it),
				style = MaterialTheme.typography.bodySmall,
				modifier = Modifier
					.fillMaxWidth()
					.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
			)
		}
	}
}
