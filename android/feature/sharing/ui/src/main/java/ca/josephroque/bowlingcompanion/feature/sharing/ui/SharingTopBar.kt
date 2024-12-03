package ca.josephroque.bowlingcompanion.feature.sharing.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.BackButton

@Composable
fun SharingTopBar(
	@Suppress("UNUSED_PARAMETER") state: SharingTopBarUiState,
	onAction: (SharingTopBarUiAction) -> Unit,
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		BackButton(onClick = { onAction(SharingTopBarUiAction.BackClicked) })

		Text(
			text = stringResource(R.string.sharing_title),
			style = MaterialTheme.typography.titleLarge,
		)
	}
}
