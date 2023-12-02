package ca.josephroque.bowlingcompanion.feature.alleyform

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton
import ca.josephroque.bowlingcompanion.core.model.AlleyMaterial
import ca.josephroque.bowlingcompanion.core.model.AlleyMechanism
import ca.josephroque.bowlingcompanion.core.model.AlleyPinBase
import ca.josephroque.bowlingcompanion.core.model.AlleyPinFall
import ca.josephroque.bowlingcompanion.feature.alleyform.ui.AlleyForm
import java.util.UUID

@Composable
internal fun AlleyFormRoute(
	onBackPressed: () -> Unit,
	onDismiss: () -> Unit,
	onManageLanes: (UUID) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AlleyFormViewModel = hiltViewModel(),
) {
	val alleyFormState = viewModel.uiState.collectAsState().value

	when (alleyFormState) {
		AlleyFormUiState.Dismissed -> onDismiss()
		is AlleyFormUiState.ManagingLanes -> onManageLanes(alleyFormState.alleyId)
		is AlleyFormUiState.Create, is AlleyFormUiState.Edit, AlleyFormUiState.Loading -> Unit
	}

	AlleyFormScreen(
		alleyFormState = alleyFormState,
		onBackPressed = onBackPressed,
		loadAlley = viewModel::loadAlley,
		saveAlley = viewModel::saveAlley,
		deleteAlley = viewModel::deleteAlley,
		onNameChanged = viewModel::updateName,
		onMaterialChanged = viewModel::updateMaterial,
		onMechanismChanged = viewModel::updateMechanism,
		onPinBaseChanged = viewModel::updatePinBase,
		onPinFallChanged = viewModel::updatePinFall,
		onManageLanes = viewModel::manageLanes,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AlleyFormScreen(
	alleyFormState: AlleyFormUiState,
	onBackPressed: () -> Unit,
	loadAlley: () -> Unit,
	saveAlley: () -> Unit,
	deleteAlley: () -> Unit,
	onNameChanged: (String) -> Unit,
	onMaterialChanged: (AlleyMaterial?) -> Unit,
	onMechanismChanged: (AlleyMechanism?) -> Unit,
	onPinBaseChanged: (AlleyPinBase?) -> Unit,
	onPinFallChanged: (AlleyPinFall?) -> Unit,
	onManageLanes: () -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		loadAlley()
	}

	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			AlleyFormTopBar(
				alleyFormState = alleyFormState,
				onBackPressed = onBackPressed,
				saveAlley = saveAlley,
				scrollBehavior = scrollBehavior,
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (alleyFormState) {
			AlleyFormUiState.Loading, AlleyFormUiState.Dismissed, is AlleyFormUiState.ManagingLanes -> Unit
			is AlleyFormUiState.Create ->
				AlleyForm(
					name = alleyFormState.properties.name,
					nameErrorId = alleyFormState.fieldErrors.nameErrorId,
					onNameChanged = onNameChanged,
					numberOfLanes = alleyFormState.numberOfLanes,
					onManageLanes = onManageLanes,
					material = alleyFormState.properties.material,
					onMaterialChanged = onMaterialChanged,
					pinFall = alleyFormState.properties.pinFall,
					onPinFallChanged = onPinFallChanged,
					mechanism = alleyFormState.properties.mechanism,
					onMechanismChanged = onMechanismChanged,
					pinBase = alleyFormState.properties.pinBase,
					onPinBaseChanged = onPinBaseChanged,
					modifier = Modifier.padding(padding),
				)
			is AlleyFormUiState.Edit ->
				AlleyForm(
					name = alleyFormState.properties.name,
					nameErrorId = alleyFormState.fieldErrors.nameErrorId,
					onNameChanged = onNameChanged,
					numberOfLanes = alleyFormState.numberOfLanes,
					onManageLanes = onManageLanes,
					material = alleyFormState.properties.material,
					onMaterialChanged = onMaterialChanged,
					pinFall = alleyFormState.properties.pinFall,
					onPinFallChanged = onPinFallChanged,
					mechanism = alleyFormState.properties.mechanism,
					onMechanismChanged = onMechanismChanged,
					pinBase = alleyFormState.properties.pinBase,
					onPinBaseChanged = onPinBaseChanged,
					modifier = Modifier.padding(padding),
				)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlleyFormTopBar(
	alleyFormState: AlleyFormUiState,
	onBackPressed: () -> Unit,
	saveAlley: () -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		title = { Title(alleyFormState) },
		navigationIcon = { BackButton(onClick = onBackPressed) },
		actions = { Actions(alleyFormState, saveAlley) },
		scrollBehavior = scrollBehavior,
	)
}

@Composable
private fun Title(alleyFormState: AlleyFormUiState) {
	when (alleyFormState) {
		AlleyFormUiState.Loading, AlleyFormUiState.Dismissed, is AlleyFormUiState.ManagingLanes -> Text("")
		is AlleyFormUiState.Create -> Text(stringResource(R.string.alley_form_title_new))
		is AlleyFormUiState.Edit -> Text(stringResource(R.string.alley_form_title_edit, alleyFormState.initialValue.name))
	}
}

@Composable
private fun Actions(
	alleyFormState: AlleyFormUiState,
	saveAlley: () -> Unit,
) {
	when (alleyFormState) {
		AlleyFormUiState.Loading, AlleyFormUiState.Dismissed, is AlleyFormUiState.ManagingLanes -> Unit
		is AlleyFormUiState.Edit, is AlleyFormUiState.Create ->
			Text(
				stringResource(RCoreDesign.string.action_save),
				modifier = Modifier
					.clickable(onClick = saveAlley)
					.padding(16.dp),
			)
	}
}