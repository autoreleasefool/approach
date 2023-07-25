package ca.josephroque.bowlingcompanion.feature.onboarding.legacyuser

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.icons.rememberKeyboardDoubleArrowUp

@Composable
internal fun LegacyUserOnboardingScreen(
	onCompleteOnboarding: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: LegacyUserOnboardingViewModel = hiltViewModel(),
) {
	val legacyUserOnboardingUiState by viewModel.uiState.collectAsStateWithLifecycle()

	LegacyUserOnboarding(
		legacyUserOnboardingUiState = legacyUserOnboardingUiState,
		handleEvent = viewModel::handleEvent,
		modifier = modifier,
	)
}

@Composable
internal fun LegacyUserOnboarding(
	legacyUserOnboardingUiState: LegacyUserOnboardingUiState,
	handleEvent: (LegacyUserOnboardingUiEvent) -> Unit,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier.fillMaxSize(),
	) {
		LegacyAppHeader(
			legacyUserOnboardingUiState = legacyUserOnboardingUiState,
		)
		ApproachAppHeader(
			onNextClicked = { handleEvent(LegacyUserOnboardingUiEvent.ApproachIconClicked) }
		)
	}
}

@Composable
internal fun LegacyAppHeader(
	legacyUserOnboardingUiState: LegacyUserOnboardingUiState,
	modifier: Modifier = Modifier,
) {
	val visibleState = remember {
		MutableTransitionState(false).apply {
			targetState = true
		}
	}

	LaunchedEffect(legacyUserOnboardingUiState) {
		when (legacyUserOnboardingUiState) {
			LegacyUserOnboardingUiState.ShowingApproachDetails ->
				visibleState.targetState = false
			LegacyUserOnboardingUiState.Started,
			LegacyUserOnboardingUiState.Complete,
			LegacyUserOnboardingUiState.ImportingData ->
				Unit
		}
	}

	Column(
		verticalArrangement = Arrangement.Center,
		modifier = modifier.fillMaxSize(),
	) {
		AnimatedVisibility(
			visibleState = visibleState,
			enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
			exit = slideOutVertically(targetOffsetY = { -it / 2 }) + fadeOut(),
			modifier = modifier,
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp),
			) {
				Text(
					text = stringResource(R.string.onboarding_legacy_user_title_your_bowling_companion),
					fontSize = 30.sp,
					fontStyle = FontStyle.Italic,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.weight(1.0f),
				)

				Image(
					painter = painterResource(R.drawable.ic_bowling_companion_rounded),
					contentDescription = null,
					contentScale = ContentScale.Fit,
					modifier = Modifier.size(128.dp),
				)
			}
		}
	}
}

@Composable
internal fun ApproachAppHeader(
	onNextClicked: () -> Unit,
	modifier: Modifier = Modifier,
) {
	BoxWithConstraints(
		modifier = modifier.fillMaxSize(),
	) {
		val scope = this

		Column(
			modifier = Modifier
				.offset(y = scope.maxHeight - 80.dp)
				.clickable { onNextClicked() },
		) {
			Image(
				imageVector = rememberKeyboardDoubleArrowUp(),
				contentDescription = stringResource(R.string.onboarding_legacy_user_content_description_next),
				modifier = Modifier
					.padding(bottom = 8.dp)
					.align(CenterHorizontally)
					.size(40.dp),
			)

			Image(
				painter = painterResource(R.drawable.ic_approach_squircle),
				contentDescription = null,
				contentScale = ContentScale.FillWidth,
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp)
			)
		}
	}
}

@Preview
@Composable
internal fun LegacyUserOnboardingPreview() {
	Surface {
		LegacyUserOnboarding(
			legacyUserOnboardingUiState = LegacyUserOnboardingUiState.Started,
			handleEvent = { },
		)
	}
}