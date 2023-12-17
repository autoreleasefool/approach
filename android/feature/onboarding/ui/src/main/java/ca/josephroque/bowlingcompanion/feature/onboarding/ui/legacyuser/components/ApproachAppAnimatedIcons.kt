package ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.R
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.LegacyUserOnboardingUiState

@Composable
fun ApproachAppAnimatedIcons(
	state: LegacyUserOnboardingUiState,
	modifier: Modifier = Modifier,
) {
	val visibleState = remember { MutableTransitionState(false) }

	LaunchedEffect(state) {
		when (state) {
			LegacyUserOnboardingUiState.Started, LegacyUserOnboardingUiState.ImportingData -> Unit
			is LegacyUserOnboardingUiState.ShowingApproachHeader -> visibleState.targetState = state.isDetailsVisible
		}
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
	) {
		Box(modifier = modifier.fillMaxSize()) {
			val resources = LocalContext.current.resources
			val image = remember {
				ResourcesCompat.getDrawable(resources, R.drawable.onboarding_pattern, null)?.toBitmap()?.asImageBitmap()
			} ?: return@Box

			val brush = remember(image) {
				ShaderBrush(ImageShader(image, TileMode.Repeated, TileMode.Repeated))
			}

			Box(
				modifier = Modifier
					.fillMaxSize()
					.alpha(0.4f)
					.background(brush)
			)
		}
	}
}