package ca.josephroque.bowlingcompanion.feature.opponentslist.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpponentsListTopBar(onAction: (OpponentsListUiAction) -> Unit, scrollBehavior: TopAppBarScrollBehavior) {
	TopAppBar(
		scrollBehavior = scrollBehavior,
		title = {
			Text(
				text = stringResource(R.string.opponent_list_title),
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = {
			BackButton(onClick = { onAction(OpponentsListUiAction.BackClicked) })
		},
		actions = {
			IconButton(onClick = { onAction(OpponentsListUiAction.AddOpponentClicked) }) {
				Icon(
					imageVector = Icons.Filled.Add,
					contentDescription = stringResource(R.string.opponent_list_add),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		},
	)
}
