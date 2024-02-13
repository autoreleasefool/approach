package ca.josephroque.bowlingcompanion.feature.alleyform.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.DeleteDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.DiscardChangesDialog
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.FormRadioGroup
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.FormSection
import ca.josephroque.bowlingcompanion.core.designsystem.components.form.PickableResourceCard
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.ListSectionFooter
import ca.josephroque.bowlingcompanion.core.model.AlleyMaterial
import ca.josephroque.bowlingcompanion.core.model.AlleyMechanism
import ca.josephroque.bowlingcompanion.core.model.AlleyPinBase
import ca.josephroque.bowlingcompanion.core.model.AlleyPinFall
import ca.josephroque.bowlingcompanion.core.designsystem.text.quantityStringResource
import ca.josephroque.bowlingcompanion.core.model.ui.title

@Composable
fun AlleyForm(
	state: AlleyFormUiState,
	onAction: (AlleyFormUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	if (state.isShowingDeleteDialog) {
		DeleteDialog(
			itemName = state.name,
			onDelete = { onAction(AlleyFormUiAction.ConfirmDeleteClicked) },
			onDismiss = { onAction(AlleyFormUiAction.DismissDeleteClicked) },
		)
	}

	if (state.isShowingDiscardChangesDialog) {
		DiscardChangesDialog(
			onDiscardChanges = { onAction(AlleyFormUiAction.DiscardChangesClicked) },
			onDismiss = { onAction(AlleyFormUiAction.CancelDiscardChangesClicked) },
		)
	}

	Column(
		modifier = modifier
			.verticalScroll(rememberScrollState())
			.fillMaxSize()
			.imePadding(),
	) {
		FormSection(titleResourceId = R.string.alley_form_details_title) {
			AlleyNameField(
				name = state.name,
				onNameChanged = { onAction(AlleyFormUiAction.NameChanged(it)) },
				errorId = state.nameErrorId,
			)

			PickableResourceCard(
				resourceName = stringResource(R.string.alley_form_manage_lanes),
				selectedName = quantityStringResource(
					R.plurals.alley_form_property_lanes_created,
					quantity = state.lanes.size,
					state.lanes.size,
				),
				onClick = { onAction(AlleyFormUiAction.ManageLanesClicked) },
			)
		}

		Divider()

		FormSection(modifier = Modifier.padding(top = 16.dp)) {
			MaterialPicker(
				material = state.material,
				onMaterialChanged = { onAction(AlleyFormUiAction.MaterialChanged(it)) },
			)
		}

		Divider()

		FormSection(modifier = Modifier.padding(top = 16.dp)) {
			MechanismPicker(
				mechanism = state.mechanism,
				onMechanismChanged = { onAction(AlleyFormUiAction.MechanismChanged(it)) },
			)
		}

		Divider()

		FormSection(modifier = Modifier.padding(top = 16.dp)) {
			PinFallPicker(
				pinFall = state.pinFall,
				onPinFallChanged = { onAction(AlleyFormUiAction.PinFallChanged(it)) },
			)
		}

		Divider()

		FormSection(modifier = Modifier.padding(top = 16.dp)) {
			PinBasePicker(
				pinBase = state.pinBase,
				onPinBaseChanged = { onAction(AlleyFormUiAction.PinBaseChanged(it)) },
			)
		}

		Divider()

		if (state.isDeleteButtonEnabled) {
			Button(
				onClick = { onAction(AlleyFormUiAction.DeleteClicked) },
				colors = ButtonDefaults.buttonColors(containerColor = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive)),
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp, vertical = 8.dp),
			) {
				Text(text = stringResource(R.string.alley_form_delete))
			}
		}

		ListSectionFooter(footer = stringResource(R.string.alley_form_properties_help))
	}
}

@Composable
private fun AlleyNameField(name: String, onNameChanged: ((String) -> Unit)?, errorId: Int?) {
	OutlinedTextField(
		value = name,
		onValueChange = onNameChanged ?: {},
		label = { Text(stringResource(R.string.alley_form_property_name)) },
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
					contentDescription = null
				)
			}
		},
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp)
	)
}

@Composable
private fun MaterialPicker(material: AlleyMaterial?, onMaterialChanged: (AlleyMaterial?) -> Unit) {
	FormRadioGroup(
		title = stringResource(R.string.alley_form_property_material),
		subtitle = stringResource(R.string.alley_form_property_material_footer),
		options = AlleyMaterial.entries.toTypedArray(),
		allowNullableSelection = true,
		selected = material,
		titleForOption = { it?.title() ?: stringResource(RCoreDesign.string.none) },
		onOptionSelected = onMaterialChanged,
	)
}

@Composable
private fun MechanismPicker(mechanism: AlleyMechanism?, onMechanismChanged: (AlleyMechanism?) -> Unit) {
	FormRadioGroup(
		title = stringResource(R.string.alley_form_property_mechanism),
		subtitle = stringResource(R.string.alley_form_property_mechanism_footer),
		options = AlleyMechanism.entries.toTypedArray(),
		allowNullableSelection = true,
		selected = mechanism,
		titleForOption = { it?.title() ?: stringResource(RCoreDesign.string.none) },
		onOptionSelected = onMechanismChanged,
	)
}

@Composable
private fun PinFallPicker(pinFall: AlleyPinFall?, onPinFallChanged: (AlleyPinFall?) -> Unit) {
	FormRadioGroup(
		title = stringResource(R.string.alley_form_property_pin_fall),
		subtitle = stringResource(R.string.alley_form_property_pin_fall_footer),
		options = AlleyPinFall.entries.toTypedArray(),
		allowNullableSelection = true,
		selected = pinFall,
		titleForOption = { it?.title() ?: stringResource(RCoreDesign.string.none) },
		onOptionSelected = onPinFallChanged,
	)
}

@Composable
private fun PinBasePicker(pinBase: AlleyPinBase?, onPinBaseChanged: (AlleyPinBase?) -> Unit) {
	FormRadioGroup(
		title = stringResource(R.string.alley_form_property_pin_base),
		subtitle = stringResource(R.string.alley_form_property_pin_base_footer),
		options = AlleyPinBase.entries.toTypedArray(),
		allowNullableSelection = true,
		selected = pinBase,
		titleForOption = { it?.title() ?: stringResource(RCoreDesign.string.none) },
		onOptionSelected = onPinBaseChanged,
	)
}