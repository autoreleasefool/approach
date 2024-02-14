package ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.R
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.LegacyUserOnboardingUiAction

@Composable
fun NewApproachHeader(
	isHeaderAtTop: Boolean,
	onAction: (LegacyUserOnboardingUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	BoxWithConstraints(
		modifier = modifier.fillMaxSize(),
	) {
		val scope = this

		var headerState by remember { mutableStateOf(HeaderState.Bouncing) }

		val bounceOffset = rememberBounceAnimation(isAnimating = headerState == HeaderState.Bouncing)
		val sharedTransition =
			updateTransition(targetState = headerState, label = "NewApproachHeader.sharedTransition")
		val positionOffset = rememberPositionAnimation(
			sharedTransition,
			maxHeight = scope.maxHeight.value,
		)
		val buttonSize = rememberButtonSizeAnimation(sharedTransition)
		val iconSize = rememberIconSizeAnimation(sharedTransition, maxWidth = scope.maxWidth.value)

		LaunchedEffect(sharedTransition.currentState) {
			if (sharedTransition.currentState == HeaderState.AtTop) {
				onAction(LegacyUserOnboardingUiAction.NewApproachHeaderAnimationFinished)
			}
		}

		LaunchedEffect(isHeaderAtTop) {
			if (isHeaderAtTop) {
				headerState = HeaderState.AtTop
			}
		}

		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.fillMaxWidth()
				.offset(y = scope.maxHeight - 80.dp - bounceOffset.dp + positionOffset.dp)
				.clickable(
					enabled = headerState == HeaderState.Bouncing,
					onClick = { onAction(LegacyUserOnboardingUiAction.NewApproachHeaderClicked) },
				),
		) {
			Image(
				painter = painterResource(
					ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_double_arrow_up,
				),
				contentDescription = stringResource(R.string.onboarding_legacy_user_content_description_next),
				modifier = Modifier
					.padding(bottom = 8.dp)
					.align(Alignment.CenterHorizontally)
					.size(buttonSize.dp),
			)

			Image(
				painter = painterResource(R.drawable.ic_approach_squircle),
				contentDescription = null,
				contentScale = ContentScale.FillWidth,
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.align(Alignment.CenterHorizontally)
					.width(iconSize.dp),
			)
		}
	}
}

@Composable
private fun rememberBounceAnimation(isAnimating: Boolean): Float {
	var lastBouncePosition by remember { mutableFloatStateOf(0f) }
	val bounceAnimation = remember(isAnimating) { Animatable(lastBouncePosition) }

	LaunchedEffect(isAnimating) {
		if (isAnimating) {
			bounceAnimation.animateTo(
				targetValue = lastBouncePosition + 12.dp.value,
				animationSpec = infiniteRepeatable(
					animation = tween(
						durationMillis = 400,
						delayMillis = 800,
					),
					repeatMode = RepeatMode.Reverse,
				),
			) {
				lastBouncePosition = value
			}
		} else {
			bounceAnimation.animateTo(
				targetValue = 0f,
			) {
				lastBouncePosition = value
			}
		}
	}

	return bounceAnimation.value
}

@Composable
private fun rememberPositionAnimation(
	transition: Transition<HeaderState>,
	maxHeight: Float,
): Float {
	val positionOffset by transition.animateFloat(
		label = "position",
		transitionSpec = {
			tween(durationMillis = 1000)
		},
	) { state ->
		when (state) {
			HeaderState.Bouncing -> 0f
			HeaderState.AtTop -> -maxHeight + 96.dp.value
		}
	}
	return positionOffset
}

@Composable
private fun rememberButtonSizeAnimation(transition: Transition<HeaderState>): Float {
	val size by transition.animateFloat(
		label = "buttonSize",
		transitionSpec = {
			tween(durationMillis = 1000)
		},
	) { state ->
		when (state) {
			HeaderState.Bouncing -> 40.dp.value
			HeaderState.AtTop -> 0f
		}
	}
	return size
}

@Composable
private fun rememberIconSizeAnimation(transition: Transition<HeaderState>, maxWidth: Float): Float {
	val size by transition.animateFloat(
		label = "iconSize",
		transitionSpec = {
			tween(durationMillis = 1000)
		},
	) { state ->
		when (state) {
			HeaderState.Bouncing -> maxWidth
			HeaderState.AtTop -> 96.dp.value
		}
	}
	return size
}

private enum class HeaderState {
	Bouncing,
	AtTop,
}
