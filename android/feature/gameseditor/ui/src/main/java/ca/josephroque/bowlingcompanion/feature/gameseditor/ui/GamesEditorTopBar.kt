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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesEditorTopBar(currentGameIndex: Int, onAction: (GamesEditorUiAction) -> Unit) {
	TopAppBar(
		title = {
			Text(
				stringResource(
					ca.josephroque.bowlingcompanion.core.designsystem.R.string.game_with_ordinal,
					currentGameIndex + 1,
				),
			)
		},
		colors = TopAppBarDefaults.topAppBarColors(),
		navigationIcon = { BackButton(onClick = { onAction(GamesEditorUiAction.BackClicked) }) },
		actions = {
			IconButton(onClick = { onAction(GamesEditorUiAction.SettingsClicked) }) {
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
