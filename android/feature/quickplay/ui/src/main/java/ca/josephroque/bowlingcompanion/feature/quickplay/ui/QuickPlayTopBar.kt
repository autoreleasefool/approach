package ca.josephroque.bowlingcompanion.feature.quickplay.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton
import ca.josephroque.bowlingcompanion.core.designsystem.components.CloseButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickPlayTopBar(
	state: QuickPlayTopBarUiState,
	onAction: (QuickPlayTopBarUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		scrollBehavior = scrollBehavior,
		title = {
			Text(
				text = stringResource(state.title),
				style = when (state.formStyle) {
					QuickPlayTopBarUiState.FormStyle.Sheet -> MaterialTheme.typography.titleMedium
					QuickPlayTopBarUiState.FormStyle.Normal -> MaterialTheme.typography.titleLarge
				},
			)
		},
		navigationIcon = {
			when (state.formStyle) {
				QuickPlayTopBarUiState.FormStyle.Sheet ->
					CloseButton(onClick = { onAction(QuickPlayTopBarUiAction.BackClicked) })
				QuickPlayTopBarUiState.FormStyle.Normal ->
					BackButton(onClick = { onAction(QuickPlayTopBarUiAction.BackClicked) })
			}
		},
		actions = {
			if (state.isAddBowlerEnabled) {
				IconButton(onClick = { onAction(QuickPlayTopBarUiAction.AddBowlerClicked) }) {
					Icon(
						painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_add_person),
						contentDescription = stringResource(R.string.cd_add_bowler),
					)
				}
			}
		},
	)
}
