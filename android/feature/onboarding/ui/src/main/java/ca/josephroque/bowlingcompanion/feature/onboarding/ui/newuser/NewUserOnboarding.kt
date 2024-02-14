package ca.josephroque.bowlingcompanion.feature.onboarding.ui.newuser

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.R
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.components.OnboardingBackground
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.components.ReadableContent
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.newuser.components.Logbook

@Composable
fun NewUserOnboarding(
	state: NewUserOnboardingUiState,
	onAction: (NewUserOnboardingUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val visibleState = remember { MutableTransitionState(false) }
	LaunchedEffect(Unit) {
		visibleState.targetState = true
	}

	Box(modifier = modifier.fillMaxSize()) {
		AnimatedVisibility(
			visibleState = visibleState,
			enter = slideInVertically { it / 2 } + fadeIn(),
		) {
			OnboardingBackground()
			AppDescription(onAction = onAction)
		}

		when (state) {
			is NewUserOnboardingUiState.ShowingWelcomeMessage -> Unit
			is NewUserOnboardingUiState.ShowingLogbook -> Logbook(
				name = state.name,
				onAction = onAction,
			)
		}
	}
}

@Composable
private fun AppDescription(onAction: (NewUserOnboardingUiAction) -> Unit) {
	ReadableContent(modifier = Modifier.padding(top = 32.dp)) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.fillMaxWidth()
				.verticalScroll(rememberScrollState())
				.padding(horizontal = 16.dp),
		) {
			Image(
				painter = painterResource(R.drawable.ic_approach_squircle),
				contentDescription = null,
				contentScale = ContentScale.FillWidth,
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.align(Alignment.CenterHorizontally)
					.size(96.dp),
			)

			Text(
				text = stringResource(R.string.onboarding_new_user_title_welcome),
				style = MaterialTheme.typography.titleMedium,
				fontStyle = FontStyle.Italic,
				modifier = Modifier.padding(top = 32.dp),
			)

			Text(
				text = stringResource(R.string.onboarding_new_user_title_approach),
				style = MaterialTheme.typography.headlineMedium,
				modifier = Modifier.padding(bottom = 16.dp),
			)

			Text(
				text = stringResource(R.string.onboarding_new_user_description_arrived),
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.padding(bottom = 8.dp),
			)

			Text(
				text = stringResource(R.string.onboarding_new_user_description_wish),
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.padding(bottom = 16.dp),
			)

			Text(
				text = stringResource(R.string.onboarding_new_user_description_vancouver),
				style = MaterialTheme.typography.bodySmall,
				fontWeight = FontWeight.Bold,
				fontStyle = FontStyle.Italic,
				modifier = Modifier.padding(bottom = 16.dp),
			)

			Button(
				onClick = { onAction(NewUserOnboardingUiAction.GetStartedClicked) },
			) {
				Text(
					text = stringResource(R.string.onboarding_legacy_user_get_started),
					style = MaterialTheme.typography.bodyLarge,
				)
			}
		}
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
