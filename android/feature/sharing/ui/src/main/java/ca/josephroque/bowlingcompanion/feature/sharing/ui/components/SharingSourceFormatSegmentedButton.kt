package ca.josephroque.bowlingcompanion.feature.sharing.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingSourceFormat

@Composable
fun SharingSourceFormatSegmentedButton(
	selected: SharingSourceFormat,
	supportedFormats: List<SharingSourceFormat>,
	onFormatChanged: (SharingSourceFormat) -> Unit,
	modifier: Modifier = Modifier,
) {
	SingleChoiceSegmentedButtonRow(
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp)
	) {
		supportedFormats.forEachIndexed { index, format ->
			SegmentedButton(
				selected = selected == format,
				onClick = { onFormatChanged(format) },
				shape = SegmentedButtonDefaults.itemShape(
					index = index,
					count = SharingSourceFormat.entries.size,
				),
				modifier = Modifier.fillMaxWidth(),
			) {
				Text(
					text = format.title(),
					style = MaterialTheme.typography.bodyMedium,
				)
			}
		}
	}
}

@Preview
@Composable
fun SharingSourceFormatSegmentedButtonPreview() {
	Column(modifier = Modifier.width(300.dp)) {
		SharingSourceFormatSegmentedButton(
			selected = SharingSourceFormat.GAMES,
			supportedFormats = SharingSourceFormat.entries.toList(),
			onFormatChanged = {},
		)
	}
}