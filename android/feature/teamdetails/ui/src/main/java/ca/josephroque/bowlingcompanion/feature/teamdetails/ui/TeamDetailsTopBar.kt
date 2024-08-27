package ca.josephroque.bowlingcompanion.feature.teamdetails.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailsTopBar(
	state: TeamDetailsTopBarUiState,
	onAction: (TeamDetailsTopBarUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	MediumTopAppBar(
		scrollBehavior = scrollBehavior,
		title = {
			Text(
				text = state.teamName ?: "",
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = {
			BackButton(onClick = { onAction(TeamDetailsTopBarUiAction.BackClicked) })
		},
		actions = {
			IconButton(onClick = { onAction(TeamDetailsTopBarUiAction.AddSeriesClicked) }) {
				Icon(
					imageVector = Icons.Filled.Add,
					contentDescription = stringResource(R.string.team_series_list_add),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		},
	)
}
