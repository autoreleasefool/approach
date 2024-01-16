package ca.josephroque.bowlingcompanion.feature.bowlerform.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.designsystem.components.ArchiveDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.DiscardChangesDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.FormSection

@Composable
fun BowlerForm(
	state: BowlerFormUiState,
	onAction: (BowlerFormUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	if (state.isShowingArchiveDialog) {
		ArchiveDialog(
			itemName = state.name,
			onArchive = { onAction(BowlerFormUiAction.ConfirmArchiveClicked) },
			onDismiss = { onAction(BowlerFormUiAction.DismissArchiveClicked) },
		)
	}

	if (state.isShowingDiscardChangesDialog) {
		DiscardChangesDialog(
			onDiscardChanges = { onAction(BowlerFormUiAction.DiscardChangesClicked) },
			onDismiss = { onAction(BowlerFormUiAction.CancelDiscardChangesClicked) },
		)
	}

	Column(
		modifier = modifier
			.verticalScroll(rememberScrollState())
			.imePadding(),
	) {
		FormSection(titleResourceId = R.string.bowler_form_section_details) {
			NameTextField(
				name = state.name,
				onNameChanged = { onAction(BowlerFormUiAction.NameChanged(it)) },
				nameErrorId = state.nameErrorId,
				onDoneClicked = { onAction(BowlerFormUiAction.DoneClicked) },
				modifier = Modifier.padding(horizontal = 16.dp),
			)
		}

		if (state.isArchiveButtonEnabled) {
			Button(
				onClick = { onAction(BowlerFormUiAction.ArchiveClicked) },
				colors = ButtonDefaults.buttonColors(containerColor = colorResource(RCoreDesign.color.destructive)),
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp, vertical = 8.dp),
			) {
				Text(text = stringResource(R.string.bowler_form_archive))
			}
		}
	}
}

@Composable
private fun NameTextField(
	name: String,
	onNameChanged: (String) -> Unit,
	nameErrorId: Int?,
	onDoneClicked: () -> Unit,
	modifier: Modifier = Modifier,
) {
	OutlinedTextField(
		value = name,
		onValueChange = onNameChanged,
		leadingIcon = {
			Icon(
				Icons.Filled.Person,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		},
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
		keyboardOptions = KeyboardOptions(
			imeAction = ImeAction.Done,
			capitalization = KeyboardCapitalization.Words,
		),
		keyboardActions = KeyboardActions(onDone = { onDoneClicked() }),
		modifier = modifier.fillMaxWidth(),
	)
}