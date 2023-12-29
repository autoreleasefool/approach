package ca.josephroque.bowlingcompanion.feature.gameseditor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.core.common.navigation.NavResultCallback
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditor
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditorTopBar
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetails
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
internal fun GamesEditorRoute(
	onBackPressed: () -> Unit,
	onEditMatchPlay: (UUID) -> Unit,
	onEditGear: (Set<UUID>, NavResultCallback<Set<UUID>>) -> Unit,
	onEditRolledBall: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onEditAlley: (UUID?, NavResultCallback<Set<UUID>>) -> Unit,
	onEditLanes: (UUID, Set<UUID>, NavResultCallback<Set<UUID>>) -> Unit,
	onShowGamesSettings: (UUID, UUID, NavResultCallback<UUID>) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: GamesEditorViewModel = hiltViewModel(),
) {
	val gamesEditorScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						GamesEditorScreenEvent.Dismissed -> onBackPressed()
						is GamesEditorScreenEvent.EditMatchPlay -> onEditMatchPlay(it.gameId)
						is GamesEditorScreenEvent.EditGear -> onEditGear(it.gearIds) { ids ->
							viewModel.handleAction(GamesEditorScreenUiAction.GearUpdated(ids))
						}
						is GamesEditorScreenEvent.EditAlley -> onEditAlley(it.alleyId) { ids ->
							viewModel.handleAction(GamesEditorScreenUiAction.AlleyUpdated(ids.firstOrNull()))
						}
						is GamesEditorScreenEvent.EditLanes -> onEditLanes(it.alleyId, it.laneIds) { ids ->
							viewModel.handleAction(GamesEditorScreenUiAction.LanesUpdated(ids))
						}
						is GamesEditorScreenEvent.ShowGamesSettings -> onShowGamesSettings(it.seriesId, it.currentGameId) { gameId ->
							viewModel.handleAction(GamesEditorScreenUiAction.CurrentGameUpdated(gameId))
						}
						is GamesEditorScreenEvent.EditRolledBall -> onEditRolledBall(it.ballId) { ids ->
							viewModel.handleAction(GamesEditorScreenUiAction.SelectedBallUpdated(ids.firstOrNull()))
						}
					}
				}
		}
	}

	GamesEditorScreen(
		state = gamesEditorScreenState,
		onAction = viewModel::handleAction,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GamesEditorScreen(
	state: GamesEditorScreenUiState,
	onAction: (GamesEditorScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scaffoldState = rememberBottomSheetScaffoldState()
	val handleHeight = remember { mutableFloatStateOf(56f) }

	LaunchedEffect(Unit) {
		onAction(GamesEditorScreenUiAction.LoadInitialGame)
	}

	val snackBarLockedMessage = stringResource(R.string.game_editor_locked)
	val coroutineScope = rememberCoroutineScope()
	val isGameLockSnackBarVisible = state is GamesEditorScreenUiState.Loaded && state.isGameLockSnackBarVisible
	LaunchedEffect(isGameLockSnackBarVisible) {
		if (isGameLockSnackBarVisible) {
			coroutineScope.launch {
				val result = scaffoldState.snackbarHostState.showSnackbar(
					message = snackBarLockedMessage,
					duration = SnackbarDuration.Short,
				)

				when (result) {
					SnackbarResult.Dismissed -> onAction(GamesEditorScreenUiAction.GameLockSnackBarDismissed)
					SnackbarResult.ActionPerformed -> onAction(GamesEditorScreenUiAction.GameLockSnackBarDismissed)
				}
			}
		}
	}

	BottomSheetScaffold(
		scaffoldState = scaffoldState,
		topBar = {
			GamesEditorTopBar(
				currentGameIndex = when (state) {
					is GamesEditorScreenUiState.Loaded -> state.gameDetails.currentGameIndex
					else -> 0
				},
				onAction = { onAction(GamesEditorScreenUiAction.GamesEditor(it)) },
			)
		},
		sheetSwipeEnabled = false,
		sheetDragHandle = {
			val dragHandleDescription = stringResource(R.string.bottom_sheet_drag_handle_description)
			Surface(
				modifier = modifier
					.padding(vertical = 8.dp)
					.semantics { contentDescription = dragHandleDescription }
					.onGloballyPositioned { handleHeight.floatValue = it.size.height.toFloat() },
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				shape = MaterialTheme.shapes.extraLarge,
			) {
				Box(modifier = Modifier.size(width = 32.dp, height = 4.dp))
			}
		},
		sheetPeekHeight = ((state as? GamesEditorScreenUiState.Loaded)?.headerPeekHeight?.plus(handleHeight.floatValue) ?: 0f).dp,
		sheetContent = {
			when (state) {
				GamesEditorScreenUiState.Loading -> Unit
				is GamesEditorScreenUiState.Loaded -> GameDetails(
					state = state.gameDetails,
					onAction = { onAction(GamesEditorScreenUiAction.GameDetails(it)) },
				)
			}
		},
	) { padding ->
		Surface(
			color = Color.Black,
			modifier = Modifier.fillMaxSize(),
		) {
			when (state) {
				GamesEditorScreenUiState.Loading -> Unit
				is GamesEditorScreenUiState.Loaded -> GamesEditor(
					state = state.gamesEditor,
					onAction = { onAction(GamesEditorScreenUiAction.GamesEditor(it)) },
					modifier = Modifier.padding(padding),
				)
			}
		}
	}
}