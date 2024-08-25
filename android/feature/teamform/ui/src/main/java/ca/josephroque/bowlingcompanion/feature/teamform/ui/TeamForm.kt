package ca.josephroque.bowlingcompanion.feature.teamform.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.DeleteDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.DiscardChangesDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.FormSection
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.FormSectionFooter
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.FormSectionHeader
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.HeaderAction
import ca.josephroque.bowlingcompanion.core.model.ui.BowlerRow

@Composable
fun TeamForm(
	state: TeamFormUiState,
	onAction: (TeamFormUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	if (state.isShowingDeleteDialog) {
		DeleteDialog(
			itemName = state.name,
			onDelete = { onAction(TeamFormUiAction.ConfirmDeleteClicked) },
			onDismiss = { onAction(TeamFormUiAction.DismissDeleteClicked) },
		)
	}
	
	if (state.isShowingDiscardChangesDialog) {
		DiscardChangesDialog(
			onDiscardChanges = { onAction(TeamFormUiAction.DiscardChangesClicked) },
			onDismiss = { onAction(TeamFormUiAction.CancelDiscardChangesClicked) },
		)
	}
	
	LazyColumn(
		modifier = modifier
			.fillMaxSize()
			.imePadding(),
	) {
		item {
			FormSection(titleResourceId = R.string.team_form_details) {
				TeamNameField(
					name = state.name,
					onNameChanged = { onAction(TeamFormUiAction.NameChanged(it)) },
					errorId = state.nameErrorId,
				)
			}

			HorizontalDivider()

			FormSectionHeader(
				titleResourceId = R.string.team_form_team_members,
				headerAction = HeaderAction(
					actionResourceId = R.string.team_form_manage_team_members,
					onClick = { onAction(TeamFormUiAction.ManageTeamMembersClicked) },
				),
				modifier = Modifier.padding(top = 16.dp),
			)

			if (state.members.isEmpty()) {
				Text(
					text = stringResource(R.string.team_form_property_team_members_none),
					style = MaterialTheme.typography.bodyMedium,
					modifier = Modifier.padding(16.dp),
				)
			}

			state.membersErrorId?.let {
				Text(
					text = stringResource(it),
					color = MaterialTheme.colorScheme.error,
					modifier = Modifier.padding(16.dp),
				)
			}
		}

		items(
			items = state.members,
			key = { it.id },
		) {
			BowlerRow(
				name = it.name,
				modifier = Modifier.padding(16.dp),
			)
		}

		item {
			FormSectionFooter(footerResourceId = R.string.team_form_team_members_footer)
		}
	}
}

@Composable
private fun TeamNameField(name: String, onNameChanged: ((String) -> Unit)?, errorId: Int?) {
	OutlinedTextField(
		value = name,
		onValueChange = onNameChanged ?: {},
		label = { Text(stringResource(R.string.team_form_property_name)) },
		singleLine = true,
		isError = errorId != null,
		keyboardOptions = KeyboardOptions(KeyboardCapitalization.Words),
		supportingText = {
			errorId?.let {
				Text(
					text = stringResource(it),
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
					contentDescription = null,
				)
			}
		},
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp),
	)
}
