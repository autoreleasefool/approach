package ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpponentMigrationTopBar(
	isDoneEnabled: Boolean,
	onAction: (OpponentMigrationTopBarUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		scrollBehavior = scrollBehavior,
		title = {
			Text(
				text = stringResource(R.string.onboarding_opponents_data_migration_title),
				style = MaterialTheme.typography.titleMedium,
			)
		},
		actions = {
			TextButton(
				onClick = { onAction(OpponentMigrationTopBarUiAction.DoneClicked) },
				enabled = isDoneEnabled,
			) {
				Text(
					text = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_save),
				)
			}
		},
	)
}
