package ca.josephroque.bowlingcompanion.feature.gameseditor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.core.model.Pin
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditor
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.GamesEditorTopBar
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor.FrameEditorUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetails
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetailsUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.NextGameEditableElement
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.rolleditor.RollEditorUiState
import java.util.UUID

@Composable
internal fun GamesEditorRoute(
	onBackPressed: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: GamesEditorViewModel = hiltViewModel(),
) {
	val gamesEditorState by viewModel.gamesEditorState.collectAsStateWithLifecycle()
	val frameEditorState by viewModel.frameEditorState.collectAsStateWithLifecycle()
	val rollEditorState by viewModel.rollEditorState.collectAsStateWithLifecycle()
	val scoreSheetState by viewModel.scoreSheetState.collectAsStateWithLifecycle()
	val gameDetailsState by viewModel.gameDetailsState.collectAsStateWithLifecycle()

	LaunchedEffect(gamesEditorState.didLoadInitialGame) {
		if (!gamesEditorState.didLoadInitialGame) {
			viewModel.loadGame()
		}
	}

	GamesEditorScreen(
		frameEditorState = frameEditorState,
		rollEditorState = rollEditorState,
		scoreSheetState = scoreSheetState,
		gameDetailsState = gameDetailsState,
		onBackPressed = onBackPressed,
		onOpenSettings = viewModel::openGameSettings,
		onGoToNext = viewModel::goToNext,
		onOpenSeriesStats = viewModel::openSeriesStats,
		onOpenGameStats = viewModel::openGameStats,
		onManageGear = viewModel::openGearPicker,
		onManageMatchPlay = viewModel::openMatchPlayManager,
		onManageScore = viewModel::openScoreSettings,
		onDownedPinsChanged = viewModel::updateDownedPins,
		onSelectBall = viewModel::updateSelectedBall,
		onToggleFoul = viewModel::toggleFoul,
		onToggleLock = viewModel::toggleGameLocked,
		onToggleExcludeFromStatistics = viewModel::toggleGameExcludedFromStatistics,
		onFrameSelectionChanged = viewModel::updateFrameSelection,
		modifier = modifier,
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GamesEditorScreen(
	frameEditorState: FrameEditorUiState,
	rollEditorState: RollEditorUiState,
	scoreSheetState: ScoreSheetUiState,
	gameDetailsState: GameDetailsUiState,
	onBackPressed: () -> Unit,
	onOpenSettings: () -> Unit,
	onGoToNext: (NextGameEditableElement) -> Unit,
	onOpenSeriesStats: () -> Unit,
	onOpenGameStats: () -> Unit,
	onManageGear: () -> Unit,
	onManageMatchPlay: () -> Unit,
	onManageScore: () -> Unit,
	onDownedPinsChanged: (Set<Pin>) -> Unit,
	onSelectBall: (UUID) -> Unit,
	onToggleFoul: (Boolean) -> Unit,
	onToggleLock: (Boolean?) -> Unit,
	onToggleExcludeFromStatistics: (Boolean?) -> Unit,
	onFrameSelectionChanged: (ScoreSheetUiState.Selection) -> Unit,
	modifier: Modifier = Modifier,
) {
	val scaffoldState = rememberBottomSheetScaffoldState()
	val headerHeight = remember { mutableFloatStateOf(0f) }
	val handleHeight = remember { mutableFloatStateOf(56f) }

	BottomSheetScaffold(
		scaffoldState = scaffoldState,
		topBar = {
			GamesEditorTopBar(
				gameDetailsState = gameDetailsState,
				onBackPressed = onBackPressed,
				openSettings = onOpenSettings,
			)
		},
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
		sheetPeekHeight = (headerHeight.floatValue + handleHeight.floatValue).dp,
		sheetContent = {
			GameDetails(
				state = gameDetailsState,
				goToNext = onGoToNext,
				onOpenSeriesStats = onOpenSeriesStats,
				onOpenGameStats = onOpenGameStats,
				onManageGear = onManageGear,
				onManageMatchPlay = onManageMatchPlay,
				onManageScore = onManageScore,
				onToggleLock = onToggleLock,
				onToggleExcludeFromStatistics = onToggleExcludeFromStatistics,
				onMeasureHeaderHeight = { headerHeight.floatValue = it }
			)
		},
	) {
		GamesEditor(
			frameEditorState = frameEditorState,
			rollEditorState = rollEditorState,
			scoreSheetState = scoreSheetState,
			onDownedPinsChanged = onDownedPinsChanged,
			onSelectBall = onSelectBall,
			onToggleFoul = onToggleFoul,
			onFrameSelectionChanged = onFrameSelectionChanged,
		)
	}
}