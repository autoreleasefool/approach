package ca.josephroque.bowlingcompanion.core.designsystem.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

fun Modifier.topBorder(width: Dp, color: Color) = composed(
	factory = {
		val density = LocalDensity.current
		val strokeWidthPx = density.run { width.toPx() }

		Modifier.drawBehind {
			val width = size.width
			val height = strokeWidthPx / 2

			drawLine(
				color = color,
				start = Offset(0f, height),
				end = Offset(width, height),
				strokeWidth = strokeWidthPx,
			)
		}
	}
)

fun Modifier.bottomBorder(width: Dp, color: Color) = composed(
	factory = {
		val density = LocalDensity.current
		val strokeWidthPx = density.run { width.toPx() }

		Modifier.drawBehind {
			val width = size.width
			val height = size.height - strokeWidthPx / 2

			drawLine(
				color = color,
				start = Offset(0f, height),
				end = Offset(width, height),
				strokeWidth = strokeWidthPx,
			)
		}
	}
)

fun Modifier.startBorder(width: Dp, color: Color) = composed(
	factory = {
		val density = LocalDensity.current
		val strokeWidthPx = density.run { width.toPx() }

		Modifier.drawBehind {
			val width = strokeWidthPx / 2
			val height = size.height

			drawLine(
				color = color,
				start = Offset(width, 0f),
				end = Offset(width, height),
				strokeWidth = strokeWidthPx,
			)
		}
	}
)

fun Modifier.endBorder(width: Dp, color: Color) = composed(
	factory = {
		val density = LocalDensity.current
		val strokeWidthPx = density.run { width.toPx() }

		Modifier.drawBehind {
			val width = size.width - strokeWidthPx / 2
			val height = size.height

			drawLine(
				color = color,
				start = Offset(width, 0f),
				end = Offset(width, height),
				strokeWidth = strokeWidthPx,
			)
		}
	}
)
