package ca.josephroque.bowlingcompanion.feature.gearform.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.designsystem.components.DeleteDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.DiscardChangesDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.FormSection
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.PickableResourceCard
import ca.josephroque.bowlingcompanion.core.model.ui.AvatarImage
import ca.josephroque.bowlingcompanion.core.model.ui.BowlerRow

@Composable
fun GearForm(
	state: GearFormUiState,
	onAction: (GearFormUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	if (state.isShowingDeleteDialog) {
		DeleteDialog(
			itemName = state.name,
			onDelete = { onAction(GearFormUiAction.ConfirmDeleteClicked) },
			onDismiss = { onAction(GearFormUiAction.DismissDeleteClicked) },
		)
	}

	if (state.isShowingDiscardChangesDialog) {
		DiscardChangesDialog(
			onDiscardChanges = { onAction(GearFormUiAction.DiscardChangesClicked) },
			onDismiss = { onAction(GearFormUiAction.CancelDiscardChangesClicked) },
		)
	}

	Column(
		modifier = modifier
			.verticalScroll(rememberScrollState())
			.fillMaxSize(),
	) {
		FormSection(titleResourceId = R.string.gear_form_section_details) {
			NameTextField(
				name = state.name,
				onNameChanged = { onAction(GearFormUiAction.NameChanged(it)) },
				nameErrorId = state.nameErrorId,
				modifier = Modifier.padding(horizontal = 16.dp),
			)

			PickableResourceCard(
				resourceName = stringResource(R.string.gear_form_section_owner),
				selectedName = if (state.owner == null) stringResource(R.string.gear_form_owner_none) else state.owner.name,
				onClick = { onAction(GearFormUiAction.OwnerClicked) },
				modifier = Modifier.padding(horizontal = 16.dp),
			)
		}

		FormSection(
			titleResourceId = R.string.gear_form_section_avatar,
			footerResourceId = R.string.gear_form_section_avatar_description,
		) {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.clickable(onClick = { onAction(GearFormUiAction.AvatarClicked) })
					.padding(16.dp),
			) {
				AvatarImage(
					avatar = state.avatar,
					modifier = Modifier.size(48.dp)
				)
			}
		}

		if (state.isDeleteButtonEnabled) {
			Button(
				onClick = { onAction(GearFormUiAction.DeleteClicked) },
				modifier = Modifier
					.padding(top = 16.dp)
					.fillMaxWidth(),
				colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = RCoreDesign.color.destructive)),
			) {
				Text(text = stringResource(id = R.string.gear_form_delete))
			}
		}
	}
}

@Composable
private fun NameTextField(
	name: String,
	onNameChanged: (String) -> Unit,
	nameErrorId: Int?,
	modifier: Modifier = Modifier,
) {
	OutlinedTextField(
		value = name,
		onValueChange = onNameChanged,
		leadingIcon = {
			Icon(
				painter = painterResource(RCoreDesign.drawable.ic_label),
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		},
		label = {
			Text(
				text = stringResource(R.string.gear_form_name),
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
		modifier = modifier.fillMaxWidth(),
	)
}