package ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BowlerDetailsTopBar(
	state: BowlerDetailsTopBarUiState,
	onAction: (BowlerDetailsUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	MediumTopAppBar(
		colors = TopAppBarDefaults.topAppBarColors(),
		title = {
			Text(
				text = state.bowlerName,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
				style = MaterialTheme.typography.titleLarge
			)
		},
		navigationIcon = {
			BackButton(onClick = { onAction(BowlerDetailsUiAction.BackClicked) })
		},
		actions = {
			IconButton(onClick = { onAction(BowlerDetailsUiAction.AddLeagueClicked) }) {
				Icon(
					imageVector = Icons.Filled.Add,
					contentDescription = stringResource(R.string.bowler_details_league_list_add),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		},
		scrollBehavior = scrollBehavior,
	)
}