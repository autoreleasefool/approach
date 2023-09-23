package ca.josephroque.bowlingcompanion.feature.bowlerform

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.feature.bowlerform.ui.BowlerForm

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
		floatingActionButtonPosition = FabPosition.End,
		floatingActionButton = { FloatingActionButton(bowlerFormUiState, saveBowler) },
		topBar = {
			MediumTopAppBar(
				title = { Title(bowlerFormUiState) },
				actions = { Actions(bowlerFormUiState, deleteBowler) },
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
	deleteBowler: () -> Unit,
) {
	when (uiState) {
		BowlerFormUiState.Loading, BowlerFormUiState.Dismissed, is BowlerFormUiState.Create -> Unit
		is BowlerFormUiState.Edit -> {
			IconButton(onClick = deleteBowler) {
				Icon(
					Icons.Outlined.Delete,
					tint = MaterialTheme.colorScheme.error,
					contentDescription = stringResource(R.string.action_delete),
				)
			}
		}
	}
}

@Composable
internal fun FloatingActionButton(
	uiState: BowlerFormUiState,
	saveBowler: () -> Unit,
) {
	when (uiState) {
		BowlerFormUiState.Loading, BowlerFormUiState.Dismissed -> Unit
		is BowlerFormUiState.Create, is BowlerFormUiState.Edit -> {
			ExtendedFloatingActionButton(
				text = { Text(stringResource(R.string.action_save)) },
				icon = { Icon(Icons.Filled.Add, contentDescription = null) },
				onClick = saveBowler,
			)
		}
	}
}