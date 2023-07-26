package ca.josephroque.bowlingcompanion.feature.onboarding.newuser.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.feature.onboarding.legacyuser.LegacyUserOnboardingUiState

@Composable
internal fun ApproachAppDescription(
	uiState: LegacyUserOnboardingUiState,
	onGetStartedClicked: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val visibleState = remember { MutableTransitionState(false) }

	LaunchedEffect(uiState) {
		when (uiState) {
			is LegacyUserOnboardingUiState.ShowingApproachHeader -> visibleState.targetState = uiState.isDetailsVisible
			LegacyUserOnboardingUiState.ImportingData,
			LegacyUserOnboardingUiState.Complete,
			LegacyUserOnboardingUiState.Started -> Unit
		}
	}

	Description(
		visibleState = visibleState,
		onGetStartedClicked = onGetStartedClicked,
		modifier = modifier,
	)
}

@Composable
private fun Description(
	visibleState: MutableTransitionState<Boolean>,
	onGetStartedClicked: () -> Unit,
	modifier: Modifier = Modifier,
) {
	AnimatedVisibility(
		visibleState = visibleState,
		enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
	) {
		Column(
			modifier = modifier.fillMaxSize(),
		) {
			Text(
				text = stringResource(R.string.onboarding_legacy_user_title_is_taking_a_new),
				fontSize = 24.sp,
			)

			Text(
				text = stringResource(R.string.onboarding_legacy_user_title_approach),
				fontSize = 30.sp,
				fontStyle = FontStyle.Italic,
				fontWeight = FontWeight.Bold,
			)

			Text(
				text = stringResource(R.string.onboarding_legacy_user_description_updated),
			)

			Text(
				text = stringResource(R.string.onboarding_legacy_user_description_wish),
			)

			Text(
				text = stringResource(R.string.onboarding_legacy_user_description_vancouver),
				fontSize = 12.sp,
			)

			Spacer(modifier = Modifier.weight(1f))

			Button(onClick = onGetStartedClicked) {
				Text(
					text = stringResource(R.string.onboarding_legacy_user_get_started)
				)
			}
		}
	}
}