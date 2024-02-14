package ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.details

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcknowledgementDetailsTopBar(
	state: AcknowledgementDetailsTopBarUiState,
	onAction: (AcknowledgementDetailsUiAction) -> Unit,
	scrollBehavior: TopAppBarScrollBehavior,
) {
	TopAppBar(
		scrollBehavior = scrollBehavior,
		title = {
			Text(
				text = state.name,
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = {
			BackButton(onClick = { onAction(AcknowledgementDetailsUiAction.BackClicked) })
		},
	)
}
