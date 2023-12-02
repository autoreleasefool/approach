package ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.Acknowledgement

@Composable
fun Acknowledgements(
	state: AcknowledgementsUiState,
	onAction: (AcknowledgementsUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	LazyColumn(
		modifier = modifier
			.fillMaxSize(),
	) {
		itemsIndexed(
			state.acknowledgements,
			key = { _, acknowledgement -> acknowledgement.name },
		) { index, acknowledgement ->
			AcknowledgementItem(
				acknowledgement = acknowledgement,
				onClick = { onAction(AcknowledgementsUiAction.AcknowledgementClicked(acknowledgement.name)) },
			)

			if (index < state.acknowledgements.size - 1) {
				Divider(modifier = Modifier.padding(start = 16.dp))
			}
		}
	}
}

@Composable
private fun AcknowledgementItem(
	acknowledgement: Acknowledgement,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier
			.fillMaxWidth()
			.clickable(onClick = onClick)
			.padding(16.dp),
	) {
		Text(
			text = acknowledgement.name,
			style = MaterialTheme.typography.titleMedium,
		)
	}
}