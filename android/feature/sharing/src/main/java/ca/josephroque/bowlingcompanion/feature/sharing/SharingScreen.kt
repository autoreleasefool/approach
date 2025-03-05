package ca.josephroque.bowlingcompanion.feature.sharing

import android.content.Intent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.sharing.ui.Sharing
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingSource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharingSheet(
	source: SharingSource,
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: SharingViewModel = hiltViewModel(),
) {
	val sharingState by viewModel.uiState.collectAsStateWithLifecycle()

	val context = LocalContext.current
	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						SharingScreenEvent.Dismissed -> onDismiss()
						is SharingScreenEvent.LaunchShareIntent -> {
							val fileUri = FileProvider.getUriForFile(
								context,
								"ca.josephroque.bowlingcompanion.fileprovider",
								it.file,
							)

							val intent = Intent(Intent.ACTION_SEND).apply {
								type = "image/png"
								putExtra(Intent.EXTRA_STREAM, fileUri)
								addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
							}

							context.startActivity(Intent.createChooser(intent, null))
						}
					}
				}
		}
	}

	val isSystemInDarkTheme = isSystemInDarkTheme()
	LaunchedEffect(source) {
		viewModel.handleAction(SharingScreenUiAction.DidStartSharing(source, isSystemInDarkTheme))
	}

	ModalBottomSheet(
		onDismissRequest = onDismiss,
		modifier = modifier,
	) {
		SharingScreen(
			state = sharingState,
			onAction = viewModel::handleAction,
		)
	}
}

@Composable
private fun SharingScreen(
	state: SharingScreenUiState,
	onAction: (SharingScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	when (state) {
		SharingScreenUiState.Loading -> Unit
		is SharingScreenUiState.Sharing -> Sharing(
			state = state.sharing,
			onAction = { onAction(SharingScreenUiAction.Sharing(it)) },
			modifier = modifier,
		)
	}
}
