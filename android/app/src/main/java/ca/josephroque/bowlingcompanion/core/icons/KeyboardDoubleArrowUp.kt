package ca.josephroque.bowlingcompanion.core.icons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Composable
fun rememberKeyboardDoubleArrowUp(): ImageVector {
	return remember {
		ImageVector.Builder(
			name = "keyboard_double_arrow_up",
			defaultWidth = 40.0.dp,
			defaultHeight = 40.0.dp,
			viewportWidth = 40.0f,
			viewportHeight = 40.0f
		).apply {
			path(
				fill = SolidColor(Color.Black),
				fillAlpha = 1f,
				stroke = null,
				strokeAlpha = 1f,
				strokeLineWidth = 1.0f,
				strokeLineCap = StrokeCap.Butt,
				strokeLineJoin = StrokeJoin.Miter,
				strokeLineMiter = 1f,
				pathFillType = PathFillType.NonZero
			) {
				moveTo(11.083f, 19.417f)
				quadToRelative(-0.375f, -0.375f, -0.375f, -0.917f)
				reflectiveQuadToRelative(0.375f, -0.917f)
				lineToRelative(8f, -8f)
				quadToRelative(0.209f, -0.208f, 0.438f, -0.312f)
				quadToRelative(0.229f, -0.104f, 0.479f, -0.104f)
				quadToRelative(0.25f, 0f, 0.479f, 0.104f)
				quadToRelative(0.229f, 0.104f, 0.438f, 0.312f)
				lineToRelative(8f, 8f)
				quadToRelative(0.375f, 0.375f, 0.375f, 0.917f)
				reflectiveQuadToRelative(-0.375f, 0.917f)
				quadToRelative(-0.417f, 0.416f, -0.938f, 0.416f)
				quadToRelative(-0.521f, 0f, -0.937f, -0.416f)
				lineTo(20f, 12.375f)
				lineToRelative(-7.042f, 7.042f)
				quadToRelative(-0.416f, 0.416f, -0.937f, 0.416f)
				quadToRelative(-0.521f, 0f, -0.938f, -0.416f)
				close()
				moveToRelative(0f, 9.958f)
				quadToRelative(-0.375f, -0.375f, -0.375f, -0.917f)
				quadToRelative(0f, -0.541f, 0.375f, -0.958f)
				lineToRelative(8f, -7.958f)
				quadToRelative(0.209f, -0.209f, 0.438f, -0.313f)
				quadToRelative(0.229f, -0.104f, 0.479f, -0.104f)
				quadToRelative(0.25f, 0f, 0.479f, 0.104f)
				quadToRelative(0.229f, 0.104f, 0.438f, 0.313f)
				lineToRelative(8f, 7.958f)
				quadToRelative(0.375f, 0.417f, 0.375f, 0.938f)
				quadToRelative(0f, 0.52f, -0.375f, 0.937f)
				quadToRelative(-0.417f, 0.417f, -0.938f, 0.417f)
				quadToRelative(-0.521f, 0f, -0.937f, -0.417f)
				lineTo(20f, 22.333f)
				lineToRelative(-7.042f, 7.042f)
				quadToRelative(-0.416f, 0.417f, -0.937f, 0.417f)
				quadToRelative(-0.521f, 0f, -0.938f, -0.417f)
				close()
			}
		}.build()
	}
}