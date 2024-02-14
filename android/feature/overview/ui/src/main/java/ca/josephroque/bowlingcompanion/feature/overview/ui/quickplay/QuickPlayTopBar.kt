package ca.josephroque.bowlingcompanion.feature.overview.ui.quickplay

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
import ca.josephroque.bowlingcompanion.core.designsystem.components.CloseButton
import ca.josephroque.bowlingcompanion.feature.overview.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickPlayTopBar(
	onAction: (QuickPlayUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		scrollBehavior = scrollBehavior,
		title = {
			Text(
				text = stringResource(R.string.overview_quick_play),
				style = MaterialTheme.typography.titleMedium,
			)
		},
		navigationIcon = { CloseButton(onClick = { onAction(QuickPlayUiAction.BackClicked) }) },
		actions = {
			IconButton(onClick = { onAction(QuickPlayUiAction.AddBowlerClicked) }) {
				Icon(
					painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_add_person),
					contentDescription = stringResource(R.string.cd_add_bowler),
				)
			}
		},
	)
}
