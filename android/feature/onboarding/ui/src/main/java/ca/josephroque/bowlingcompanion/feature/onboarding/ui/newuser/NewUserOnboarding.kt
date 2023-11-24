package ca.josephroque.bowlingcompanion.feature.onboarding.ui.newuser

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.R
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.newuser.components.Logbook

@Composable
fun NewUserOnboarding(
	state: NewUserOnboardingUiState,
	onAction: (NewUserOnboardingUiAction) -> Unit,
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
				.padding(horizontal = 16.dp)
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
			onAction = onAction,
			modifier = Modifier
				.padding(horizontal = 16.dp)
				.padding(bottom = 16.dp),
		)

		when (state) {
			NewUserOnboardingUiState.ShowingWelcomeMessage -> Unit
			is NewUserOnboardingUiState.ShowingLogbook -> Logbook(
				name = state.name,
				onAction = onAction,
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
				style = MaterialTheme.typography.titleMedium,
				fontStyle = FontStyle.Italic,
			)
			Text(
				text = stringResource(R.string.onboarding_new_user_title_to_your_new),
				style = MaterialTheme.typography.titleMedium,
				fontStyle = FontStyle.Italic,
			)
			Text(
				text = stringResource(R.string.onboarding_new_user_title_approach),
				style = MaterialTheme.typography.headlineMedium,
			)
		}

		Spacer(modifier = Modifier.width(16.dp))

		Image(
			painter = painterResource(RCoreDesign.drawable.pin),
			contentDescription = null,
			contentScale = ContentScale.Fit,
			modifier = Modifier.width(36.dp),
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
	onAction: (NewUserOnboardingUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Button(
		onClick = { onAction(NewUserOnboardingUiAction.GetStartedClicked) },
		modifier = modifier.fillMaxWidth(),
	) {
		Text(
			text = stringResource(R.string.onboarding_new_user_get_started),
			style = MaterialTheme.typography.titleLarge,
		)
	}
}

@Preview
@Composable
private fun NewUserOnboardingPreview() {
	Surface {
		NewUserOnboarding(
			state = NewUserOnboardingUiState.ShowingWelcomeMessage,
			onAction = {},
		)
	}
}