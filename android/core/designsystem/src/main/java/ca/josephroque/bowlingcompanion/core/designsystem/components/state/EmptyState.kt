package ca.josephroque.bowlingcompanion.core.designsystem.components.state

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun MutedEmptyState(
	@StringRes title: Int,
	@DrawableRes icon: Int,
	@StringRes message: Int,
	modifier: Modifier = Modifier,
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp),
	) {
		Image(
			painter = painterResource(icon),
			contentDescription = null,
			modifier = Modifier
				.widthIn(max = 160.dp)
				.heightIn(max = 160.dp),
		)

		Text(
			text = stringResource(title),
			style = MaterialTheme.typography.titleMedium,
			textAlign = TextAlign.Center,
			modifier = Modifier.fillMaxWidth(),
		)

		Text(
			text = stringResource(message),
			style = MaterialTheme.typography.bodyMedium,
			textAlign = TextAlign.Center,
			modifier = Modifier.fillMaxWidth(),
		)
	}
}

@Composable
fun DefaultEmptyState(
	@StringRes title: Int,
	@DrawableRes icon: Int,
	@StringRes message: Int,
	@StringRes action: Int,
	onActionClick: () -> Unit,
) {
	EmptyState(
		icon = {
			Image(
				painter = painterResource(icon),
				contentDescription = null,
				modifier = Modifier
					.fillMaxSize()
					.padding(bottom = 8.dp),
			)
		},
		title = {
			Text(
				text = stringResource(title),
				style = MaterialTheme.typography.titleMedium,
				textAlign = TextAlign.Center,
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp),
			)
		},
		message = {
			Text(
				text = stringResource(message),
				style = MaterialTheme.typography.bodyMedium,
				textAlign = TextAlign.Center,
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp),
			)
		},
		action = {
			Button(onClick = onActionClick) {
				Text(text = stringResource(action))
			}
		},
	)
}

@Composable
fun EmptyState(
	icon: @Composable BoxScope.() -> Unit,
	title: @Composable ColumnScope.() -> Unit,
	modifier: Modifier = Modifier,
	message: (@Composable ColumnScope.() -> Unit)? = null,
	action: (@Composable ColumnScope.() -> Unit)? = null,
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = modifier
			.fillMaxSize()
			.padding(16.dp),
	) {
		Spacer(modifier = Modifier.weight(1f))

		Box(modifier = Modifier.heightIn(max = 160.dp)) {
			icon()
		}

		Spacer(modifier = Modifier.weight(1f))

		title()
		message?.invoke(this)
		action?.invoke(this)
	}
}
