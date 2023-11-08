package ca.josephroque.bowlingcompanion.feature.avatarform

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.LoadingState
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.AvatarForm
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.AvatarFormTopBar
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.AvatarFormUiAction
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.AvatarFormUiState

@Composable
internal fun AvatarFormRoute(
	onDismissWithResult: (Avatar) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AvatarFormViewModel = hiltViewModel(),
) {
	val avatarFormScreenState = viewModel.uiState.collectAsState().value

	when (val avatarFormScreenEvent = viewModel.events.collectAsState().value) {
		is AvatarFormScreenEvent.Dismissed -> onDismissWithResult(avatarFormScreenEvent.result)
		else -> Unit
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

	when (state) {
		AvatarFormScreenUiState.Loading -> LoadingState()
		is AvatarFormScreenUiState.Loaded ->
			AvatarFormScreen(
				state = state.form,
				onAction = { onAction(AvatarFormScreenUiAction.AvatarFormAction(it)) },
				modifier = modifier,
			)
	}
}

@Composable
private fun AvatarFormScreen(
	state: AvatarFormUiState,
	onAction: (AvatarFormUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = { AvatarFormTopBar(onAction = onAction) },
	) { padding ->
		AvatarForm(
			state = state,
			onAction = onAction,
			modifier = modifier.padding(padding),
		)
	}
}