package ca.josephroque.bowlingcompanion.feature.sharing.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharingTopBar(
	@Suppress("UNUSED_PARAMETER") state: SharingTopBarUiState,
	onAction: (SharingTopBarUiAction) -> Unit,
) {
	TopAppBar(
		title = {
			Text(
				text = stringResource(R.string.sharing_title),
				style = MaterialTheme.typography.titleLarge,
			)
		},
		navigationIcon = {
			BackButton(onClick = { onAction(SharingTopBarUiAction.BackClicked) })
		},
	)
}
