package ca.josephroque.bowlingcompanion.feature.bowlerform

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton
import ca.josephroque.bowlingcompanion.core.database.model.BowlerUpdate
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerForm
import java.util.UUID

@Composable
internal fun BowlerFormRoute(
	onBackPressed: () -> Unit,
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: BowlerFormViewModel = hiltViewModel(),
) {
	val bowlerFormState = viewModel.uiState.collectAsState().value

	when (bowlerFormState) {
		BowlerFormUiState.Dismissed -> onDismiss()
		else -> Unit
	}

	BowlerFormScreen(
		bowlerFormState = bowlerFormState,
		loadBowler = viewModel::loadBowler,
		saveBowler = viewModel::saveBowler,
		archiveBowler = viewModel::archiveBowler,
		updateName = viewModel::updateName,
		onBackPressed = onBackPressed,
		modifier = modifier,
	)
}

@Composable
internal fun BowlerFormScreen(
	bowlerFormState: BowlerFormUiState,
	onBackPressed: () -> Unit,
	loadBowler: () -> Unit,
	saveBowler: () -> Unit,
	archiveBowler: () -> Unit,
	updateName: (String) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		loadBowler()
	}

	Scaffold(
		topBar = {
			BowlerFormTopBar(
				bowlerFormState = bowlerFormState,
				onBackPressed = onBackPressed,
				saveBowler = saveBowler
			)
		},
	) { padding ->
	 when (bowlerFormState) {
		 BowlerFormUiState.Loading, BowlerFormUiState.Dismissed -> Unit
		 is BowlerFormUiState.Edit ->
			 BowlerForm(
				 name = bowlerFormState.properties.name,
				 onNameChanged = updateName,
				 onDoneClicked = saveBowler,
				 nameErrorId = bowlerFormState.fieldErrors.nameErrorId,
				 modifier = modifier
					 .padding(padding)
			 )
		 is BowlerFormUiState.Create ->
			 BowlerForm(
				 name = bowlerFormState.properties.name,
				 onNameChanged = updateName,
				 onDoneClicked = saveBowler,
				 nameErrorId = bowlerFormState.fieldErrors.nameErrorId,
				 modifier = modifier
					 .padding(padding)
			 )
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BowlerFormTopBar(
	bowlerFormState: BowlerFormUiState,
	onBackPressed: () -> Unit,
	saveBowler: () -> Unit,
) {
	TopAppBar(
		title = { Title(bowlerFormState) },
		navigationIcon = { BackButton(onClick = onBackPressed) },
		actions = { Actions(bowlerFormState, saveBowler) },
	)
}

@Composable
private fun Title(
	uiState: BowlerFormUiState,
) {
	Text(
		text = when (uiState) {
			BowlerFormUiState.Loading, BowlerFormUiState.Dismissed -> ""
			is BowlerFormUiState.Create -> when (uiState.properties.kind) {
				BowlerKind.PLAYABLE -> stringResource(R.string.bowler_form_new_bowler)
				BowlerKind.OPPONENT -> stringResource(R.string.bowler_form_new_opponent)
			}
			is BowlerFormUiState.Edit -> stringResource(R.string.bowler_form_edit, uiState.initialValue.name)
		},
		style = MaterialTheme.typography.titleLarge,
	)
}

@Composable
private fun Actions(
	uiState: BowlerFormUiState,
	saveBowler: () -> Unit,
) {
	when (uiState) {
		BowlerFormUiState.Loading, BowlerFormUiState.Dismissed -> Unit
		is BowlerFormUiState.Edit, is BowlerFormUiState.Create -> {
			Text(
				text = stringResource(RCoreDesign.string.action_save),
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier
					.clickable(onClick = saveBowler)
					.padding(16.dp),
			)
		}
	}
}

@Preview
@Composable
private fun BowlerFormPreview() {
	BowlerFormScreen(
		bowlerFormState = BowlerFormUiState.Edit(
			properties = BowlerUpdate(id = UUID.randomUUID(), name = "Joseph"),
			initialValue = BowlerUpdate(id = UUID.randomUUID(), name = "Joseph"),
			fieldErrors = BowlerFormFieldErrors(nameErrorId = R.string.bowler_form_name_missing)
		),
		onBackPressed = {},
		saveBowler = {},
		loadBowler = {},
		archiveBowler = {},
		updateName = {},
	)
}