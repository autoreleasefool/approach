package ca.josephroque.bowlingcompanion.feature.onboarding.newuser

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.R

@Composable
internal fun NewUserOnboardingScreen(
	onCompleteOnboarding: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: NewUserOnboardingViewModel = hiltViewModel(),
) {
	val newUserOnboardingUiState by viewModel.uiState.collectAsStateWithLifecycle()

	when (newUserOnboardingUiState) {
		NewUserOnboardingUiState.Complete -> onCompleteOnboarding()
		NewUserOnboardingUiState.ShowingWelcomeMessage, is NewUserOnboardingUiState.ShowingLogbook -> Unit
	}

	NewUserOnboarding(
		newUserOnboardingUiState = newUserOnboardingUiState,
		modifier = modifier,
		showLogbook = viewModel::showLogbook,
		addBowler = viewModel::addBowler,
		updateBowlerName = viewModel::updateName,
	)
}

@Composable
private fun NewUserOnboarding(
	newUserOnboardingUiState: NewUserOnboardingUiState,
	showLogbook: () -> Unit,
	addBowler: () -> Unit,
	updateBowlerName: (String) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState()),
	) {
		Card(
			modifier = Modifier
				.padding(top = 16.dp)
				.padding(horizontal = 16.dp),
		) {
			Title(modifier = Modifier.padding(16.dp))
		}

		Spacer(modifier = Modifier.height(16.dp))

		Card(
			modifier = Modifier.padding(horizontal = 16.dp),
		) {
			Description(modifier = Modifier.padding(16.dp))
		}

		Spacer(modifier = Modifier.height(32.dp))

		TagLine(modifier = Modifier.padding(bottom = 16.dp))

		Spacer(modifier = Modifier.weight(1.0f))

		Actions(
			onGetStartedClick = showLogbook,
			modifier = Modifier
				.padding(horizontal = 16.dp)
				.padding(bottom = 16.dp),
		)

		when (newUserOnboardingUiState) {
			NewUserOnboardingUiState.Complete, NewUserOnboardingUiState.ShowingWelcomeMessage -> Unit
			is NewUserOnboardingUiState.ShowingLogbook -> Logbook(
				name = newUserOnboardingUiState.name,
				onNameChanged = updateBowlerName,
				onAddBowlerClicked = addBowler,
			)
		}
	}
}

@Composable
private fun Title(
	modifier: Modifier = Modifier,
) {
	Row(
		horizontalArrangement = Arrangement.SpaceBetween,
		modifier = modifier.fillMaxWidth(),
	) {
		Column(
			verticalArrangement = Arrangement.Center,
		) {
			Text(
				text = stringResource(R.string.onboarding_new_user_title_welcome),
				style = MaterialTheme.typography.headlineSmall,
				fontStyle = FontStyle.Italic,
			)
			Text(
				text = stringResource(R.string.onboarding_new_user_title_to_your_new),
				style = MaterialTheme.typography.headlineSmall,
				fontStyle = FontStyle.Italic,
			)
			Text(
				text = stringResource(R.string.onboarding_new_user_title_approach),
				style = MaterialTheme.typography.displayMedium,
			)
		}

		Spacer(modifier = Modifier.width(16.dp))

		Image(
			painter = painterResource(R.drawable.pin),
			contentDescription = null,
			contentScale = ContentScale.Fit,
			modifier = Modifier.width(48.dp),
		)
	}
}

@Composable
private fun Description(
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier,
	) {
		Text(
			text = stringResource(R.string.onboarding_new_user_description_arrived),
			style = MaterialTheme.typography.bodyLarge,
		)
		Spacer(modifier = Modifier.height(16.dp))
		Text(
			text = stringResource(R.string.onboarding_new_user_description_wish),
			style = MaterialTheme.typography.bodyLarge,
		)
	}
}

@Composable
private fun TagLine(
	modifier: Modifier = Modifier,
) {
	Text(
		text = stringResource(R.string.onboarding_new_user_description_vancouver),
		textAlign = TextAlign.Center,
		style = MaterialTheme.typography.bodyMedium,
		modifier = modifier.fillMaxWidth(),
	)
}

@Composable
private fun Actions(
	onGetStartedClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Button(
		onClick = onGetStartedClick,
		modifier = modifier.fillMaxWidth(),
	) {
		Text(
			text = stringResource(R.string.onboarding_new_user_get_started),
			style = MaterialTheme.typography.titleLarge,
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Logbook(
	name: String,
	onNameChanged: (String) -> Unit,
	onAddBowlerClicked: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val sheetState = rememberModalBottomSheetState(
		confirmValueChange = { sheetSize ->
			when (sheetSize) {
				SheetValue.Hidden -> false
				SheetValue.Expanded, SheetValue.PartiallyExpanded -> true
			}
		},
	)
	val focusRequester = remember { FocusRequester() }
	val focusManager = LocalFocusManager.current

	ModalBottomSheet(
		onDismissRequest = {},
		sheetState = sheetState,
		modifier = modifier,
	) {
		Text(
			text = stringResource(R.string.onboarding_new_user_logbook),
			style = MaterialTheme.typography.bodyLarge,
			textAlign = TextAlign.Center,
			modifier = Modifier.fillMaxWidth(),
		)

		BasicTextField(
			value = name,
			onValueChange = onNameChanged,
			textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
			singleLine = true,
			keyboardOptions = KeyboardOptions(
				imeAction = ImeAction.Done,
			),
			keyboardActions = KeyboardActions(
				onDone = {
					focusManager.clearFocus()
					onAddBowlerClicked()
				},
			),
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp)
				.focusRequester(focusRequester)
		)

		Button(
			onClick = {
				focusManager.clearFocus()
				onAddBowlerClicked()
			},
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 16.dp, bottom = 32.dp, start = 16.dp, end = 16.dp),
		) {
			Text(
				text = stringResource(R.string.onboarding_new_user_add_bowler),
				style = MaterialTheme.typography.titleLarge,
			)
		}
	}

	LaunchedEffect(Unit) {
		focusRequester.requestFocus()
	}
}

@Preview
@Composable
private fun NewUserOnboardingPreview() {
	Surface {
		NewUserOnboarding(
			NewUserOnboardingUiState.ShowingWelcomeMessage,
			showLogbook = {},
			updateBowlerName = {},
			addBowler = {},
		)
	}
}