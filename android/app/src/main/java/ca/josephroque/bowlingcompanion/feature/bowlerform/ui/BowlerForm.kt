package ca.josephroque.bowlingcompanion.feature.bowlerform.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.R

@Composable
internal fun BowlerForm(
	name: String,
	errorId: Int?,
	onNameChanged: (String) -> Unit,
	onDoneClicked: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier.fillMaxSize()
	) {
		OutlinedTextField(
			value = name,
			onValueChange = onNameChanged,
			leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
			label = { Text(stringResource(R.string.bowler_form_name)) },
			singleLine = true,
			isError = errorId != null,
			supportingText = {
				errorId?.let {
					Text(
						text = stringResource(it),
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.error,
						modifier = Modifier.fillMaxWidth(),
					)
				}
			},
			trailingIcon = {
				if (errorId != null) {
					Icon(
						Icons.Default.Warning,
						tint = MaterialTheme.colorScheme.error,
						contentDescription = null
					)
				}
			},
			keyboardOptions = KeyboardOptions(
				imeAction = ImeAction.Done,
			),
			keyboardActions = KeyboardActions(
				onDone = { onDoneClicked() },
			),
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
		)
	}
}