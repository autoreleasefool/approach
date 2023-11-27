package ca.josephroque.bowlingcompanion.feature.avatarform

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.AvatarForm
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.AvatarFormTopBar
import kotlinx.coroutines.launch

@Composable
internal fun AvatarFormRoute(
	onDismissWithResult: (Avatar) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AvatarFormViewModel = hiltViewModel(),
) {
	val avatarFormScreenState = viewModel.uiState.collectAsState().value

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						is AvatarFormScreenEvent.Dismissed -> onDismissWithResult(it.result)
					}
				}
		}
	}

	AvatarFormScreen(
		state = avatarFormScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@Composable
private fun AvatarFormScreen(
	state: AvatarFormScreenUiState,
	onAction: (AvatarFormScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LaunchedEffect(Unit) {
		onAction(AvatarFormScreenUiAction.LoadAvatar)
	}

	Scaffold(
		topBar = {
			AvatarFormTopBar(
				onAction = { onAction(AvatarFormScreenUiAction.AvatarFormAction(it)) }
			)
	  },
	) { padding ->
		when (state) {
			AvatarFormScreenUiState.Loading -> Unit
			is AvatarFormScreenUiState.Loaded ->
				AvatarForm(
					state = state.form,
					onAction = { onAction(AvatarFormScreenUiAction.AvatarFormAction(it)) },
					modifier = modifier.padding(padding),
				)
		}
	}
}