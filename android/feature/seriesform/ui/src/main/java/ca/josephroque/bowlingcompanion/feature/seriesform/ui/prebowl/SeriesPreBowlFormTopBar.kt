package ca.josephroque.bowlingcompanion.feature.seriesform.ui.prebowl

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesPreBowlFormTopBar(
	state: SeriesPreBowlFormTopBarUiState,
	onAction: (SeriesPreBowlFormTopBarUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		title = {
			Text(
				stringResource(R.string.series_pre_bowl_form_title),
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = {
			BackButton(onClick = { onAction(SeriesPreBowlFormTopBarUiAction.BackClicked) })
		},
		actions = {
			TextButton(
				onClick = { onAction(SeriesPreBowlFormTopBarUiAction.DoneClicked) },
				enabled = state.isDoneEnabled,
			) {
				Text(
					text = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_save),
					style = MaterialTheme.typography.bodyMedium,
				)
			}
		},
		scrollBehavior = scrollBehavior,
	)
}
