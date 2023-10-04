package ca.josephroque.bowlingcompanion.feature.bowlerform

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import ca.josephroque.bowlingcompanion.core.model.BowlerDetails
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
		deleteBowler = viewModel::deleteBowler,
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
	deleteBowler: () -> Unit,
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
				 name = bowlerFormState.name,
				 onNameChanged = updateName,
				 onDoneClicked = saveBowler,
				 nameErrorId = bowlerFormState.fieldErrors.nameErrorId,
				 modifier = modifier
					 .padding(padding)
			 )
		 is BowlerFormUiState.Create ->
			 BowlerForm(
				 name = bowlerFormState.name,
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
internal fun BowlerFormTopBar(
	bowlerFormState: BowlerFormUiState,
	onBackPressed: () -> Unit,
	saveBowler: () -> Unit,
) {
	TopAppBar(
		title = { Title(bowlerFormState) },
		navigationIcon = {
			IconButton(onClick = onBackPressed) {
				Icon(
					Icons.Default.ArrowBack,
					contentDescription = stringResource(R.string.cd_back),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		},
		actions = {
			Actions(
				bowlerFormState,
				saveBowler,
			)
		},
	)
}

@Composable
internal fun Title(
	uiState: BowlerFormUiState,
) {
	Text(
		text = when (uiState) {
			BowlerFormUiState.Loading, BowlerFormUiState.Dismissed -> ""
			is BowlerFormUiState.Create -> stringResource(R.string.bowler_form_new)
			is BowlerFormUiState.Edit -> stringResource(R.string.bowler_form_edit, uiState.initialValue.name)
		},
		style = MaterialTheme.typography.titleLarge,
	)
}

@Composable
internal fun Actions(
	uiState: BowlerFormUiState,
	saveBowler: () -> Unit,
) {
	when (uiState) {
		BowlerFormUiState.Loading, BowlerFormUiState.Dismissed -> Unit
		is BowlerFormUiState.Edit, is BowlerFormUiState.Create -> {
			Text(
				text = stringResource(R.string.action_save),
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
fun BowlerFormPreview() {
	BowlerFormScreen(
		bowlerFormState = BowlerFormUiState.Edit(
			name = "Joseph",
			initialValue = BowlerDetails(id = UUID.randomUUID(), name = "Joseph"),
			fieldErrors = BowlerFormFieldErrors(nameErrorId = R.string.bowler_form_name_missing)
		),
		onBackPressed = {},
		saveBowler = {},
		loadBowler = {},
		deleteBowler = {},
		updateName = {},
	)
}