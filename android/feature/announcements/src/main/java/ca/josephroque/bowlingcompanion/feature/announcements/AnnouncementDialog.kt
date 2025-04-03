package ca.josephroque.bowlingcompanion.feature.announcements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.announcements.ui.Announcement
import ca.josephroque.bowlingcompanion.feature.announcements.ui.AnnouncementUiAction
import kotlinx.coroutines.launch

@Composable
fun AnnouncementDialog(
	onNavigateToAchievements: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: AnnouncementViewModel = hiltViewModel()
) {
	val announcementDialogState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						AnnouncementDialogEvent.NavigateToAchievements -> onNavigateToAchievements()
					}
				}
		}
	}

	AnnouncementDialog(
		state = announcementDialogState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@Composable
private fun AnnouncementDialog(
	state: AnnouncementDialogUiState,
	onAction: (AnnouncementDialogUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	when (state) {
		is AnnouncementDialogUiState.Announcement -> Dialog(
			onDismissRequest = {
				onAction(AnnouncementDialogUiAction.Announcement(AnnouncementUiAction.Dismissed))
			},
		) {
			Announcement(
				state = state.announcement,
				onAction = { onAction(AnnouncementDialogUiAction.Announcement(it)) },
				modifier = modifier,
			)
		}
		AnnouncementDialogUiState.NoAnnouncement -> Unit
	}
}