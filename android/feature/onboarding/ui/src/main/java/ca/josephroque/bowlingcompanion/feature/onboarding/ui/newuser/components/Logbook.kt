package ca.josephroque.bowlingcompanion.feature.onboarding.ui.newuser.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.R
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.newuser.NewUserOnboardingUiAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Logbook(
	name: String,
	enabled: Boolean,
	onAction: (NewUserOnboardingUiAction) -> Unit,
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

	// FIXME: Figure out a way to focus text field automatically without crashing
	// IllegalStateException: FocusRequester is not initialized.
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
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = 16.dp),
		)

		val localStyle = LocalTextStyle.current
		val textStyle = localStyle.merge(
			TextStyle(color = LocalContentColor.current, textAlign = TextAlign.Center),
		)
		TextField(
			value = name,
			onValueChange = { onAction(NewUserOnboardingUiAction.NameChanged(it)) },
			textStyle = textStyle,
			singleLine = true,
			enabled = enabled,
			keyboardOptions = KeyboardOptions(
				imeAction = ImeAction.Done,
				capitalization = KeyboardCapitalization.Words,
			),
			keyboardActions = KeyboardActions(
				onDone = {
					focusManager.clearFocus()
					onAction(NewUserOnboardingUiAction.AddBowlerClicked)
				},
			),
			placeholder = {
				Box(modifier = Modifier.fillMaxWidth()) {
					Text(
						text = stringResource(R.string.onboarding_new_user_your_name),
						style = MaterialTheme.typography.bodyLarge,
						textAlign = TextAlign.Center,
						modifier = Modifier.align(Alignment.Center),
					)
				}
			},
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
		)

		Button(
			onClick = {
				focusManager.clearFocus()
				onAction(NewUserOnboardingUiAction.AddBowlerClicked)
			},
			enabled = enabled,
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 16.dp, bottom = 32.dp, start = 16.dp, end = 16.dp),
		) {
			Text(text = stringResource(R.string.onboarding_new_user_add_bowler))
		}
	}
}

@Preview
@Composable
private fun LogbookPreview() {
	Logbook(
		name = "Joseph Roque",
		onAction = {},
		enabled = true,
	)
}
