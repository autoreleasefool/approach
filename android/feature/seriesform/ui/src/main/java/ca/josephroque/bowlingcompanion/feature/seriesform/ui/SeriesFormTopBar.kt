package ca.josephroque.bowlingcompanion.feature.seriesform.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.common.utils.simpleFormat
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesFormTopBar(
	state: SeriesFormTopBarUiState,
	onAction: (SeriesFormUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		title = { Title(state) },
		navigationIcon = { BackButton(onClick = { onAction(SeriesFormUiAction.BackClicked) }) },
		actions = { Actions(onAction) },
		scrollBehavior = scrollBehavior,
	)
}

@Composable
private fun Title(
	state: SeriesFormTopBarUiState,
) {
	Text(
		text = if (state.existingDate == null) {
			stringResource(R.string.series_form_new_series_title)
		} else {
			stringResource(R.string.series_form_edit_title, state.existingDate.simpleFormat())
		},
		style = MaterialTheme.typography.titleLarge,
	)
}

@Composable
private fun Actions(
	onAction: (SeriesFormUiAction) -> Unit,
) {
	TextButton(onClick = { onAction(SeriesFormUiAction.DoneClicked) }) {
		Text(
			text = stringResource(ca.josephroque.bowlingcompanion.core.designsystem.R.string.action_save),
			style = MaterialTheme.typography.bodyMedium,
		)
	}
}