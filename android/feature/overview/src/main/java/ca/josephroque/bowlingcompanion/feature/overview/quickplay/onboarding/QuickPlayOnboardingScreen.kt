package ca.josephroque.bowlingcompanion.feature.overview.quickplay.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.overview.ui.quickplay.onboarding.QuickPlayOnboarding
import ca.josephroque.bowlingcompanion.feature.overview.ui.quickplay.onboarding.QuickPlayOnboardingTopBar
import ca.josephroque.bowlingcompanion.feature.overview.ui.quickplay.onboarding.QuickPlayOnboardingUiAction
import kotlinx.coroutines.launch

@Composable
internal fun QuickPlayOnboardingRoute(
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: QuickPlayOnboardingViewModel = hiltViewModel(),
) {
	val quickPlayScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						QuickPlayOnboardingScreenEvent.Dismissed -> onDismiss()
					}
				}
		}
	}

	QuickPlayOnboardingScreen(
		state = quickPlayScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@Composable
private fun QuickPlayOnboardingScreen(
	state: QuickPlayOnboardingScreenUiState,
	onAction: (QuickPlayOnboardingScreenUiAction) -> Unit,
	modifier: Modifier,
) {
	BackHandler {
		onAction(
			QuickPlayOnboardingScreenUiAction.QuickPlayOnboarding(QuickPlayOnboardingUiAction.BackClicked),
		)
	}

	Scaffold(
		topBar = {
			QuickPlayOnboardingTopBar(
				onAction = { onAction(QuickPlayOnboardingScreenUiAction.QuickPlayOnboarding(it)) },
			)
		},
	) { padding ->
		when (state) {
			QuickPlayOnboardingScreenUiState.Loading -> Unit
			QuickPlayOnboardingScreenUiState.Loaded -> QuickPlayOnboarding(
				onAction = { onAction(QuickPlayOnboardingScreenUiAction.QuickPlayOnboarding(it)) },
				modifier = modifier.padding(padding),
			)
		}
	}
}
