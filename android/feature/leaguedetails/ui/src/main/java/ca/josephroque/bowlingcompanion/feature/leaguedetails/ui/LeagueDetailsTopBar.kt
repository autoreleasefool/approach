package ca.josephroque.bowlingcompanion.feature.leaguedetails.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueDetailsTopBar(
	leagueName: String?,
	onAction: (LeagueDetailsUiAction) -> Unit,
) {
	TopAppBar(
		colors = TopAppBarDefaults.topAppBarColors(),
		title = {
			Text(
				text = leagueName ?: "",
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = {
			BackButton(onClick = { onAction(LeagueDetailsUiAction.BackClicked) })
		},
		actions = {
			IconButton(onClick = { onAction(LeagueDetailsUiAction.AddSeriesClicked) }) {
				Icon(
					imageVector = Icons.Filled.Add,
					contentDescription = stringResource(R.string.series_list_add),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		},
	)
}