package ca.josephroque.bowlingcompanion.core.designsystem.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.R

@Composable
fun Tip(@StringRes title: Int, @StringRes message: Int, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
	Card(modifier = modifier) {
		Column {
			Row(
				modifier = Modifier.fillMaxWidth(),
			) {
				Text(
					text = stringResource(title),
					style = MaterialTheme.typography.titleMedium,
					modifier = Modifier
						.weight(1f)
						.padding(horizontal = 16.dp)
						.padding(top = 16.dp, bottom = 8.dp),
				)

				IconButton(
					onClick = onDismiss,
					modifier = Modifier.padding(top = 8.dp, end = 8.dp),
				) {
					Icon(
						Icons.Default.Close,
						contentDescription = stringResource(R.string.action_dismiss),
					)
				}
			}

			Text(
				text = stringResource(message),
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.padding(bottom = 16.dp),
			)
		}
	}
}

@Preview
@Composable
private fun TipPreview() {
	Tip(
		title = R.string.archive_dialog_title,
		message = R.string.archive_dialog_message,
		onDismiss = {},
	)
}
