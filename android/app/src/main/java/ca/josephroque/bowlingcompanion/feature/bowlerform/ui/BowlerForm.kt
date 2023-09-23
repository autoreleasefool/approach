package ca.josephroque.bowlingcompanion.feature.bowlerform.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.feature.bowlerform.BowlerFormFieldErrors
import ca.josephroque.bowlingcompanion.feature.bowlerform.BowlerFormScreen
import ca.josephroque.bowlingcompanion.feature.bowlerform.BowlerFormUiState

@OptIn(ExperimentalMaterial3Api::class)
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
			label = { Text(stringResource(R.string.bowler_form_name)) },
			singleLine = true,
			isError = errorId != null,
			supportingText = {
				if (errorId != null) {
					Text(
						text = stringResource(errorId),
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

@Preview
@Composable
fun BowlerFormPreview() {
	BowlerFormScreen(
		bowlerFormUiState = BowlerFormUiState.Create(
			name = "Joseph",
			kind = BowlerKind.PLAYABLE,
			fieldErrors = BowlerFormFieldErrors(nameErrorId = R.string.bowler_form_name_missing)
		),
		saveBowler = {},
		loadBowler = {},
		deleteBowler = {},
		updateName = {},
	)
}