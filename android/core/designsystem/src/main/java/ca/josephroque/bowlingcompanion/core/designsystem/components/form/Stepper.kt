package ca.josephroque.bowlingcompanion.core.designsystem.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.R

@Composable
fun Stepper(
	title: String,
	value: Int,
	onValueChanged: (Int) -> Unit,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp),
	) {
		IconButton(onClick = { onValueChanged(value - 1) }) {
			Icon(
				painter = painterResource(R.drawable.ic_minus_circle),
				contentDescription = stringResource(R.string.cd_decrement),
				tint = MaterialTheme.colorScheme.onSurface,
			)
		}

		OutlinedTextField(
			value = value.toString(),
			onValueChange = {
				val intValue = it.toIntOrNull() ?: 1
				onValueChanged(intValue)
			},
			label = { Text(title) },
			singleLine = true,
			modifier = Modifier.weight(1f),
		)

		IconButton(onClick = { onValueChanged(value + 1) }) {
			Icon(
				painter = painterResource(R.drawable.ic_add_circle),
				contentDescription = stringResource(R.string.cd_increment),
				tint = MaterialTheme.colorScheme.onSurface,
			)
		}
	}
}