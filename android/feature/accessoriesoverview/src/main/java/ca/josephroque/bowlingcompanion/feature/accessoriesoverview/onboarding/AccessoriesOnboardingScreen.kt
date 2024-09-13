package ca.josephroque.bowlingcompanion.feature.accessoriesoverview.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.accessoriesoverview.ui.onboarding.AccessoriesOnboarding
import kotlinx.coroutines.launch

@Composable
internal fun AccessoriesOnboardingRoute(
	onDismiss: () -> Unit,
	viewModel: AccessoriesOnboardingViewModel = hiltViewModel(),
) {
	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						AccessoriesOnboardingScreenEvent.Dismissed -> onDismiss()
					}
				}
		}
	}

	AccessoriesOnboardingScreen(
		onAction = viewModel::handleAction,
	)
}

@Composable
private fun AccessoriesOnboardingScreen(onAction: (AccessoriesOnboardingScreenUiAction) -> Unit) {
	DisposableEffect(Unit) {
		onDispose {
			onAction(AccessoriesOnboardingScreenUiAction.Dismissed)
		}
	}

	AccessoriesOnboarding(
		onAction = { onAction(AccessoriesOnboardingScreenUiAction.AccessoriesOnboarding(it)) },
	)
}
