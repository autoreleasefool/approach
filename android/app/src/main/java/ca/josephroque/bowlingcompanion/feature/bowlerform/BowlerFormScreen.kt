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
		handleEvent = viewModel::handleEvent,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BowlerFormScreen(
	bowlerFormUiState: BowlerFormUiState,
	handleEvent: (BowlerFormUiEvent) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		handleEvent(BowlerFormUiEvent.OnAppear)
	}

	Scaffold(
		floatingActionButtonPosition = FabPosition.End,
		floatingActionButton = { FloatingActionButton(bowlerFormUiState, handleEvent) },
		topBar = {
			MediumTopAppBar(
				title = { Title(bowlerFormUiState) },
				actions = { Actions(bowlerFormUiState, handleEvent) },
			)
		}
	) { padding ->
	 when (bowlerFormUiState) {
		 BowlerFormUiState.Loading, BowlerFormUiState.Dismissed -> Unit
		 is BowlerFormUiState.Edit ->
			 BowlerForm(
				 name = bowlerFormUiState.name,
				 onNameChanged = { handleEvent(BowlerFormUiEvent.NameChanged(it)) },
				 onDoneClicked = { handleEvent(BowlerFormUiEvent.SaveButtonClick) },
				 errorId = bowlerFormUiState.fieldErrors.nameErrorId,
				 modifier = modifier
					 .padding(padding)
			 )
		 is BowlerFormUiState.Create ->
			 BowlerForm(
				 name = bowlerFormUiState.name,
				 onNameChanged = { handleEvent(BowlerFormUiEvent.NameChanged(it)) },
				 onDoneClicked = { handleEvent(BowlerFormUiEvent.SaveButtonClick) },
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
	handleEvent: (BowlerFormUiEvent) -> Unit,
) {
	when (uiState) {
		BowlerFormUiState.Loading, BowlerFormUiState.Dismissed, is BowlerFormUiState.Create -> Unit
		is BowlerFormUiState.Edit -> {
			IconButton(onClick = { handleEvent(BowlerFormUiEvent.DeleteButtonClick) }) {
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
	handleEvent: (BowlerFormUiEvent) -> Unit,
) {
	when (uiState) {
		BowlerFormUiState.Loading, BowlerFormUiState.Dismissed -> Unit
		is BowlerFormUiState.Create, is BowlerFormUiState.Edit -> {
			ExtendedFloatingActionButton(
				text = { Text(stringResource(R.string.action_save)) },
				icon = { Icon(Icons.Filled.Add, contentDescription = null) },
				onClick = { handleEvent(BowlerFormUiEvent.SaveButtonClick) },
			)
		}
	}
}