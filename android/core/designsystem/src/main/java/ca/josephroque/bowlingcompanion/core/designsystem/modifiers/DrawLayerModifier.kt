package ca.josephroque.bowlingcompanion.core.designsystem.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer

fun Modifier.drawToLayer(graphicsLayer: GraphicsLayer) = this.drawWithContent {
	graphicsLayer.record {
		this@drawWithContent.drawContent()
	}

	drawLayer(graphicsLayer)
}
