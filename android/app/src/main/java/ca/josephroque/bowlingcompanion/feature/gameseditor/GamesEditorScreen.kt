package ca.josephroque.bowlingcompanion.feature.gameseditor

import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton
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
			)
		},
	) {

	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GamesEditorTopBar(
	gameDetailsState: GameDetailsUiState,
	onBackPressed: () -> Unit,
	openSettings: () -> Unit,
) {
	TopAppBar(
		title = {
			when (gameDetailsState) {
				GameDetailsUiState.Loading -> Unit
				is GameDetailsUiState.Edit -> Text(
					stringResource(
						R.string.game_with_ordinal,
						gameDetailsState.currentGameIndex + 1
					)
				)
			}
		},
		colors = TopAppBarDefaults.topAppBarColors(),
		navigationIcon = { BackButton(onClick = onBackPressed) },
		actions = {
			IconButton(onClick = openSettings) {
				Icon(
					painter = painterResource(R.drawable.ic_settings),
					contentDescription = stringResource(R.string.cd_settings),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		}
	)
}