package ca.josephroque.bowlingcompanion.core.model.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.AlleyMaterial
import ca.josephroque.bowlingcompanion.core.model.AlleyMechanism
import ca.josephroque.bowlingcompanion.core.model.AlleyPinBase
import ca.josephroque.bowlingcompanion.core.model.AlleyPinFall

@Composable
fun AlleyRow(
	name: String,
	modifier: Modifier = Modifier,
	material: AlleyMaterial? = null,
	mechanism: AlleyMechanism? = null,
	pinBase: AlleyPinBase? = null,
	pinFall: AlleyPinFall? = null,
	onClick: (() -> Unit)? = null,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier = modifier
			.fillMaxWidth()
			.then(if (onClick != null)
				Modifier
					.clickable(onClick = onClick)
					.padding(16.dp)
			else Modifier)
	) {
		// FIXME: Add location name

		Text(
			text = name,
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.weight(1f),
		)

		val alleyProperties = listOfNotNull(
			material?.icon(),
			pinFall?.icon(),
			mechanism?.icon(),
			pinBase?.icon(),
		).chunked(2)

		Column {
			alleyProperties.forEach { row ->
				Row(horizontalArrangement = Arrangement.Start) {
					row.forEach {
						Icon(
							painter = it,
							// FIXME: add content description
							contentDescription = null,
							modifier = Modifier
								.padding(2.dp)
								.size(16.dp)
						)
					}
				}
			}
		}
	}
}

@Preview
@Composable
private fun AlleyRowPreview() {
	Surface {
		AlleyRow(
			name = "Grandview Lanes",
			material = AlleyMaterial.SYNTHETIC,
			pinFall = AlleyPinFall.STRINGS,
			mechanism = AlleyMechanism.INTERCHANGEABLE,
			pinBase = AlleyPinBase.BLACK,
			onClick = {}
		)
	}
}