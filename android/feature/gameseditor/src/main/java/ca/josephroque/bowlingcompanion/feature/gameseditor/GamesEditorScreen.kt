package ca.josephroque.bowlingcompanion.feature.gameseditor

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import ca.josephroque.bowlingcompanion.core.designsystem.modifiers.disableGestures
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.GearID
import ca.josephroque.bowlingcompanion.core.model.LaneID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.navigation.GamesSettingsResultViewModel
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultKey
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultViewModel
import ca.josephroque.bowlingcompanion.core.navigation.ScoreEditorResultViewModel
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditor
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditorTopBar
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditorTopBarUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetails
import ca.josephroque.bowlingcompanion.feature.sharing.SharingSheet
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
internal fun GamesEditorRoute(
	onBackPressed: () -> Unit,
	onEditMatchPlay: (GameID) -> Unit,
	onEditGear: (Set<GearID>, ResourcePickerResultKey) -> Unit,
	onEditRolledBall: (GearID?, ResourcePickerResultKey) -> Unit,
	onEditAlley: (AlleyID?, ResourcePickerResultKey) -> Unit,
	onEditLanes: (AlleyID, Set<LaneID>, ResourcePickerResultKey) -> Unit,
	onShowGamesSettings: (
		TeamSeriesID?,
		List<SeriesID>,
		GameID,
	) -> Unit,
	onShowStatistics: (TrackableFilter) -> Unit,
	onShowBowlerScores: (List<SeriesID>, gameIndex: Int) -> Unit,
	onEditScore: (
		score: Int,
		GameScoringMethod,
	) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: GamesEditorViewModel = hiltViewModel(),
	pickerResultViewModel: ResourcePickerResultViewModel = hiltViewModel(),
	gamesSettingsResultViewModel: GamesSettingsResultViewModel = hiltViewModel(),
	scoreResultViewModel: ScoreEditorResultViewModel = hiltViewModel(),
) {
	val gamesEditorScreenState by viewModel.uiState.collectAsStateWithLifecycle()

	val lifecycleOwner = LocalLifecycleOwner.current
	val lifecycle = lifecycleOwner.lifecycle

	DisposableEffect(lifecycle) {
		lifecycle.addObserver(viewModel)
		onDispose {
			lifecycle.removeObserver(viewModel)
		}
	}

	LaunchedEffect(Unit) {
		pickerResultViewModel.getSelectedIds(GAMES_EDITOR_GAME_GEAR_RESULT_KEY) { GearID(it) }
			.onEach { viewModel.handleAction(GamesEditorScreenUiAction.GearUpdated(it)) }
			.launchIn(this)

		pickerResultViewModel.getSelectedIds(GAMES_EDITOR_GAME_ALLEY_RESULT_KEY) { AlleyID(it) }
			.onEach { viewModel.handleAction(GamesEditorScreenUiAction.AlleyUpdated(it.firstOrNull())) }
			.launchIn(this)

		pickerResultViewModel.getSelectedIds(GAMES_EDITOR_GAME_LANES_RESULT_KEY) { LaneID(it) }
			.onEach { viewModel.handleAction(GamesEditorScreenUiAction.LanesUpdated(it)) }
			.launchIn(this)

		pickerResultViewModel.getSelectedIds(GAMES_EDITOR_GAME_ROLLED_BALL_RESULT_KEY) { GearID(it) }
			.onEach {
				viewModel.handleAction(GamesEditorScreenUiAction.SelectedBallUpdated(it.firstOrNull()))
			}
			.launchIn(this)

		gamesSettingsResultViewModel.getResult()
			.onEach { seriesAndGame ->
				viewModel.handleAction(GamesEditorScreenUiAction.SeriesUpdated(seriesAndGame.first))
				viewModel.handleAction(GamesEditorScreenUiAction.CurrentGameUpdated(seriesAndGame.second))
			}
			.launchIn(this)

		scoreResultViewModel.getScore()
			.onEach {
				viewModel.handleAction(GamesEditorScreenUiAction.ScoreUpdated(it.second, it.first))
			}
			.launchIn(this)
	}

	LaunchedEffect(Unit) {
		lifecycleOwner.lifecycleScope.launch {
			viewModel.events
				.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
				.collect {
					when (it) {
						GamesEditorScreenEvent.Dismissed -> onBackPressed()
						is GamesEditorScreenEvent.EditMatchPlay -> onEditMatchPlay(it.gameId)
						is GamesEditorScreenEvent.EditGear -> onEditGear(
							it.gearIds,
							GAMES_EDITOR_GAME_GEAR_RESULT_KEY,
						)
						is GamesEditorScreenEvent.EditAlley -> onEditAlley(
							it.alleyId,
							GAMES_EDITOR_GAME_ALLEY_RESULT_KEY,
						)
						is GamesEditorScreenEvent.EditLanes -> onEditLanes(
							it.alleyId,
							it.laneIds,
							GAMES_EDITOR_GAME_LANES_RESULT_KEY,
						)
						is GamesEditorScreenEvent.ShowGamesSettings -> onShowGamesSettings(
							it.teamSeriesId,
							it.series,
							it.currentGameId,
						)
						is GamesEditorScreenEvent.EditRolledBall -> onEditRolledBall(
							it.ballId,
							GAMES_EDITOR_GAME_ROLLED_BALL_RESULT_KEY,
						)
						is GamesEditorScreenEvent.ShowStatistics -> onShowStatistics(it.filter)
						is GamesEditorScreenEvent.ShowBowlerScores -> onShowBowlerScores(
							it.series,
							it.gameIndex,
						)
						is GamesEditorScreenEvent.EditScore -> onEditScore(
							it.score,
							it.scoringMethod,
						)
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

@SuppressLint("ReturnFromAwaitPointerEventScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GamesEditorScreen(
	state: GamesEditorScreenUiState,
	onAction: (GamesEditorScreenUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val bottomSheetState = rememberStandardBottomSheetState(skipHiddenState = false)
	val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)
	val handleHeight = remember { mutableFloatStateOf(56f) }
	val backgroundCoverOpacity = remember { Animatable(0F) }
	
	DisposableEffect(Unit) {
		onAction(GamesEditorScreenUiAction.DidAppear)
		onDispose { onAction(GamesEditorScreenUiAction.DidDisappear) }
	}

	var hasExpandedSheet by remember { mutableStateOf(true) }
	LaunchedEffect(state) {
		when (state) {
			GamesEditorScreenUiState.Loading -> Unit
			is GamesEditorScreenUiState.Loaded -> {
				if (state.bottomSheet.appearance.isGameDetailsSheetVisible == hasExpandedSheet) return@LaunchedEffect
				if (state.bottomSheet.appearance.isGameDetailsSheetVisible) {
					scaffoldState.bottomSheetState.partialExpand()
				} else {
					scaffoldState.bottomSheetState.hide()
				}
				hasExpandedSheet = state.bottomSheet.appearance.isGameDetailsSheetVisible
			}
		}
	}

	LaunchedEffect(bottomSheetState.currentValue) {
		when (bottomSheetState.currentValue) {
			SheetValue.Expanded ->
				backgroundCoverOpacity.animateTo(0.6F)
			SheetValue.PartiallyExpanded ->
				backgroundCoverOpacity.animateTo(0F)
			SheetValue.Hidden -> {
				backgroundCoverOpacity.animateTo(0F)
				bottomSheetState.partialExpand()
			}
		}
	}

	val snackBarLockedMessage = stringResource(R.string.game_editor_locked)
	val snackBarUnlockAction = stringResource(R.string.game_editor_locked_unlock)
	val isGameLockSnackBarVisible = state is GamesEditorScreenUiState.Loaded &&
		state.alerts.isGameLockSnackBarVisible
	LaunchedEffect(isGameLockSnackBarVisible) {
		if (isGameLockSnackBarVisible) {
			val result = scaffoldState.snackbarHostState.showSnackbar(
				message = snackBarLockedMessage,
				duration = SnackbarDuration.Short,
				actionLabel = snackBarUnlockAction,
			)

			when (result) {
				SnackbarResult.Dismissed -> onAction(GamesEditorScreenUiAction.GameLockSnackBarDismissed)
				SnackbarResult.ActionPerformed -> onAction(GamesEditorScreenUiAction.GameLockSnackBarUnlocked)
			}
		}
	}
	
	val highestPossibleScore = if (state is GamesEditorScreenUiState.Loaded) {
		state.alerts.highestScorePossibleAlert?.score
	} else {
		0
	}

	val highestScorePossibleAlertMessage = stringResource(
		R.string.game_editor_strike_out_score,
		highestPossibleScore ?: 0,
	)
	LaunchedEffect(highestPossibleScore) {
		if (highestPossibleScore != null) {
			val result = scaffoldState.snackbarHostState.showSnackbar(
				message = highestScorePossibleAlertMessage,
				duration = SnackbarDuration.Long,
			)

			when (result) {
				SnackbarResult.Dismissed -> onAction(
					GamesEditorScreenUiAction.HighestPossibleScoreSnackBarDismissed,
				)
				SnackbarResult.ActionPerformed -> onAction(
					GamesEditorScreenUiAction.HighestPossibleScoreSnackBarDismissed,
				)
			}
		}
	}

	BottomSheetScaffold(
		scaffoldState = scaffoldState,
		topBar = {
			GamesEditorTopBar(
				state = when (state) {
					is GamesEditorScreenUiState.Loaded -> state.content.topBar
					else -> GamesEditorTopBarUiState()
				},
				onAction = { onAction(GamesEditorScreenUiAction.TopBar(it)) },
			)
		},
		sheetDragHandle = {
			val dragHandleDescription =
				stringResource(
					ca.josephroque.bowlingcompanion.core.designsystem.R.string
						.bottom_sheet_drag_handle_description,
				)
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
		sheetPeekHeight = (
			(state as? GamesEditorScreenUiState.Loaded)?.bottomSheet?.appearance?.headerPeekHeight?.plus(
				handleHeight.floatValue,
			) ?: 0f
			).dp,
		sheetContent = {
			when (state) {
				GamesEditorScreenUiState.Loading -> Unit
				is GamesEditorScreenUiState.Loaded -> GameDetails(
					state = state.bottomSheet.gameDetails,
					onAction = { onAction(GamesEditorScreenUiAction.GameDetails(it)) },
				)
			}
		},
	) { padding ->
		Surface(
			color = Color.Black,
			modifier = Modifier.fillMaxSize(),
		) {
			Box {
				when (state) {
					GamesEditorScreenUiState.Loading -> Unit
					is GamesEditorScreenUiState.Loaded -> GamesEditor(
						state = state.content.gamesEditor,
						onAction = { onAction(GamesEditorScreenUiAction.GamesEditor(it)) },
						modifier = Modifier.padding(padding),
					)
				}

				Box(
					modifier = Modifier
						.fillMaxSize()
						.background(Color.Black.copy(alpha = backgroundCoverOpacity.value))
						.disableGestures(
							disabled = when (bottomSheetState.currentValue) {
								SheetValue.PartiallyExpanded, SheetValue.Hidden -> false
								SheetValue.Expanded -> true
							},
						),
				)
			}
		}

		val sharingSource = when (state) {
			is GamesEditorScreenUiState.Loaded -> state.sharingSource
			else -> null
		}
		if (sharingSource != null) {
			SharingSheet(
				source = sharingSource,
				onDismiss = { onAction(GamesEditorScreenUiAction.SharingDismissed) },
			)
		}
	}
}
