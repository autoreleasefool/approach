package ca.josephroque.bowlingcompanion.feature.avatarform

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.AvatarForm
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.AvatarFormTopBar
import ca.josephroque.bowlingcompanion.feature.avatarform.ui.AvatarFormUiState

@Composable
internal fun AvatarFormRoute(
	onBackPressed: () -> Unit,
	onDismissWithResult: (Avatar) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AvatarFormViewModel = hiltViewModel(),
) {
	val avatarFormState = viewModel.uiState.collectAsState().value
	val avatarFormEvent = viewModel.events.collectAsState().value

	LaunchedEffect(avatarFormEvent) {
		when (avatarFormEvent) {
			is AvatarFormEvent.Dismissed -> onDismissWithResult(avatarFormEvent.avatar)
			null -> Unit
		}
	}

	LaunchedEffect(Unit) {
		viewModel.loadAvatar()
	}

	AvatarFormScreen(
		avatarFormState = avatarFormState,
		onBackPressed = onBackPressed,
		onColorChanged = viewModel::onColorChanged,
		onSaveAvatar = viewModel::saveAvatar,
		onPrimaryColorClicked = viewModel::onPrimaryColorClicked,
		onSecondaryColorClicked = viewModel::onSecondaryColorClicked,
		onRandomizeColorsClicked = viewModel::onRandomizeColorsClicked,
		onLabelChanged = viewModel::onLabelChanged,
		modifier = modifier,
	)
}

@Composable
private fun AvatarFormScreen(
	avatarFormState: AvatarFormUiState,
	onBackPressed: () -> Unit,
	onSaveAvatar: () -> Unit,
	onColorChanged: (Color) -> Unit,
	onPrimaryColorClicked: () -> Unit,
	onSecondaryColorClicked: () -> Unit,
	onRandomizeColorsClicked: () -> Unit,
	onLabelChanged: (String) -> Unit,
	modifier: Modifier = Modifier,
) {
	Scaffold(
		topBar = {
			AvatarFormTopBar(
				onBackPressed = onBackPressed,
				saveAvatar = onSaveAvatar,
			)
		},
	) { padding ->
		AvatarForm(
			state = avatarFormState,
			onColorChanged = onColorChanged,
			onPrimaryColorClicked = onPrimaryColorClicked,
			onSecondaryColorClicked = onSecondaryColorClicked,
			onRandomizeColorsClicked = onRandomizeColorsClicked,
			onLabelChanged = onLabelChanged,
			modifier = modifier.padding(padding),
		)
	}
}