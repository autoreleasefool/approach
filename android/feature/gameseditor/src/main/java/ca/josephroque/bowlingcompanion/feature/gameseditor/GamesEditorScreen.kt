package ca.josephroque.bowlingcompanion.feature.gameseditor

import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditorTopBar
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor.FrameEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetails
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetailsUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.NextGameEditableElement
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor.RollEditorUiState

@Composable
internal fun GamesEditorRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: GamesEditorViewModel = hiltViewModel(),
) {
	val gamesEditorState by viewModel.gamesEditorState.collectAsStateWithLifecycle()
	val frameEditorState by viewModel.frameEditorState.collectAsStateWithLifecycle()
	val rollEditorState by viewModel.rollEditorState.collectAsStateWithLifecycle()
	val gameDetailsState by viewModel.gameDetailsState.collectAsStateWithLifecycle()

	LaunchedEffect(gamesEditorState.didLoadInitialGame) {
		if (!gamesEditorState.didLoadInitialGame) {
			viewModel.loadGame()
		}
	}

	GamesEditorScreen(
		frameEditorState = frameEditorState,
		rollEditorState = rollEditorState,
		gameDetailsState = gameDetailsState,
		onBackPressed = onBackPressed,
		onOpenSettings = viewModel::openGameSettings,
		onGoToNext = viewModel::goToNext,
		onOpenSeriesStats = viewModel::openSeriesStats,
		onOpenGameStats = viewModel::openGameStats,
		onManageGear = viewModel::openGearPicker,
		onManageMatchPlay = viewModel::openMatchPlayManager,
		onManageScore = viewModel::openScoreSettings,
		onToggleLock = viewModel::toggleGameLocked,
		onToggleExcludeFromStatistics = viewModel::toggleGameExcludedFromStatistics,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GamesEditorScreen(
	frameEditorState: FrameEditorUiState,
	rollEditorState: RollEditorUiState,
	gameDetailsState: GameDetailsUiState,
	onBackPressed: () -> Unit,
	onOpenSettings: () -> Unit,
	onGoToNext: (NextGameEditableElement) -> Unit,
	onOpenSeriesStats: () -> Unit,
	onOpenGameStats: () -> Unit,
	onManageGear: () -> Unit,
	onManageMatchPlay: () -> Unit,
	onManageScore: () -> Unit,
	onToggleLock: (Boolean?) -> Unit,
	onToggleExcludeFromStatistics: (Boolean?) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scaffoldState = rememberBottomSheetScaffoldState()
	BottomSheetScaffold(
		scaffoldState = scaffoldState,
		topBar = {
			GamesEditorTopBar(
				gameDetailsState = gameDetailsState,
				onBackPressed = onBackPressed,
				openSettings = onOpenSettings,
			)
		},
		sheetContent = {
			GameDetails(
				gameDetailsState = gameDetailsState,
				goToNext = onGoToNext,
				onOpenSeriesStats = onOpenSeriesStats,
				onOpenGameStats = onOpenGameStats,
				onManageGear = onManageGear,
				onManageMatchPlay = onManageMatchPlay,
				onManageScore = onManageScore,
				onToggleLock = onToggleLock,
				onToggleExcludeFromStatistics = onToggleExcludeFromStatistics,
			)
		},
	) {

	}
}