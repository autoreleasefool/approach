package ca.josephroque.bowlingcompanion.feature.sharing.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.sharing.ui.games.GamesSharingConfigurationUiState

@Composable
fun LayoutSegmentedButton(
	selected: GamesSharingConfigurationUiState.Layout,
	onLayoutChanged: (GamesSharingConfigurationUiState.Layout) -> Unit,
	modifier: Modifier = Modifier,
) {
	SingleChoiceSegmentedButtonRow(
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp),
	) {
		GamesSharingConfigurationUiState.Layout.entries.forEachIndexed { index, layout ->
			SegmentedButton(
				selected = selected == layout,
				onClick = { onLayoutChanged(layout) },
				shape = SegmentedButtonDefaults.itemShape(
					index = index,
					count = GamesSharingConfigurationUiState.Layout.entries.size,
				),
				modifier = Modifier.fillMaxWidth(),
			) {
				Text(
					text = layout.title(),
					style = MaterialTheme.typography.bodyMedium,
				)
			}
		}
	}
}
