package ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.R
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.LegacyUserOnboardingUiState

@Composable
fun LegacyCompanionHeader(
	state: LegacyUserOnboardingUiState,
	modifier: Modifier = Modifier,
) {
	val visibleState = remember { MutableTransitionState(false) }

	LaunchedEffect(Unit) {
		visibleState.targetState = true
	}

	LaunchedEffect(state) {
		when (state) {
			LegacyUserOnboardingUiState.Started, LegacyUserOnboardingUiState.ImportingData -> Unit
			is LegacyUserOnboardingUiState.ShowingApproachHeader -> visibleState.targetState = false
		}
	}

	Header(
		visibleState = visibleState,
		modifier = modifier,
	)
}

@Composable
private fun Header(
	visibleState: MutableTransitionState<Boolean>,
	modifier: Modifier = Modifier,
) {
	Column(
		verticalArrangement = Arrangement.Center,
		modifier = modifier.fillMaxSize(),
	) {
		AnimatedVisibility(
			visibleState = visibleState,
			enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
			exit = slideOutVertically(targetOffsetY = { -it / 2 }) + fadeOut(),
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp),
			) {
				Text(
					text = stringResource(R.string.onboarding_legacy_user_title_your_bowling_companion),
					style = MaterialTheme.typography.titleLarge,
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