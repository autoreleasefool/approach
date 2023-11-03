package ca.josephroque.bowlingcompanion.feature.gameseditor.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.gamedetails.GameDetailsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesEditorTopBar(
	gameDetailsState: GameDetailsUiState,
	onBackPressed: () -> Unit,
	openSettings: () -> Unit,
) {
	TopAppBar(
		title = {
			Text(
				stringResource(
					RCoreDesign.string.game_with_ordinal,
					gameDetailsState.currentGameIndex + 1
				)
			)
		},
		colors = TopAppBarDefaults.topAppBarColors(),
		navigationIcon = { BackButton(onClick = onBackPressed) },
		actions = {
			IconButton(onClick = openSettings) {
				Icon(
					painter = painterResource(RCoreDesign.drawable.ic_settings),
					contentDescription = stringResource(R.string.cd_settings),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		}
	)
}