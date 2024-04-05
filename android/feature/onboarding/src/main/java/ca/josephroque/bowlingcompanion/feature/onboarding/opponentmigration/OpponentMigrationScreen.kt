package ca.josephroque.bowlingcompanion.feature.onboarding.opponentmigration

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration.OpponentMigration
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration.OpponentMigrationBottomBar
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration.OpponentMigrationBottomBarUiState
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration.OpponentMigrationTopBar
import kotlinx.coroutines.launch

@Composable
internal fun OpponentMigrationRoute(
	onDismiss: () -> Unit,
	onCompleteMigration: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: OpponentMigrationViewModel = hiltViewModel(),
) {
	val opponentMigrationScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						OpponentMigrationScreenEvent.Dismissed -> onDismiss()
						OpponentMigrationScreenEvent.FinishedMigration -> onCompleteMigration()
					}
				}
		}
	}

	OpponentMigrationScreen(
		state = opponentMigrationScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OpponentMigrationScreen(
	state: OpponentMigrationScreenUiState,
	onAction: (OpponentMigrationScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

	Scaffold(
		topBar = {
			OpponentMigrationTopBar(
				isDoneEnabled = (state as? OpponentMigrationScreenUiState.Loaded)
					?.opponentMigration?.isMigrating ?: false,
				onAction = { onAction(OpponentMigrationScreenUiAction.TopBar(it)) },
				scrollBehavior = scrollBehavior,
			)
		},
		bottomBar = {
			OpponentMigrationBottomBar(
				state = (state as? OpponentMigrationScreenUiState.Loaded)?.bottomBar
					?: OpponentMigrationBottomBarUiState.StartMigration,
				onAction = { onAction(OpponentMigrationScreenUiAction.BottomBar(it)) },
			)
		},
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { padding ->
		when (state) {
			OpponentMigrationScreenUiState.Loading -> Unit
			is OpponentMigrationScreenUiState.Loaded -> {
				OpponentMigration(
					state.opponentMigration,
					onAction = { onAction(OpponentMigrationScreenUiAction.OpponentMigration(it)) },
					modifier = Modifier.padding(padding),
				)
			}
		}
	}
}
