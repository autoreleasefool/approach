package ca.josephroque.bowlingcompanion.core.designsystem.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.R

@Composable
fun Stepper(
	title: String,
	value: Int,
	onValueChanged: (Int) -> Unit,
	step: Int = 1,
	range: IntRange? = null,
	modifier: Modifier = Modifier,
) {
	val changeValue = { newValue: Int ->
		val coercedValue = newValue.coerceIn(range ?: Int.MIN_VALUE..Int.MAX_VALUE)
		onValueChanged(coercedValue)
	}

	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp),
	) {
		OutlinedTextField(
			value = value.toString(),
			onValueChange = {
				if (value == 1 && it.matches(Regex("^\\d1$"))) {
					changeValue(it.first().digitToIntOrNull() ?: 1)
				} else {
					changeValue(it.toIntOrNull() ?: 1)
				}
			},
			label = { Text(title) },
			singleLine = true,
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
			modifier = Modifier.weight(1f),
		)

		IconButton(onClick = { changeValue(value - step) }) {
			Icon(
				painter = painterResource(R.drawable.ic_minus_circle),
				contentDescription = stringResource(R.string.cd_decrement),
				tint = MaterialTheme.colorScheme.onSurface,
			)
		}

		IconButton(onClick = { changeValue(value + step) }) {
			Icon(
				painter = painterResource(R.drawable.ic_add_circle),
				contentDescription = stringResource(R.string.cd_increment),
				tint = MaterialTheme.colorScheme.onSurface,
			)
		}
	}
}
