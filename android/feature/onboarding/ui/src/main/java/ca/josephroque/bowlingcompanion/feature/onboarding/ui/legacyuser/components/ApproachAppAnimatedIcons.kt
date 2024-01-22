package ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.components.OnboardingBackground

@Composable
fun ApproachAppAnimatedIcons(
	isVisible: Boolean,
	modifier: Modifier = Modifier,
) {
	val visibleState = remember { MutableTransitionState(false) }

	LaunchedEffect(isVisible) {
		visibleState.targetState = isVisible
	}

	AnimatedIcons(
		visibleState = visibleState,
		modifier = modifier,
	)
}

@Composable
fun AnimatedIcons(
	visibleState: MutableTransitionState<Boolean>,
	modifier: Modifier = Modifier,
) {
	AnimatedVisibility(
		visibleState = visibleState,
		enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
		modifier = modifier,
	) {
		OnboardingBackground()
	}
}