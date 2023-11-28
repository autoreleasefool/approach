package ca.josephroque.bowlingcompanion.feature.seriesdetails.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import ca.josephroque.bowlingcompanion.core.common.utils.format
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesDetailsTopBar(
	state: SeriesDetailsTopBarUiState,
	onAction: (SeriesDetailsUiAction) -> Unit,
) {
	TopAppBar(
		colors = TopAppBarDefaults.topAppBarColors(),
		title = {
			Text(
				text = state.seriesDate?.format("MMMM d, yyyy") ?: "",
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = {
			BackButton(onClick = { onAction(SeriesDetailsUiAction.BackClicked) })
		},
	)
}