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
fun imageSeriesListEmptyState(accentColor: Color = MaterialTheme.colorScheme.primary): ImageVector {
	LaunchedEffect(isSystemInDarkTheme()) {
		// Clear cached image when theme changes
		_SeriesListEmptyState = null
	}
	if (_SeriesListEmptyState != null) {
		return _SeriesListEmptyState!!
	}
	_SeriesListEmptyState = ImageVector.Builder(
		name = "SeriesListEmptyState",
		defaultWidth = 579.23.dp,
		defaultHeight = 563.51.dp,
		viewportWidth = 579.23f,
		viewportHeight = 563.51f
	).apply {
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(223.64f, 513.25f)
			lineToRelative(-15.08f, 13.89f)
			lineToRelative(11.99f, -20.11f)
			curveToRelative(-9.44f, -17.13f, -24.89f, -31.93f, -24.89f, -31.93f)
			reflectiveCurveToRelative(-32.05f, 30.7f, -32.05f, 54.84f)
			reflectiveCurveToRelative(14.35f, 32.56f, 32.05f, 32.56f)
			curveToRelative(17.7f, 0f, 32.05f, -8.43f, 32.05f, -32.56f)
			curveTo(227.7f, 524.56f, 226.11f, 518.86f, 223.64f, 513.25f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(204.68f, 527.89f)
			verticalLineToRelative(1.19f)
			curveToRelative(-0.07f, 13.83f, -2.4f, 24.62f, -6.98f, 32.21f)
			curveToRelative(-0.06f, 0.11f, -0.14f, 0.22f, -0.2f, 0.33f)
			lineToRelative(-0.51f, -0.31f)
			lineToRelative(-0.49f, -0.31f)
			curveToRelative(5.08f, -8.2f, 6.87f, -19.8f, 6.94f, -31.86f)
			curveToRelative(0.01f, -0.39f, 0.01f, -0.78f, 0.01f, -1.18f)
			curveToRelative(-0.02f, -5.11f, -0.33f, -10.27f, -0.83f, -15.29f)
			curveToRelative(-0.04f, -0.39f, -0.08f, -0.78f, -0.12f, -1.18f)
			curveToRelative(-0.69f, -6.62f, -1.7f, -12.94f, -2.72f, -18.44f)
			curveToRelative(-0.07f, -0.39f, -0.15f, -0.78f, -0.22f, -1.16f)
			curveToRelative(-1.77f, -9.29f, -3.58f, -16.02f, -3.99f, -17.51f)
			curveToRelative(-0.05f, -0.18f, -0.08f, -0.28f, -0.08f, -0.31f)
			lineToRelative(0.56f, -0.16f)
			lineToRelative(0.01f, -0.01f)
			lineToRelative(0.57f, -0.16f)
			curveToRelative(0.01f, 0.02f, 0.11f, 0.36f, 0.27f, 0.98f)
			curveToRelative(0.63f, 2.33f, 2.27f, 8.66f, 3.88f, 17.01f)
			curveToRelative(0.07f, 0.38f, 0.15f, 0.77f, 0.22f, 1.15f)
			curveToRelative(0.84f, 4.46f, 1.65f, 9.44f, 2.3f, 14.67f)
			quadToRelative(0.25f, 1.97f, 0.44f, 3.89f)
			curveToRelative(0.05f, 0.39f, 0.09f, 0.79f, 0.12f, 1.18f)
			quadTo(204.65f, 520.78f, 204.68f, 527.89f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(200.75f, 491.75f)
			curveToRelative(-0.39f, 0.05f, -0.8f, 0.11f, -1.2f, 0.15f)
			arcToRelative(32.49f, 32.49f, 0f, isMoreThanHalf = false, isPositiveArc = true, -3.32f, 0.17f)
			arcToRelative(31.6f, 31.6f, 0f, isMoreThanHalf = false, isPositiveArc = true, -13.66f, -3.09f)
			curveToRelative(-0.25f, 0.31f, -0.49f, 0.62f, -0.75f, 0.94f)
			arcToRelative(32.77f, 32.77f, 0f, isMoreThanHalf = false, isPositiveArc = false, 14.41f, 3.32f)
			arcToRelative(33.62f, 33.62f, 0f, isMoreThanHalf = false, isPositiveArc = false, 3.55f, -0.19f)
			curveToRelative(0.4f, -0.04f, 0.8f, -0.09f, 1.2f, -0.15f)
			arcToRelative(32.5f, 32.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 9.39f, -2.84f)
			quadToRelative(-0.38f, -0.49f, -0.74f, -0.95f)
			arcTo(31.49f, 31.49f, 0f, isMoreThanHalf = false, isPositiveArc = true, 200.75f, 491.75f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(203.71f, 511.46f)
			quadToRelative(-0.61f, 0.04f, -1.22f, 0.04f)
			curveToRelative(-0.12f, 0.01f, -0.25f, 0.01f, -0.38f, 0.01f)
			arcTo(31.77f, 31.77f, 0f, isMoreThanHalf = false, isPositiveArc = true, 176.04f, 497.88f)
			curveToRelative(-0.24f, 0.35f, -0.47f, 0.69f, -0.7f, 1.05f)
			arcToRelative(32.95f, 32.95f, 0f, isMoreThanHalf = false, isPositiveArc = false, 26.78f, 13.75f)
			curveToRelative(0.16f, 0f, 0.33f, 0f, 0.49f, -0.01f)
			curveToRelative(0.41f, -0.01f, 0.82f, -0.02f, 1.22f, -0.04f)
			arcToRelative(32.78f, 32.78f, 0f, isMoreThanHalf = false, isPositiveArc = false, 17.46f, -6.12f)
			curveToRelative(-0.19f, -0.35f, -0.38f, -0.69f, -0.57f, -1.04f)
			arcTo(31.55f, 31.55f, 0f, isMoreThanHalf = false, isPositiveArc = true, 203.71f, 511.46f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(204.68f, 527.89f)
			curveToRelative(-0.41f, 0.04f, -0.82f, 0.06f, -1.24f, 0.08f)
			curveToRelative(-0.44f, 0.02f, -0.88f, 0.03f, -1.33f, 0.03f)
			arcToRelative(31.86f, 31.86f, 0f, isMoreThanHalf = false, isPositiveArc = true, -30.54f, -22.92f)
			curveToRelative(-0.26f, 0.48f, -0.53f, 0.95f, -0.78f, 1.43f)
			arcToRelative(33.02f, 33.02f, 0f, isMoreThanHalf = false, isPositiveArc = false, 31.32f, 22.67f)
			curveToRelative(0.44f, 0f, 0.88f, -0.01f, 1.32f, -0.03f)
			curveToRelative(0.42f, -0.01f, 0.83f, -0.04f, 1.24f, -0.06f)
			arcToRelative(32.93f, 32.93f, 0f, isMoreThanHalf = false, isPositiveArc = false, 21.73f, -10.61f)
			curveToRelative(-0.12f, -0.44f, -0.26f, -0.87f, -0.41f, -1.3f)
			arcTo(31.75f, 31.75f, 0f, isMoreThanHalf = false, isPositiveArc = true, 204.68f, 527.89f)
			close()
		}
		path(fill = SolidColor(Color(0xFFCACACA))) {
			moveTo(578.39f, 30.18f)
			lineTo(0.84f, 30.18f)
			arcToRelative(0.84f, 0.84f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, -1.68f)
			lineTo(578.39f, 28.5f)
			arcToRelative(0.84f, 0.84f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 1.68f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(19.71f, 9.22f)
			moveToRelative(-9.22f, 0f)
			arcToRelative(9.22f, 9.22f, 0f, isMoreThanHalf = true, isPositiveArc = true, 18.44f, 0f)
			arcToRelative(9.22f, 9.22f, 0f, isMoreThanHalf = true, isPositiveArc = true, -18.44f, 0f)
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(51.56f, 9.22f)
			moveToRelative(-9.22f, 0f)
			arcToRelative(9.22f, 9.22f, 0f, isMoreThanHalf = true, isPositiveArc = true, 18.44f, 0f)
			arcToRelative(9.22f, 9.22f, 0f, isMoreThanHalf = true, isPositiveArc = true, -18.44f, 0f)
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(83.41f, 9.22f)
			moveToRelative(-9.22f, 0f)
			arcToRelative(9.22f, 9.22f, 0f, isMoreThanHalf = true, isPositiveArc = true, 18.44f, 0f)
			arcToRelative(9.22f, 9.22f, 0f, isMoreThanHalf = true, isPositiveArc = true, -18.44f, 0f)
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(559.31f, 5.64f)
			lineTo(536.68f, 5.64f)
			arcToRelative(1.68f, 1.68f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, -3.35f)
			lineTo(559.31f, 2.29f)
			arcToRelative(1.68f, 1.68f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, 3.35f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(559.31f, 11.93f)
			lineTo(536.68f, 11.93f)
			arcToRelative(1.68f, 1.68f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, -3.35f)
			lineTo(559.31f, 8.57f)
			arcToRelative(1.68f, 1.68f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, 3.35f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(559.31f, 18.21f)
			lineTo(536.68f, 18.21f)
			arcToRelative(1.68f, 1.68f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, -3.35f)
			lineTo(559.31f, 14.86f)
			arcToRelative(1.68f, 1.68f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, 3.35f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF0F0F0))) {
			moveTo(17.4f, 68.09f)
			horizontalLineToRelative(529.28f)
			verticalLineToRelative(280.42f)
			horizontalLineToRelative(-529.28f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(546.68f, 120.58f)
			verticalLineToRelative(-2.75f)
			lineTo(443.65f, 117.83f)
			verticalLineToRelative(-49.74f)
			horizontalLineToRelative(-2.75f)
			verticalLineToRelative(49.74f)
			lineTo(337.87f, 117.83f)
			verticalLineToRelative(-49.74f)
			horizontalLineToRelative(-2.75f)
			verticalLineToRelative(49.74f)
			lineTo(232.09f, 117.83f)
			verticalLineToRelative(-49.74f)
			horizontalLineToRelative(-2.75f)
			verticalLineToRelative(49.74f)
			lineTo(126.31f, 117.83f)
			verticalLineToRelative(-49.74f)
			horizontalLineToRelative(-2.75f)
			verticalLineToRelative(49.74f)
			lineTo(17.4f, 117.83f)
			verticalLineToRelative(2.75f)
			lineTo(123.56f, 120.58f)
			verticalLineToRelative(54.95f)
			lineTo(17.4f, 175.53f)
			verticalLineToRelative(2.75f)
			lineTo(123.56f, 178.28f)
			verticalLineToRelative(54.95f)
			lineTo(17.4f, 233.23f)
			verticalLineToRelative(2.75f)
			lineTo(123.56f, 235.98f)
			verticalLineToRelative(54.94f)
			lineTo(17.4f, 290.92f)
			verticalLineToRelative(2.75f)
			lineTo(123.56f, 293.67f)
			verticalLineToRelative(54.84f)
			horizontalLineToRelative(2.75f)
			verticalLineToRelative(-54.84f)
			lineTo(229.34f, 293.67f)
			verticalLineToRelative(54.84f)
			horizontalLineToRelative(2.75f)
			verticalLineToRelative(-54.84f)
			lineTo(335.12f, 293.67f)
			verticalLineToRelative(54.84f)
			horizontalLineToRelative(2.75f)
			verticalLineToRelative(-54.84f)
			lineTo(440.9f, 293.67f)
			verticalLineToRelative(54.84f)
			horizontalLineToRelative(2.75f)
			verticalLineToRelative(-54.84f)
			lineTo(546.68f, 293.67f)
			verticalLineToRelative(-2.75f)
			lineTo(443.65f, 290.92f)
			verticalLineToRelative(-54.94f)
			lineTo(546.68f, 235.98f)
			verticalLineToRelative(-2.75f)
			lineTo(443.65f, 233.23f)
			lineTo(443.65f, 178.28f)
			lineTo(546.68f, 178.28f)
			verticalLineToRelative(-2.75f)
			lineTo(443.65f, 175.53f)
			verticalLineToRelative(-54.95f)
			close()
			moveTo(229.34f, 290.92f)
			lineTo(126.31f, 290.92f)
			verticalLineToRelative(-54.94f)
			lineTo(229.34f, 235.98f)
			close()
			moveTo(229.34f, 233.23f)
			lineTo(126.31f, 233.23f)
			lineTo(126.31f, 178.28f)
			lineTo(229.34f, 178.28f)
			close()
			moveTo(229.34f, 175.53f)
			lineTo(126.31f, 175.53f)
			verticalLineToRelative(-54.95f)
			lineTo(229.34f, 120.58f)
			close()
			moveTo(335.12f, 290.92f)
			lineTo(232.09f, 290.92f)
			verticalLineToRelative(-54.94f)
			lineTo(335.12f, 235.98f)
			close()
			moveTo(335.12f, 233.23f)
			lineTo(232.09f, 233.23f)
			lineTo(232.09f, 178.28f)
			lineTo(335.12f, 178.28f)
			close()
			moveTo(335.12f, 175.53f)
			lineTo(232.09f, 175.53f)
			verticalLineToRelative(-54.95f)
			lineTo(335.12f, 120.58f)
			close()
			moveTo(440.9f, 290.92f)
			lineTo(337.87f, 290.92f)
			verticalLineToRelative(-54.94f)
			lineTo(440.9f, 235.98f)
			close()
			moveTo(440.9f, 233.23f)
			lineTo(337.87f, 233.23f)
			lineTo(337.87f, 178.28f)
			lineTo(440.9f, 178.28f)
			close()
			moveTo(440.9f, 175.53f)
			lineTo(337.87f, 175.53f)
			verticalLineToRelative(-54.95f)
			lineTo(440.9f, 120.58f)
			close()
		}
		path(fill = SolidColor(Color(0xFFCACACA))) {
			moveTo(146.81f, 133.94f)
			horizontalLineToRelative(35.41f)
			verticalLineToRelative(34.28f)
			horizontalLineToRelative(-35.41f)
			close()
		}
		path(fill = SolidColor(Color(0xFFCACACA))) {
			moveTo(472.81f, 244.94f)
			horizontalLineToRelative(35.41f)
			verticalLineToRelative(34.28f)
			horizontalLineToRelative(-35.41f)
			close()
		}
		path(fill = SolidColor(Color(0xFFCACACA))) {
			moveTo(157.81f, 301.94f)
			horizontalLineToRelative(35.41f)
			verticalLineToRelative(34.28f)
			horizontalLineToRelative(-35.41f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(168.27f, 127.58f)
			horizontalLineToRelative(35.41f)
			verticalLineToRelative(34.28f)
			horizontalLineToRelative(-35.41f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(370.13f, 270.08f)
			lineToRelative(42.02f, -37.44f)
			lineToRelative(12.4f, 6.14f)
			lineToRelative(-50.4f, 52.86f)
			lineToRelative(-4.02f, -21.56f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(417.68f, 235.51f)
			moveToRelative(-10f, 0f)
			arcToRelative(10f, 10f, 0f, isMoreThanHalf = true, isPositiveArc = true, 20f, 0f)
			arcToRelative(10f, 10f, 0f, isMoreThanHalf = true, isPositiveArc = true, -20f, 0f)
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(330.8f, 551.86f)
			lineToRelative(-9.94f, 0f)
			lineToRelative(-4.73f, -38.35f)
			lineToRelative(14.67f, 0f)
			lineToRelative(-0f, 38.35f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(333.33f, 561.49f)
			lineToRelative(-32.06f, -0f)
			verticalLineToRelative(-0.41f)
			arcToRelative(12.48f, 12.48f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12.48f, -12.48f)
			horizontalLineToRelative(0f)
			lineToRelative(19.58f, 0f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(294.8f, 551.86f)
			lineToRelative(-9.94f, 0f)
			lineToRelative(-4.73f, -38.35f)
			lineToRelative(14.67f, 0f)
			lineToRelative(-0f, 38.35f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(297.33f, 561.49f)
			lineToRelative(-32.06f, -0f)
			verticalLineToRelative(-0.41f)
			arcToRelative(12.48f, 12.48f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12.48f, -12.48f)
			horizontalLineToRelative(0f)
			lineToRelative(19.58f, 0f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(339.15f, 332.64f)
			reflectiveCurveToRelative(25f, -2f, 17f, 42f)
			reflectiveCurveToRelative(-24.57f, 164.36f, -24.57f, 164.36f)
			lineToRelative(-15.02f, 2.07f)
			lineTo(307.68f, 380.51f)
			lineToRelative(-6.71f, 160.56f)
			lineToRelative(-19.11f, -2.07f)
			lineToRelative(-18.71f, -172.36f)
			reflectiveCurveToRelative(-6.01f, -13.86f, -0f, -23.43f)
			reflectiveCurveTo(339.15f, 332.64f, 339.15f, 332.64f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(323.15f, 223.64f)
			lineToRelative(6.94f, -3.72f)
			reflectiveCurveToRelative(17.1f, -1.46f, 24.08f, 18.63f)
			lineTo(372.68f, 266.51f)
			lineToRelative(30f, -26f)
			lineToRelative(13f, 9f)
			lineToRelative(-37.81f, 45.12f)
			lineToRelative(-15.72f, 2.01f)
			lineToRelative(-34f, -44f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(297.96f, 142.51f)
			curveToRelative(-28.52f, 0f, -36.5f, 31.33f, -36.5f, 49f)
			curveToRelative(0f, 17.67f, 16.34f, 15f, 36.5f, 15f)
			curveToRelative(8.65f, 0f, 16.6f, 0.49f, 22.85f, -0.35f)
			lineToRelative(3.01f, -7.35f)
			lineToRelative(2.42f, 6.09f)
			curveToRelative(5.13f, -1.89f, 8.22f, -5.72f, 8.22f, -13.39f)
			curveTo(334.46f, 173.83f, 327.46f, 142.51f, 297.96f, 142.51f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(297.84f, 181.92f)
			moveToRelative(-24.56f, 0f)
			arcToRelative(24.56f, 24.56f, 0f, isMoreThanHalf = true, isPositiveArc = true, 49.12f, 0f)
			arcToRelative(24.56f, 24.56f, 0f, isMoreThanHalf = true, isPositiveArc = true, -49.12f, 0f)
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(269.46f, 179.51f)
			verticalLineToRelative(0f)
			horizontalLineToRelative(9.71f)
			lineToRelative(4.29f, -12f)
			lineToRelative(0.86f, 12f)
			horizontalLineToRelative(4.64f)
			lineToRelative(2.5f, -7f)
			lineToRelative(0.5f, 7f)
			horizontalLineToRelative(34.5f)
			verticalLineToRelative(0f)
			arcToRelative(26f, 26f, 0f, isMoreThanHalf = false, isPositiveArc = false, -26f, -26f)
			horizontalLineToRelative(-5f)
			arcTo(26f, 26f, 0f, isMoreThanHalf = false, isPositiveArc = false, 269.46f, 179.51f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(398.27f, 197.58f)
			horizontalLineToRelative(35.41f)
			verticalLineToRelative(34.28f)
			horizontalLineToRelative(-35.41f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(346.92f, 323.59f)
			curveToRelative(-3.77f, 0.05f, -15.13f, -67.35f, -15.13f, -67.35f)
			lineToRelative(-1.7f, -36.32f)
			lineToRelative(-12.41f, -0.42f)
			lineTo(317.68f, 218.69f)
			arcToRelative(6.18f, 6.18f, 0f, isMoreThanHalf = false, isPositiveArc = false, -6.18f, -6.18f)
			horizontalLineToRelative(-23.64f)
			arcToRelative(6.18f, 6.18f, 0f, isMoreThanHalf = false, isPositiveArc = false, -6.16f, 5.79f)
			lineToRelative(-9.73f, -0.33f)
			lineToRelative(-16.86f, 53.55f)
			reflectiveCurveToRelative(15.53f, 49.76f, 8.04f, 71.69f)
			curveToRelative(0f, 0f, 73.04f, 6.27f, 87.02f, -7.65f)
			curveTo(350.17f, 335.56f, 350.69f, 323.54f, 346.92f, 323.59f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(276.15f, 224.64f)
			lineTo(271.97f, 217.97f)
			reflectiveCurveToRelative(-13.61f, -3.36f, -19.21f, 5.65f)
			reflectiveCurveTo(230.68f, 281.51f, 230.68f, 281.51f)
			lineToRelative(17f, 6f)
			lineToRelative(19.29f, -27.05f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(283.27f, 259.58f)
			horizontalLineToRelative(35.41f)
			verticalLineToRelative(34.28f)
			horizontalLineToRelative(-35.41f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(256.1f, 271.97f)
			lineTo(243.78f, 270.67f)
			lineToRelative(0f, 0f)
			arcToRelative(12.09f, 12.09f, 0f, isMoreThanHalf = false, isPositiveArc = false, 8.58f, 16.98f)
			lineToRelative(48.74f, 9.73f)
			lineToRelative(-5.08f, -15.66f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(301.25f, 287.56f)
			moveToRelative(-10f, 0f)
			arcToRelative(10f, 10f, 0f, isMoreThanHalf = true, isPositiveArc = true, 20f, 0f)
			arcToRelative(10f, 10f, 0f, isMoreThanHalf = true, isPositiveArc = true, -20f, 0f)
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(288.77f, 297.86f)
			lineToRelative(-37.13f, -8.49f)
			arcToRelative(16.04f, 16.04f, 0f, isMoreThanHalf = false, isPositiveArc = true, -11.63f, -10.64f)
			lineToRelative(-1.5f, -4.63f)
			lineToRelative(9.64f, -7.58f)
			lineToRelative(44.15f, 12.34f)
			close()
		}
		path(fill = SolidColor(Color(0xFFCACACA))) {
			moveTo(497.44f, 563.51f)
			horizontalLineToRelative(-381f)
			arcToRelative(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, -2f)
			horizontalLineToRelative(381f)
			arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 2f)
			close()
		}
	}.build()

	return _SeriesListEmptyState!!
}

@Suppress("ObjectPropertyName")
private var _SeriesListEmptyState: ImageVector? = null
