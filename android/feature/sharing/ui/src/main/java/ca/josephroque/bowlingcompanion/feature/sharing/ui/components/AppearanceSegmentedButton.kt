package ca.josephroque.bowlingcompanion.feature.sharing.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingAppearance

@Composable
fun AppearanceSegmentedButton(selected: SharingAppearance, onAppearanceChanged: (SharingAppearance) -> Unit) {
	SingleChoiceSegmentedButtonRow(
		modifier = Modifier.padding(horizontal = 16.dp),
	) {
		SharingAppearance.entries.forEachIndexed { index, appearance ->
			SegmentedButton(
				selected = selected == appearance,
				onClick = { onAppearanceChanged(appearance) },
				shape = SegmentedButtonDefaults.itemShape(
					index = index,
					count = SharingAppearance.entries.size,
				),
			) {
				Text(
					text = appearance.name,
					style = MaterialTheme.typography.bodyMedium,
				)
			}
		}
	}
}
