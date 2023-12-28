package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.header
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import ca.josephroque.bowlingcompanion.core.model.ui.GameRow
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R
import java.util.UUID

@Composable
fun GamesSettings(
	state: GamesSettingsUiState,
	onAction: (GamesSettingsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LazyColumn(modifier = modifier) {
		header(R.string.game_settings_current_game)

		items(state.games) {
			val isSelected = state.currentGameId == it.id

			Surface(
				color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
				modifier = Modifier
					.selectable(
						selected = isSelected,
						onClick = { onAction(GamesSettingsUiAction.GameClicked(it.id)) },
						role = Role.RadioButton,
					)
			) {
				Row(
					horizontalArrangement = Arrangement.spacedBy(16.dp),
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp, vertical = 8.dp),
				) {
					GameRow(
						index = it.index,
						score = it.score,
						modifier = Modifier.weight(1f),
					)

					Box(modifier = Modifier.padding(horizontal = 8.dp)) {
						RadioButton(selected = isSelected, onClick = null)
					}
				}
			}
		}
	}
}

@Preview
@Composable
private fun GamesSettingsPreview() {
	Surface {
		GamesSettings(
			state = GamesSettingsUiState(
				currentGameId = UUID.randomUUID(),
				games = listOf(
					GameListItem(
						id = UUID.randomUUID(),
						index = 0,
						score = 250,
					),
					GameListItem(
						id = UUID.randomUUID(),
						index = 1,
						score = 300,
					)
				)
			),
			onAction = {},
		)
	}
}