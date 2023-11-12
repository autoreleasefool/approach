package ca.josephroque.bowlingcompanion.feature.settings.ui.acknowledgements.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AcknowledgementDetails(
	state: AcknowledgementDetailsUiState,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState()),
	) {
		Text(
			text = state.acknowledgement.license,
			style = MaterialTheme.typography.bodyMedium,
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
		)
	}
}