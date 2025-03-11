package ca.josephroque.bowlingcompanion.feature.gameseditor.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesEditorTopBar(
	state: GamesEditorTopBarUiState,
	onAction: (GamesEditorTopBarUiAction) -> Unit,
) {
	TopAppBar(
		title = {
			Text(
				stringResource(
					ca.josephroque.bowlingcompanion.core.designsystem.R.string.game_with_ordinal,
					state.currentGameIndex + 1,
				),
			)
		},
		colors = TopAppBarDefaults.topAppBarColors(),
		navigationIcon = { BackButton(onClick = { onAction(GamesEditorTopBarUiAction.BackClicked) }) },
		actions = {
			if (state.isSharingButtonVisible) {
				IconButton(onClick = { onAction(GamesEditorTopBarUiAction.ShareClicked) }) {
					Icon(
						Icons.Default.Share,
						contentDescription = stringResource(
							ca.josephroque.bowlingcompanion.core.designsystem.R.string.cd_share,
						),
					)
				}
			}

			IconButton(onClick = { onAction(GamesEditorTopBarUiAction.SettingsClicked) }) {
				Icon(
					painter = painterResource(
						ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_settings,
					),
					contentDescription = stringResource(
						ca.josephroque.bowlingcompanion.core.designsystem.R.string.cd_settings,
					),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		},
	)
}
