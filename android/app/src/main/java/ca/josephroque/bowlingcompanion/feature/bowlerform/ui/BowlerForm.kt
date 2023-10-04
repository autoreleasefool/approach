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
import ca.josephroque.bowlingcompanion.core.components.form.FormSection

@Composable
internal fun BowlerForm(
	name: String,
	onNameChanged: (String) -> Unit,
	nameErrorId: Int?,
	onDoneClicked: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier.fillMaxSize()) {
		FormSection(titleResourceId = R.string.bowler_form_section_details) {
			NameTextField(
				name = name,
				onNameChanged = onNameChanged,
				nameErrorId = nameErrorId,
				onDoneClicked = onDoneClicked,
				modifier = Modifier.padding(horizontal = 16.dp),
			)
		}
	}
}

@Composable
internal fun NameTextField(
	name: String,
	onNameChanged: (String) -> Unit,
	nameErrorId: Int?,
	onDoneClicked: () -> Unit,
	modifier: Modifier = Modifier,
) {
	OutlinedTextField(
		value = name,
		onValueChange = onNameChanged,
		leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
		label = {
			Text(
				text = stringResource(R.string.bowler_form_name),
				style = MaterialTheme.typography.bodyLarge
			)
		},
		singleLine = true,
		isError = nameErrorId != null,
		supportingText = {
			nameErrorId?.let {
				Text(
					text = stringResource(it),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.error,
					modifier = Modifier.fillMaxWidth(),
				)
			}
		},
		trailingIcon = {
			if (nameErrorId != null) {
				Icon(
					Icons.Default.Warning,
					tint = MaterialTheme.colorScheme.error,
					contentDescription = null
				)
			}
		},
		keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done,),
		keyboardActions = KeyboardActions(onDone = { onDoneClicked() }),
		modifier = modifier.fillMaxWidth(),
	)
}