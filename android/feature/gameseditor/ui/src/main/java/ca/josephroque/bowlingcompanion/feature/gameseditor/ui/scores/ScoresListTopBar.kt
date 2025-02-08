package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scores

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoresListTopBar(onAction: (ScoresListUiAction) -> Unit, scrollBehavior: TopAppBarScrollBehavior) {
	TopAppBar(
		title = {
			Text(
				text = stringResource(R.string.scores_list_title),
				style = MaterialTheme.typography.titleMedium,
			)
		},
		navigationIcon = {
			BackButton(
				onClick = { onAction(ScoresListUiAction.BackClicked) },
			)
		},
		scrollBehavior = scrollBehavior,
	)
}
