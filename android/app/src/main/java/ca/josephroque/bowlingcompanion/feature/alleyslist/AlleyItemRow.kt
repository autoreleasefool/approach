package ca.josephroque.bowlingcompanion.feature.alleyslist

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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.components.icon
import ca.josephroque.bowlingcompanion.core.model.AlleyListItem
import ca.josephroque.bowlingcompanion.core.model.AlleyMaterial
import ca.josephroque.bowlingcompanion.core.model.AlleyMechanism
import ca.josephroque.bowlingcompanion.core.model.AlleyPinBase
import ca.josephroque.bowlingcompanion.core.model.AlleyPinFall
import java.util.UUID

@Composable
internal fun AlleyItemRow(
	alley: AlleyListItem,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier = modifier
			.fillMaxWidth()
			.clickable(onClick = onClick)
			.padding(16.dp)
	) {
		// TODO: Add location name

		Text(
			text = alley.name,
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.weight(1f),
		)

		val alleyProperties = alley.properties().chunked(2)
		Column {
			alleyProperties.forEach { row ->
				Row(horizontalArrangement = Arrangement.Start) {
					row.forEach {
						Icon(
							painter = it,
							// TODO: add content description
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

@Composable
private fun AlleyListItem.properties(): List<Painter> {
	val properties = mutableListOf<Painter>()
	this.material?.let { properties.add(it.icon()) }
	this.pinFall?.let { properties.add(it.icon()) }
	this.mechanism?.let { properties.add(it.icon()) }
	this.pinBase?.let { properties.add(it.icon()) }
	return properties
}

@Preview
@Composable
private fun AlleyItemRowPreview() {
	Surface {
		AlleyItemRow(
			alley = AlleyListItem(
				id = UUID.randomUUID(),
				name = "Grandview Lanes",
				material = AlleyMaterial.SYNTHETIC,
				pinFall = AlleyPinFall.STRINGS,
				mechanism = AlleyMechanism.INTERCHANGEABLE,
				pinBase = AlleyPinBase.BLACK,
			),
			onClick = {}
		)
	}
}