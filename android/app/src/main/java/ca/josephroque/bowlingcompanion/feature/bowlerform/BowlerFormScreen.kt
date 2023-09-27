package ca.josephroque.bowlingcompanion.feature.bowlerform

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.model.BowlerDetails
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerForm
import java.util.UUID

@Composable
internal fun BowlerFormRoute(
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: BowlerFormViewModel = hiltViewModel(),
) {
	val bowlerFormUiState = viewModel.uiState.collectAsState().value

	when (bowlerFormUiState) {
		BowlerFormUiState.Dismissed -> onDismiss()
		else -> Unit
	}

	BowlerFormScreen(
		bowlerFormUiState = bowlerFormUiState,
		loadBowler = viewModel::loadBowler,
		saveBowler = viewModel::saveBowler,
		deleteBowler = viewModel::deleteBowler,
		updateName = viewModel::updateName,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BowlerFormScreen(
	bowlerFormUiState: BowlerFormUiState,
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
			TopAppBar(
				title = { Title(bowlerFormUiState) },
				actions = { Actions(bowlerFormUiState, saveBowler) },
			)
		}
	) { padding ->
	 when (bowlerFormUiState) {
		 BowlerFormUiState.Loading, BowlerFormUiState.Dismissed -> Unit
		 is BowlerFormUiState.Edit ->
			 BowlerForm(
				 name = bowlerFormUiState.name,
				 onNameChanged = updateName,
				 onDoneClicked = saveBowler,
				 errorId = bowlerFormUiState.fieldErrors.nameErrorId,
				 modifier = modifier
					 .padding(padding)
			 )
		 is BowlerFormUiState.Create ->
			 BowlerForm(
				 name = bowlerFormUiState.name,
				 onNameChanged = updateName,
				 onDoneClicked = saveBowler,
				 errorId = bowlerFormUiState.fieldErrors.nameErrorId,
				 modifier = modifier
					 .padding(padding)
			 )
		}
	}
}

@Composable
internal fun Title(
	uiState: BowlerFormUiState,
) {
	when (uiState) {
		BowlerFormUiState.Loading, BowlerFormUiState.Dismissed -> Text("")
		is BowlerFormUiState.Create -> Text(stringResource(R.string.bowler_form_new))
		is BowlerFormUiState.Edit -> Text(stringResource(R.string.bowler_form_edit, uiState.initialValue.name))
	}
}

@Composable
internal fun Actions(
	uiState: BowlerFormUiState,
	saveBowler: () -> Unit,
) {
	when (uiState) {
		BowlerFormUiState.Loading, BowlerFormUiState.Dismissed, is BowlerFormUiState.Create -> Unit
		is BowlerFormUiState.Edit -> {
			Text(
				stringResource(R.string.action_save),
				modifier = Modifier.clickable(onClick = saveBowler),
			)
		}
	}
}

@Preview
@Composable
fun BowlerFormPreview() {
	BowlerFormScreen(
		bowlerFormUiState = BowlerFormUiState.Edit(
			name = "Joseph",
			initialValue = BowlerDetails(id = UUID.randomUUID(), name = "Joseph"),
			fieldErrors = BowlerFormFieldErrors(nameErrorId = R.string.bowler_form_name_missing)
		),
		saveBowler = {},
		loadBowler = {},
		deleteBowler = {},
		updateName = {},
	)
}