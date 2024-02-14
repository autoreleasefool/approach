package ca.josephroque.bowlingcompanion.feature.overview.ui

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewTopBar(onAction: (OverviewUiAction) -> Unit, scrollBehavior: TopAppBarScrollBehavior) {
	CenterAlignedTopAppBar(
		scrollBehavior = scrollBehavior,
		colors = TopAppBarDefaults.topAppBarColors(),
		title = {
			Text(
				text = stringResource(R.string.overview_title),
				style = MaterialTheme.typography.titleLarge,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
		},
		actions = {
			IconButton(onClick = { onAction(OverviewUiAction.AddBowlerClicked) }) {
				Icon(
					painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_add_person),
					contentDescription = stringResource(R.string.bowler_list_add),
					tint = MaterialTheme.colorScheme.onSurface,
				)
			}
		},
	)
}
