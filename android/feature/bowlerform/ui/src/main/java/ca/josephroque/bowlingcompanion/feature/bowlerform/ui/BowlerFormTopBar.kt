package ca.josephroque.bowlingcompanion.feature.bowlerform.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton
import ca.josephroque.bowlingcompanion.core.model.BowlerKind

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BowlerFormTopBar(
	state: BowlerFormTopBarUiState,
	onAction: (BowlerFormUiAction) -> Unit,
) {
	TopAppBar(
		title = { Title(state) },
		navigationIcon = { BackButton(onClick = { onAction(BowlerFormUiAction.BackClicked) }) },
		actions = { Actions(onAction) },
	)
}

@Composable
private fun Title(
	state: BowlerFormTopBarUiState,
) {
	Text(
		text = if (state.existingName == null) {
			when (state.kind) {
				BowlerKind.PLAYABLE -> stringResource(R.string.bowler_form_new_bowler_title)
				BowlerKind.OPPONENT -> stringResource(R.string.bowler_form_new_opponent_title)
			}
		} else {
			stringResource(R.string.bowler_form_edit_title, state.existingName)
		},
		style = MaterialTheme.typography.titleLarge,
	)
}

@Composable
private fun Actions(
	onAction: (BowlerFormUiAction) -> Unit,
) {
	Text(
		text = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_save),
		style = MaterialTheme.typography.bodyMedium,
		modifier = Modifier
			.clickable(onClick = {
				Log.d("Joseph", "Saved")
				onAction(BowlerFormUiAction.DoneClicked)
			})
			.padding(16.dp),
	)
}