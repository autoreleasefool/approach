package ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.R

@Composable
fun OpponentMigrationBottomBar(
	state: OpponentMigrationBottomBarUiState,
	onAction: (OpponentMigrationBottomBarUiAction) -> Unit,
) {
	BottomAppBar {
		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 8.dp),
		) {
			when (state) {
				OpponentMigrationBottomBarUiState.StartMigration -> {
					Spacer(modifier = Modifier.weight(1f))

					Button(
						onClick = { onAction(OpponentMigrationBottomBarUiAction.StartMigrationClicked) },
					) {
						Text(text = stringResource(R.string.action_start))
					}
				}

				is OpponentMigrationBottomBarUiState.Migrating -> {
					FilledTonalIconButton(
						enabled = state.isUndoEnabled,
						onClick = { onAction(OpponentMigrationBottomBarUiAction.UndoClicked) },
					) {
						Icon(
							painterResource(R.drawable.ic_undo),
							contentDescription = stringResource(R.string.action_undo_merge),
						)
					}

					Button(
						enabled = state.isMergeEnabled,
						onClick = { onAction(OpponentMigrationBottomBarUiAction.MergeClicked) },
					) {
						Icon(
							painterResource(R.drawable.ic_merge),
							contentDescription = null,
						)

						Text(text = stringResource(R.string.action_merge_bowlers))
					}
				}
			}
		}
	}
}
