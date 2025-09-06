package ca.josephroque.bowlingcompanion.core.designsystem.images

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Composable
fun imageNotFound(accentColor: Color = MaterialTheme.colorScheme.primary): ImageVector {
	LaunchedEffect(isSystemInDarkTheme()) {
		// Clear cached image when theme changes
		_NotFound = null
	}
	if (_NotFound != null) {
		return _NotFound!!
	}
	_NotFound = ImageVector.Builder(
		name = "NotFound",
		defaultWidth = 885.2.dp,
		defaultHeight = 708.32.dp,
		viewportWidth = 885.2f,
		viewportHeight = 708.32f
	).apply {
		path(fill = SolidColor(Color(0xFFF0F0F0))) {
			moveTo(148.56f, 708.13f)
			reflectiveCurveToRelative(-12.75f, -31.39f, 25.5f, -54.93f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF0F0F0))) {
			moveTo(132.87f, 707.56f)
			reflectiveCurveToRelative(-3.89f, -22.37f, -34f, -22.18f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF0F0F0))) {
			moveTo(575.56f, 708.13f)
			reflectiveCurveToRelative(-12.75f, -31.39f, 25.5f, -54.93f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF0F0F0))) {
			moveTo(559.87f, 707.56f)
			reflectiveCurveToRelative(-3.89f, -22.37f, -34f, -22.18f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF0F0F0))) {
			moveTo(565.05f, 394.03f)
			moveToRelative(-110.85f, 0f)
			arcToRelative(110.85f, 110.85f, 0f, isMoreThanHalf = true, isPositiveArc = true, 221.71f, 0f)
			arcToRelative(110.85f, 110.85f, 0f, isMoreThanHalf = true, isPositiveArc = true, -221.71f, 0f)
		}
		path(fill = SolidColor(Color(0xFFF0F0F0))) {
			moveTo(565.36f, 394.03f)
			lineToRelative(0.31f, 0f)
			lineToRelative(5.48f, 314.29f)
			lineToRelative(-11.57f, 0f)
			lineToRelative(5.79f, -314.29f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF0F0F0))) {
			moveTo(803.05f, 479.09f)
			moveToRelative(-80.85f, 0f)
			arcToRelative(80.85f, 80.85f, 0f, isMoreThanHalf = true, isPositiveArc = true, 161.71f, 0f)
			arcToRelative(80.85f, 80.85f, 0f, isMoreThanHalf = true, isPositiveArc = true, -161.71f, 0f)
		}
		path(fill = SolidColor(Color(0xFFF0F0F0))) {
			moveTo(803.28f, 479.08f)
			lineToRelative(0.22f, 0f)
			lineToRelative(4f, 229.23f)
			lineToRelative(-8.44f, 0f)
			lineToRelative(4.22f, -229.23f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(355.33f, 479.91f)
			arcToRelative(10.66f, 10.66f, 0f, isMoreThanHalf = false, isPositiveArc = false, 9f, -13.65f)
			lineTo(395.06f, 444.09f)
			lineToRelative(-18.41f, -6.99f)
			lineToRelative(-26.36f, 22.12f)
			arcToRelative(10.72f, 10.72f, 0f, isMoreThanHalf = false, isPositiveArc = false, 5.04f, 20.69f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(332.8f, 697.35f)
			lineToRelative(-9.76f, -0f)
			lineToRelative(-4.64f, -37.64f)
			lineToRelative(14.4f, 0f)
			lineToRelative(-0f, 37.64f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(335.28f, 706.81f)
			lineToRelative(-31.47f, -0f)
			verticalLineToRelative(-0.4f)
			arcToRelative(12.25f, 12.25f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12.25f, -12.25f)
			horizontalLineToRelative(0f)
			lineToRelative(19.22f, 0f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(258.25f, 697.35f)
			lineToRelative(-9.76f, -0f)
			lineToRelative(-4.64f, -37.64f)
			lineToRelative(14.4f, 0f)
			lineToRelative(-0f, 37.64f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(260.74f, 706.81f)
			lineToRelative(-31.47f, -0f)
			verticalLineToRelative(-0.4f)
			arcToRelative(12.25f, 12.25f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12.25f, -12.25f)
			horizontalLineToRelative(0f)
			lineToRelative(19.22f, 0f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(265.79f, 488.03f)
			lineToRelative(-20.68f, 113.1f)
			lineToRelative(-7.23f, 89.84f)
			lineToRelative(23.49f, -1.16f)
			lineToRelative(13.37f, -87.24f)
			lineToRelative(33.23f, -75.96f)
			lineToRelative(9.89f, 162.99f)
			lineToRelative(21.62f, 0.07f)
			lineToRelative(11.63f, -206.17f)
			lineToRelative(-85.31f, 4.52f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(353.75f, 358.57f)
			lineToRelative(-28.99f, -11.53f)
			lineToRelative(-33.26f, 3.81f)
			lineTo(263.31f, 366.43f)
			reflectiveCurveToRelative(8.62f, 108.16f, -3.88f, 133.53f)
			curveToRelative(0f, 0f, 10.02f, 13.87f, 50.83f, 10.49f)
			curveToRelative(0f, 0f, 39.75f, -5.02f, 40.74f, -18.06f)
			reflectiveCurveToRelative(1.19f, -36.74f, 1.19f, -36.74f)
			lineToRelative(13.8f, -47.55f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(274.64f, 368.51f)
			lineToRelative(-11.33f, -2.08f)
			reflectiveCurveToRelative(-12.06f, 5.29f, -11.93f, 21.04f)
			reflectiveCurveToRelative(-21.3f, 68.57f, 4.33f, 70.78f)
			reflectiveCurveTo(274.64f, 420.8f, 274.64f, 420.8f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(344.56f, 365.28f)
			lineToRelative(9.19f, -6.71f)
			reflectiveCurveToRelative(14.04f, 3.17f, 19.23f, 14.47f)
			curveToRelative(0f, 0f, 57.57f, 41.75f, 41.73f, 57.58f)
			reflectiveCurveToRelative(-48.11f, 35.32f, -48.11f, 35.32f)
			lineToRelative(-11.95f, -13.53f)
			lineToRelative(29.65f, -26.86f)
			lineToRelative(-24.32f, -22.16f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(329.86f, 317.05f)
			arcToRelative(23.8f, 23.8f, 0f, isMoreThanHalf = false, isPositiveArc = true, -47.6f, 0f)
			verticalLineToRelative(-0.91f)
			arcToRelative(23.8f, 23.8f, 0f, isMoreThanHalf = false, isPositiveArc = true, 47.58f, 0.91f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(309.67f, 313.21f)
			curveToRelative(1.52f, -0.25f, 3.17f, -0.49f, 4.54f, 0.21f)
			curveToRelative(2.97f, 1.52f, 2.28f, 5.82f, 3.9f, 8.65f)
			curveToRelative(0.64f, 1.13f, 1.67f, 2.05f, 2.1f, 3.26f)
			reflectiveCurveToRelative(0.16f, 2.74f, 0.83f, 3.92f)
			curveToRelative(0.97f, 1.7f, 3.59f, 1.95f, 5.32f, 0.94f)
			arcToRelative(8.45f, 8.45f, 0f, isMoreThanHalf = false, isPositiveArc = false, 3.34f, -4.71f)
			curveToRelative(0.57f, -1.62f, 1.05f, -3.41f, 2.44f, -4.47f)
			arcToRelative(5.16f, 5.16f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.33f, -1.09f)
			arcToRelative(3.29f, 3.29f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.32f, -1.86f)
			curveToRelative(0f, -4.12f, -0.64f, -7.31f, 0.29f, -11.3f)
			curveToRelative(1.49f, -6.43f, 2.86f, -9.61f, -1.87f, -14.38f)
			arcToRelative(16.35f, 16.35f, 0f, isMoreThanHalf = false, isPositiveArc = false, -8.9f, -4.56f)
			curveToRelative(-3.05f, -0.5f, -7.53f, 3.75f, -10.61f, 3.65f)
			curveToRelative(-7.84f, -0.31f, -14.45f, -7.57f, -21.92f, -5.23f)
			arcToRelative(15.42f, 15.42f, 0f, isMoreThanHalf = false, isPositiveArc = false, -8.05f, 5.93f)
			curveToRelative(-3.65f, 5.38f, -3.33f, 7.47f, -2.8f, 13.89f)
			arcToRelative(2.38f, 2.38f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.41f, 1.37f)
			curveToRelative(0.27f, 0.33f, 0.72f, 0.51f, 0.99f, 0.84f)
			arcToRelative(2.08f, 2.08f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.3f, 1.5f)
			arcToRelative(10.08f, 10.08f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.72f, 3.05f)
			arcToRelative(1.25f, 1.25f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.11f, 1.1f)
			arcToRelative(1.11f, 1.11f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.61f, 0.41f)
			curveToRelative(3.04f, 1.1f, 6.33f, -1.02f, 9.59f, -0.96f)
			reflectiveCurveToRelative(6.35f, 2.23f, 9.71f, 1.76f)
			curveTo(303.68f, 314.69f, 306.68f, 313.71f, 309.67f, 313.21f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(309.52f, 445.52f)
			arcToRelative(2.84f, 2.84f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.63f, 0.16f)
			lineToRelative(-26.43f, 3.69f)
			arcToRelative(2.84f, 2.84f, 0f, isMoreThanHalf = false, isPositiveArc = true, -3.06f, -1.8f)
			lineToRelative(-22.75f, -59.19f)
			arcToRelative(2.86f, 2.86f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.04f, -3.81f)
			lineToRelative(0f, 0f)
			lineToRelative(24.85f, -5.55f)
			arcToRelative(2.27f, 2.27f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.55f, -0.2f)
			lineToRelative(24.17f, -12.64f)
			arcToRelative(2.83f, 2.83f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.59f, -0.23f)
			lineToRelative(26.6f, -7.09f)
			arcToRelative(2.85f, 2.85f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3.4f, 1.74f)
			lineToRelative(19.83f, 51.61f)
			arcToRelative(2.84f, 2.84f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.38f, 2.73f)
			lineToRelative(-6.86f, 9.21f)
			arcToRelative(2.85f, 2.85f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.27f, 0.96f)
			lineToRelative(-14.99f, 5.76f)
			arcToRelative(2.26f, 2.26f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.31f, 0.15f)
			lineTo(309.91f, 445.34f)
			arcTo(2.86f, 2.86f, 0f, isMoreThanHalf = false, isPositiveArc = true, 309.52f, 445.52f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE4E4E4))) {
			moveTo(321.81f, 366.75f)
			lineToRelative(-3.38f, 0.9f)
			lineToRelative(-8.95f, 2.39f)
			lineToRelative(-0.02f, 0f)
			lineToRelative(-12.26f, 6.41f)
			lineToRelative(-4.55f, 2.38f)
			lineToRelative(-0f, 0f)
			lineToRelative(-6.83f, 3.57f)
			lineToRelative(-0.19f, 0.04f)
			lineToRelative(-7.1f, 1.58f)
			lineToRelative(-3.43f, 0.77f)
			lineToRelative(-13.99f, 3.12f)
			lineToRelative(21.96f, 57.14f)
			lineToRelative(14.41f, -2.01f)
			lineToRelative(3.53f, -0.49f)
			lineToRelative(7.53f, -1.05f)
			lineToRelative(0.09f, -0.01f)
			lineToRelative(24.21f, -13.87f)
			lineToRelative(14.73f, -5.66f)
			lineToRelative(6.72f, -9.03f)
			lineToRelative(-19.12f, -49.74f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(265.53f, 399.37f)
			lineToRelative(72.82f, -27.99f)
			lineToRelative(1.25f, 3.25f)
			lineToRelative(-72.82f, 27.99f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(269.68f, 410.18f)
			lineToRelative(72.82f, -27.99f)
			lineToRelative(1.25f, 3.25f)
			lineToRelative(-72.82f, 27.99f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(278.08f, 432.03f)
			lineToRelative(72.82f, -27.99f)
			lineToRelative(1.25f, 3.25f)
			lineToRelative(-72.82f, 27.99f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(321.81f, 366.75f)
			lineToRelative(21.8f, 56.73f)
			lineToRelative(-3.24f, 1.25f)
			lineToRelative(-21.93f, -57.08f)
			lineToRelative(3.38f, -0.9f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(297.21f, 376.45f)
			lineToRelative(-4.48f, 12.46f)
			lineToRelative(-1.55f, 4.32f)
			lineToRelative(-3.62f, 10.08f)
			lineToRelative(-0.78f, 2.15f)
			lineToRelative(-0.78f, 2.17f)
			lineToRelative(-1.02f, 2.84f)
			lineToRelative(-7.34f, 20.42f)
			lineToRelative(-1.91f, -4.96f)
			lineToRelative(5.98f, -16.66f)
			lineToRelative(1.35f, -3.77f)
			lineToRelative(0.2f, -0.55f)
			lineToRelative(1.6f, -4.46f)
			lineToRelative(2.02f, -5.62f)
			lineToRelative(1.55f, -4.32f)
			lineToRelative(4.21f, -11.72f)
			lineToRelative(0f, -0f)
			lineToRelative(4.55f, -2.38f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(278.55f, 384.03f)
			lineToRelative(22.49f, 58.52f)
			lineToRelative(-3.53f, 0.49f)
			lineToRelative(-22.39f, -58.25f)
			lineToRelative(3.43f, -0.77f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(273.73f, 429.71f)
			moveToRelative(-10.35f, 0f)
			arcToRelative(10.35f, 10.35f, 0f, isMoreThanHalf = true, isPositiveArc = true, 20.71f, 0f)
			arcToRelative(10.35f, 10.35f, 0f, isMoreThanHalf = true, isPositiveArc = true, -20.71f, 0f)
		}
		path(fill = SolidColor(Color(0xFFE4E4E4))) {
			moveTo(701.23f, 183.98f)
			moveToRelative(-183.98f, 0f)
			arcToRelative(183.98f, 183.98f, 0f, isMoreThanHalf = true, isPositiveArc = true, 367.95f, 0f)
			arcToRelative(183.98f, 183.98f, 0f, isMoreThanHalf = true, isPositiveArc = true, -367.95f, 0f)
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(837.07f, 61.09f)
			arcTo(183.99f, 183.99f, 0f, isMoreThanHalf = false, isPositiveArc = true, 530.51f, 255.81f)
			arcTo(183.99f, 183.99f, 0f, isMoreThanHalf = true, isPositiveArc = false, 837.07f, 61.09f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE4E4E4))) {
			moveTo(700.72f, 183.98f)
			lineToRelative(-0.51f, 0f)
			lineToRelative(-9.1f, 521.6f)
			lineToRelative(19.21f, 0f)
			lineToRelative(-9.6f, -521.6f)
			close()
		}
		path(fill = SolidColor(Color(0xFFCACACA))) {
			moveTo(835f, 707.8f)
			horizontalLineToRelative(-834f)
			arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, -2f)
			horizontalLineToRelative(834f)
			arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 2f)
			close()
		}
	}.build()

	return _NotFound!!
}

@Suppress("ObjectPropertyName")
private var _NotFound: ImageVector? = null