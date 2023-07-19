package ca.josephroque.bowlingcompanion.feature.bowlerform

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.model.BowlerKind

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BowlerForm(
	name: String,
	errorId: Int?,
	onNameChanged: (String) -> Unit,
	onDoneClicked: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier.fillMaxSize()
	) {
		OutlinedTextField(
			value = name,
			onValueChange = onNameChanged,
			label = { Text(stringResource(R.string.bowler_form_name)) },
			singleLine = true,
			isError = errorId != null,
			supportingText = {
				if (errorId != null) {
					Text(
						text = stringResource(errorId),
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
			keyboardOptions = KeyboardOptions(
				imeAction = ImeAction.Done,
			),
			keyboardActions = KeyboardActions(
				onDone = { onDoneClicked() },
			),
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
		)
	}
}

@Preview
@Composable
fun BowlerFormPreview() {
	BowlerFormScreen(
		bowlerFormUiState = BowlerFormUiState.Create(
			name = "Joseph",
			kind = BowlerKind.PLAYABLE,
			fieldErrors = BowlerFormFieldErrors(nameErrorId = R.string.bowler_form_name_missing)
		),
		handleEvent = {},
	)
}