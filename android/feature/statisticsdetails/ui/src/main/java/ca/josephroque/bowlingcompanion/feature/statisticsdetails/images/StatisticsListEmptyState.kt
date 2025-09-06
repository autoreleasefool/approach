package ca.josephroque.bowlingcompanion.feature.statisticsdetails.images

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
fun imageStatisticsListEmptyState(accentColor: Color = MaterialTheme.colorScheme.primary): ImageVector {
	LaunchedEffect(isSystemInDarkTheme()) {
		// Clear cached image when theme changes
		_StatisticsListEmptyState = null
	}
	if (_StatisticsListEmptyState != null) {
		return _StatisticsListEmptyState!!
	}
	_StatisticsListEmptyState = ImageVector.Builder(
		name = "StatisticsListEmptyState",
		defaultWidth = 822.03.dp,
		defaultHeight = 595.29.dp,
		viewportWidth = 822.03f,
		viewportHeight = 595.29f
	).apply {
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(695.21f, 7f)
			verticalLineToRelative(221.92f)
			curveToRelative(-0.67f, -0.68f, -1.33f, -1.36f, -2f, -2.04f)
			quadToRelative(-0.61f, -0.62f, -1.23f, -1.25f)
			arcTo(242.56f, 242.56f, 0f, isMoreThanHalf = false, isPositiveArc = false, 481.19f, 157.24f)
			curveTo(453.67f, 161.68f, 427.82f, 168.48f, 412.36f, 178.9f)
			curveToRelative(-116.28f, 78.36f, -248.21f, 3.69f, -285.15f, -17.19f)
			curveToRelative(-0.67f, -0.37f, -1.34f, -0.75f, -2f, -1.14f)
			verticalLineToRelative(-153.57f)
			arcToRelative(7f, 7f, 0f, isMoreThanHalf = false, isPositiveArc = true, 7f, -7f)
			horizontalLineToRelative(556f)
			arcTo(7f, 7f, 0f, isMoreThanHalf = false, isPositiveArc = true, 695.21f, 7f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(413.71f, 340.16f)
			lineToRelative(-15.01f, 3.39f)
			curveToRelative(-1.74f, 9.89f, -1.42f, 50.05f, -1.42f, 50.05f)
			lineToRelative(-1.29f, 38.43f)
			arcToRelative(6.8f, 6.8f, 0f, isMoreThanHalf = true, isPositiveArc = false, 11.72f, 0.99f)
			lineToRelative(1.95f, -47.39f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(396.36f, 301.5f)
			horizontalLineToRelative(0f)
			arcToRelative(16.24f, 16.24f, 0f, isMoreThanHalf = false, isPositiveArc = true, 19.63f, 15.47f)
			lineToRelative(0.25f, 9.71f)
			reflectiveCurveToRelative(3.8f, 49.51f, 0.31f, 59.18f)
			lineToRelative(-4.96f, 27.92f)
			lineToRelative(-15.92f, 2.03f)
			lineToRelative(-0.78f, -29.38f)
			reflectiveCurveToRelative(4.7f, -32.43f, -0.4f, -37.69f)
			curveToRelative(-3.13f, -3.22f, -8.11f, -16.57f, -10.62f, -28.02f)
			arcTo(16.2f, 16.2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 396.36f, 301.5f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(284.61f, 563.91f)
			arcToRelative(0.55f, 0.55f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.52f, -0.37f)
			lineTo(261.25f, 399.24f)
			lineTo(215.52f, 563.67f)
			arcToRelative(0.55f, 0.55f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.91f, -0.63f)
			lineTo(260.97f, 397.72f)
			arcToRelative(0.56f, 0.56f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.53f, -0.23f)
			arcToRelative(0.55f, 0.55f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.45f, 0.37f)
			lineToRelative(23.19f, 165.33f)
			arcToRelative(0.55f, 0.55f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.52f, 0.73f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(251.76f, 563.91f)
			arcToRelative(0.55f, 0.55f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.55f, -0.63f)
			lineToRelative(9.66f, -165.33f)
			arcToRelative(0.55f, 0.55f, 0f, isMoreThanHalf = true, isPositiveArc = true, 1.09f, 0.16f)
			lineToRelative(-9.66f, 165.33f)
			arcTo(0.55f, 0.55f, 0f, isMoreThanHalf = false, isPositiveArc = true, 251.76f, 563.91f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(335.93f, 366.31f)
			arcTo(7.18f, 7.18f, 0f, isMoreThanHalf = false, isPositiveArc = true, 332.91f, 374.07f)
			arcToRelative(6.96f, 6.96f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.2f, 0.64f)
			lineTo(229.14f, 416.94f)
			arcToRelative(7.18f, 7.18f, 0f, isMoreThanHalf = false, isPositiveArc = true, -9.37f, -3.9f)
			arcToRelative(7.1f, 7.1f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.32f, -0.97f)
			arcToRelative(7.18f, 7.18f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4.22f, -8.4f)
			lineTo(326.24f, 361.44f)
			arcToRelative(0.37f, 0.37f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.07f, -0.02f)
			arcToRelative(7.17f, 7.17f, 0f, isMoreThanHalf = false, isPositiveArc = true, 9.62f, 4.89f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(230.58f, 389.78f)
			lineToRelative(0f, 0f)
			arcToRelative(3.04f, 3.04f, 120.73f, isMoreThanHalf = false, isPositiveArc = true, 3.69f, 2.19f)
			lineToRelative(2.04f, 8.02f)
			arcToRelative(3.04f, 3.04f, 120.73f, isMoreThanHalf = false, isPositiveArc = true, -2.19f, 3.69f)
			lineToRelative(0f, 0f)
			arcToRelative(3.04f, 3.04f, 120.73f, isMoreThanHalf = false, isPositiveArc = true, -3.69f, -2.19f)
			lineToRelative(-2.04f, -8.02f)
			arcToRelative(3.04f, 3.04f, 120.73f, isMoreThanHalf = false, isPositiveArc = true, 2.19f, -3.69f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(224.42f, 390.21f)
			lineToRelative(11.77f, -2.99f)
			arcToRelative(1.93f, 1.93f, 120.73f, isMoreThanHalf = false, isPositiveArc = true, 2.35f, 1.4f)
			lineToRelative(0f, 0f)
			arcToRelative(1.93f, 1.93f, 120.73f, isMoreThanHalf = false, isPositiveArc = true, -1.4f, 2.35f)
			lineTo(225.37f, 393.95f)
			arcToRelative(1.93f, 1.93f, 120.73f, isMoreThanHalf = false, isPositiveArc = true, -2.35f, -1.4f)
			lineToRelative(0f, 0f)
			arcToRelative(1.93f, 1.93f, 120.73f, isMoreThanHalf = false, isPositiveArc = true, 1.4f, -2.35f)
			close()
		}
		path(fill = SolidColor(Color.LightGray)) {
			moveTo(219.89f, 413.55f)
			arcToRelative(7.18f, 7.18f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.37f, -8.21f)
			arcToRelative(9.38f, 9.38f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8.24f, 5.26f)
			arcToRelative(9.28f, 9.28f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.8f, 5.78f)
			arcToRelative(7.17f, 7.17f, 0f, isMoreThanHalf = false, isPositiveArc = true, -10.41f, -2.83f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(378.14f, 573.68f)
			arcToRelative(1.44f, 9.46f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, 0.11f, -2.88f)
			arcToRelative(1.44f, 9.46f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, -0.11f, 2.88f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(438f, 579.25f)
			arcToRelative(1.44f, 9.46f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, 0.11f, -2.88f)
			arcToRelative(1.44f, 9.46f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, -0.11f, 2.88f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFF6584))) {
			moveTo(353.91f, 582.99f)
			arcToRelative(1.44f, 9.46f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, 0.11f, -2.88f)
			arcToRelative(1.44f, 9.46f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, -0.11f, 2.88f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFF6584))) {
			moveTo(418.46f, 591.99f)
			arcToRelative(1.44f, 9.46f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, 0.11f, -2.88f)
			arcToRelative(1.44f, 9.46f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, -0.11f, 2.88f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(329.78f, 585.36f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, 0.01f, -2.59f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, -0.01f, 2.59f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(353.01f, 590.72f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, 0.01f, -2.59f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, -0.01f, 2.59f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(315.03f, 592.73f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, 0.01f, -2.59f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, -0.01f, 2.59f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(394.86f, 583.58f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, 0.01f, -2.59f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, -0.01f, 2.59f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFF6584))) {
			moveTo(367.76f, 578.37f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, 0.01f, -2.59f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, -0.01f, 2.59f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(412.82f, 575.48f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, 0.01f, -2.59f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, -0.01f, 2.59f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(378.57f, 590.97f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, 0.01f, -2.59f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, -0.01f, 2.59f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(688.21f, 0f)
			horizontalLineToRelative(-556f)
			arcToRelative(7.01f, 7.01f, 0f, isMoreThanHalf = false, isPositiveArc = false, -7f, 7f)
			verticalLineToRelative(581f)
			arcToRelative(7.02f, 7.02f, 0f, isMoreThanHalf = false, isPositiveArc = false, 7f, 7f)
			horizontalLineToRelative(556f)
			arcToRelative(7.01f, 7.01f, 0f, isMoreThanHalf = false, isPositiveArc = false, 7f, -7f)
			verticalLineToRelative(-581f)
			arcTo(7.01f, 7.01f, 0f, isMoreThanHalf = false, isPositiveArc = false, 688.21f, 0f)
			close()
			moveTo(693.21f, 588f)
			arcToRelative(5f, 5f, 0f, isMoreThanHalf = false, isPositiveArc = true, -5f, 5f)
			horizontalLineToRelative(-556f)
			arcToRelative(5f, 5f, 0f, isMoreThanHalf = false, isPositiveArc = true, -5f, -5f)
			verticalLineToRelative(-581f)
			arcToRelative(5.01f, 5.01f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5f, -5f)
			horizontalLineToRelative(556f)
			arcToRelative(5.01f, 5.01f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5f, 5f)
			close()
			moveTo(511.71f, 0.99f)
			quadToRelative(-0.84f, -0.51f, -1.68f, -0.99f)
			horizontalLineToRelative(-58.44f)
			curveToRelative(-0.42f, 0.66f, -0.82f, 1.33f, -1.21f, 2f)
			horizontalLineToRelative(63.01f)
			curveTo(512.83f, 1.66f, 512.27f, 1.33f, 511.71f, 0.99f)
			close()
			moveTo(125.21f, 198.46f)
			verticalLineToRelative(14.9f)
			curveToRelative(0.25f, 0.02f, 0.51f, 0.02f, 0.76f, 0.03f)
			lineToRelative(-0.5f, 28.64f)
			lineToRelative(-0.26f, -0.14f)
			verticalLineToRelative(2.82f)
			lineToRelative(0.21f, 0.11f)
			lineToRelative(-0.21f, 12.01f)
			verticalLineToRelative(41.4f)
			horizontalLineToRelative(2f)
			verticalLineToRelative(-106.52f)
			quadTo(126.37f, 194.95f, 125.21f, 198.46f)
			close()
			moveTo(511.71f, 0.99f)
			quadToRelative(-0.84f, -0.51f, -1.68f, -0.99f)
			horizontalLineToRelative(-58.44f)
			curveToRelative(-0.42f, 0.66f, -0.82f, 1.33f, -1.21f, 2f)
			horizontalLineToRelative(63.01f)
			curveTo(512.83f, 1.66f, 512.27f, 1.33f, 511.71f, 0.99f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(618.52f, 48.82f)
			moveToRelative(-10f, 0f)
			arcToRelative(10f, 10f, 0f, isMoreThanHalf = true, isPositiveArc = true, 20f, 0f)
			arcToRelative(10f, 10f, 0f, isMoreThanHalf = true, isPositiveArc = true, -20f, 0f)
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(490.52f, 90.82f)
			moveToRelative(-2f, 0f)
			arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 4f, 0f)
			arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, -4f, 0f)
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(388.52f, 129.82f)
			moveToRelative(-2f, 0f)
			arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 4f, 0f)
			arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, -4f, 0f)
		}
		path(fill = SolidColor(Color(0xFFFF6584))) {
			moveTo(275.52f, 78.82f)
			moveToRelative(-2f, 0f)
			arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 4f, 0f)
			arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, -4f, 0f)
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(323.52f, 28.82f)
			moveToRelative(-2f, 0f)
			arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 4f, 0f)
			arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, -4f, 0f)
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(657.52f, 17.82f)
			moveToRelative(-2f, 0f)
			arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 4f, 0f)
			arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, -4f, 0f)
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(554.52f, 51.82f)
			moveToRelative(-2f, 0f)
			arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 4f, 0f)
			arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, -4f, 0f)
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(672.52f, 84.82f)
			moveToRelative(-2f, 0f)
			arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 4f, 0f)
			arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, -4f, 0f)
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(321.52f, 117.82f)
			moveToRelative(-2f, 0f)
			arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 4f, 0f)
			arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, -4f, 0f)
		}
		path(fill = SolidColor(Color(0xFFFF6584))) {
			moveTo(168.52f, 111.82f)
			moveToRelative(-2f, 0f)
			arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 4f, 0f)
			arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = true, -4f, 0f)
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(243.56f, 134.91f)
			lineToRelative(-1.89f, 0.16f)
			lineToRelative(-0.43f, 1.85f)
			lineToRelative(-0.74f, -1.74f)
			lineToRelative(-1.89f, 0.16f)
			lineToRelative(1.43f, -1.24f)
			lineToRelative(-0.74f, -1.74f)
			lineToRelative(1.62f, 0.98f)
			lineToRelative(1.43f, -1.24f)
			lineToRelative(-0.43f, 1.85f)
			lineToRelative(1.62f, 0.98f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(484.56f, 28.91f)
			lineToRelative(-1.89f, 0.16f)
			lineToRelative(-0.43f, 1.85f)
			lineToRelative(-0.74f, -1.74f)
			lineToRelative(-1.89f, 0.16f)
			lineToRelative(1.43f, -1.24f)
			lineToRelative(-0.74f, -1.74f)
			lineToRelative(1.62f, 0.97f)
			lineToRelative(1.43f, -1.24f)
			lineToRelative(-0.43f, 1.84f)
			lineToRelative(1.62f, 0.98f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(598.56f, 109.91f)
			lineToRelative(-1.89f, 0.16f)
			lineToRelative(-0.43f, 1.85f)
			lineToRelative(-0.74f, -1.74f)
			lineToRelative(-1.89f, 0.17f)
			lineToRelative(1.43f, -1.24f)
			lineToRelative(-0.74f, -1.74f)
			lineToRelative(1.62f, 0.98f)
			lineToRelative(1.43f, -1.24f)
			lineToRelative(-0.43f, 1.85f)
			lineToRelative(1.62f, 0.98f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(386.56f, 86.91f)
			lineToRelative(-1.89f, 0.16f)
			lineToRelative(-0.43f, 1.85f)
			lineToRelative(-0.74f, -1.74f)
			lineToRelative(-1.89f, 0.17f)
			lineToRelative(1.43f, -1.24f)
			lineToRelative(-0.74f, -1.74f)
			lineToRelative(1.62f, 0.98f)
			lineToRelative(1.43f, -1.24f)
			lineToRelative(-0.43f, 1.85f)
			lineToRelative(1.62f, 0.98f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(297.56f, 175.91f)
			lineToRelative(-1.89f, 0.16f)
			lineToRelative(-0.43f, 1.85f)
			lineToRelative(-0.74f, -1.74f)
			lineToRelative(-1.89f, 0.16f)
			lineToRelative(1.43f, -1.24f)
			lineToRelative(-0.74f, -1.74f)
			lineToRelative(1.62f, 0.98f)
			lineToRelative(1.43f, -1.24f)
			lineToRelative(-0.43f, 1.85f)
			lineToRelative(1.62f, 0.98f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(674.56f, 173.91f)
			lineToRelative(-1.89f, 0.16f)
			lineToRelative(-0.43f, 1.85f)
			lineToRelative(-0.74f, -1.74f)
			lineToRelative(-1.89f, 0.16f)
			lineToRelative(1.43f, -1.24f)
			lineToRelative(-0.74f, -1.74f)
			lineToRelative(1.62f, 0.98f)
			lineToRelative(1.43f, -1.24f)
			lineToRelative(-0.43f, 1.85f)
			lineToRelative(1.62f, 0.98f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(190.56f, 57.91f)
			lineToRelative(-1.89f, 0.16f)
			lineToRelative(-0.43f, 1.85f)
			lineToRelative(-0.74f, -1.74f)
			lineToRelative(-1.89f, 0.17f)
			lineToRelative(1.43f, -1.24f)
			lineToRelative(-0.74f, -1.74f)
			lineToRelative(1.62f, 0.97f)
			lineToRelative(1.43f, -1.24f)
			lineToRelative(-0.43f, 1.85f)
			lineToRelative(1.62f, 0.98f)
			close()
		}
		path(fill = SolidColor(Color(0xFFA0616A))) {
			moveTo(410.66f, 359.62f)
			lineToRelative(3.26f, -50.58f)
			arcToRelative(8.18f, 8.18f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8.58f, -7.62f)
			horizontalLineToRelative(0f)
			arcToRelative(8.18f, 8.18f, 0f, isMoreThanHalf = false, isPositiveArc = true, 7.71f, 9.02f)
			lineToRelative(-5f, 52.43f)
			lineToRelative(-1.7f, 31.99f)
			arcToRelative(7.14f, 7.14f, 0f, isMoreThanHalf = true, isPositiveArc = true, -8.89f, 2.21f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(409.91f, 320.34f)
			horizontalLineToRelative(0f)
			arcToRelative(2.41f, 2.41f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.26f, -2.91f)
			arcToRelative(3.13f, 3.13f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.44f, -1.86f)
			arcToRelative(2.88f, 2.88f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.06f, -0.37f)
			arcToRelative(4.72f, 4.72f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.02f, -4.24f)
			curveToRelative(1.67f, -1.86f, 2.54f, -4.74f, 2.97f, -7.32f)
			arcToRelative(35.59f, 35.59f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.92f, -8.34f)
			curveToRelative(1.63f, -3.79f, 5.32f, -7.09f, 10.12f, -5.28f)
			arcToRelative(9.5f, 9.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4.79f, 4.19f)
			curveToRelative(2.29f, 3.96f, 2.14f, 8.63f, 1.97f, 13.11f)
			lineToRelative(0.94f, -1.74f)
			arcToRelative(36.33f, 36.33f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2.08f, 14.38f)
			curveToRelative(-1.08f, -0.25f, -1.9f, 1.18f, -1.58f, 2.24f)
			arcToRelative(6.35f, 6.35f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2.06f, 2.54f)
			curveToRelative(1.01f, 0.96f, 1.95f, 2.36f, 0.92f, 3.72f)
			arcToRelative(2.97f, 2.97f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2.11f, 1.11f)
			arcToRelative(56.21f, 56.21f, 0f, isMoreThanHalf = false, isPositiveArc = true, -22.86f, -2.59f)
			curveToRelative(-1.63f, -0.54f, -3.38f, -1.27f, -4.19f, -2.79f)
			reflectiveCurveTo(408.19f, 320.26f, 409.91f, 320.34f)
			close()
		}
		path(fill = SolidColor(Color(0xFFA0616A))) {
			moveTo(477.38f, 578.3f)
			lineToRelative(8.92f, 0f)
			lineToRelative(4.24f, -34.39f)
			lineToRelative(-13.16f, 0f)
			lineToRelative(0f, 34.39f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(474.47f, 572.19f)
			lineToRelative(14.25f, -0.85f)
			verticalLineToRelative(6.1f)
			lineToRelative(13.54f, 9.35f)
			arcToRelative(3.81f, 3.81f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2.17f, 6.95f)
			lineTo(483.13f, 593.75f)
			lineToRelative(-2.92f, -6.04f)
			lineToRelative(-1.14f, 6.04f)
			lineTo(472.67f, 593.75f)
			close()
		}
		path(fill = SolidColor(Color(0xFFA0616A))) {
			moveTo(429.44f, 570.44f)
			lineToRelative(8.3f, 3.25f)
			lineToRelative(16.47f, -30.49f)
			lineToRelative(-12.26f, -4.79f)
			lineToRelative(-12.52f, 32.03f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(428.95f, 563.7f)
			lineToRelative(13.58f, 4.39f)
			lineToRelative(-2.22f, 5.68f)
			lineToRelative(9.21f, 13.64f)
			arcToRelative(3.81f, 3.81f, 0f, isMoreThanHalf = false, isPositiveArc = true, -4.55f, 5.68f)
			lineToRelative(-15.8f, -6.17f)
			lineToRelative(-0.52f, -6.69f)
			lineToRelative(-3.26f, 5.21f)
			lineToRelative(-5.96f, -2.33f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(435.05f, 355.95f)
			horizontalLineToRelative(41.6f)
			reflectiveCurveToRelative(15.67f, 111.88f, 15.71f, 112.08f)
			reflectiveCurveToRelative(-8.84f, 3.43f, -4.8f, 7.47f)
			lineToRelative(4.04f, 4.04f)
			curveToRelative(3.23f, 3.23f, -2.4f, 14.27f, -2.4f, 14.27f)
			lineToRelative(6.44f, 65.7f)
			lineTo(469.05f, 559.52f)
			lineTo(456.05f, 423.81f)
			lineToRelative(17.58f, 65.06f)
			lineToRelative(-22.9f, 70.65f)
			lineToRelative(-16.49f, -9.69f)
			lineToRelative(-3.23f, -8.89f)
			lineToRelative(6.42f, -6.42f)
			reflectiveCurveToRelative(1.66f, -9.65f, 1.66f, -15.35f)
			reflectiveCurveToRelative(-4.85f, -0.85f, 0f, -5.7f)
			curveToRelative(2.1f, -2.1f, 8f, -27.83f, 8f, -27.83f)
			lineToRelative(-19.31f, -61.84f)
			lineToRelative(-4.6f, -27.6f)
			arcToRelative(34.68f, 34.68f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3.05f, -20.91f)
			curveTo(428.78f, 370.05f, 432f, 363.18f, 435.05f, 355.95f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(432.62f, 287.28f)
			lineToRelative(2.76f, -8.29f)
			arcToRelative(9.76f, 9.76f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8.65f, -6.66f)
			lineToRelative(2.46f, -0.15f)
			arcToRelative(9.76f, 9.76f, 0f, isMoreThanHalf = false, isPositiveArc = true, 9.93f, 6.86f)
			verticalLineToRelative(0f)
			arcToRelative(9.72f, 9.72f, 0f, isMoreThanHalf = false, isPositiveArc = false, 8.44f, 6.84f)
			curveToRelative(7.18f, 0.61f, 17.84f, 2.78f, 17.84f, 10.28f)
			curveToRelative(0f, 11.31f, -12.12f, 41.2f, -9.69f, 43.62f)
			reflectiveCurveToRelative(8.08f, 12.41f, 4.85f, 15.35f)
			curveToRelative(-0.84f, 0.77f, -0.16f, 4.45f, 0.11f, 7.09f)
			curveToRelative(0.49f, 4.78f, -33.23f, -5.78f, -42.92f, -3.05f)
			reflectiveCurveToRelative(-4.85f, -17.77f, -6.46f, -19.39f)
			arcToRelative(41.01f, 41.01f, 0f, isMoreThanHalf = false, isPositiveArc = true, -4.04f, -5.91f)
			arcToRelative(9.73f, 9.73f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.19f, -3.11f)
			lineToRelative(-2.78f, -14.08f)
			curveToRelative(-0.05f, -0.23f, -0.08f, -0.46f, -0.11f, -0.7f)
			curveToRelative(-0.37f, -3.01f, -2.52f, -23.29f, 5.7f, -26.28f)
			curveTo(435.05f, 286.48f, 432.62f, 287.28f, 432.62f, 287.28f)
			close()
		}
		path(fill = SolidColor(Color(0xFFA0616A))) {
			moveTo(470.27f, 361.66f)
			lineToRelative(-1.27f, -50.67f)
			arcToRelative(8.18f, 8.18f, 0f, isMoreThanHalf = false, isPositiveArc = true, 7.87f, -8.36f)
			horizontalLineToRelative(0f)
			arcTo(8.18f, 8.18f, 0f, isMoreThanHalf = false, isPositiveArc = true, 485.36f, 310.94f)
			lineToRelative(-0.29f, 52.67f)
			lineToRelative(1.17f, 32.01f)
			arcToRelative(7.14f, 7.14f, 0f, isMoreThanHalf = true, isPositiveArc = true, -8.66f, 3f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(464.6f, 321.44f)
			verticalLineToRelative(0f)
			arcToRelative(2.41f, 2.41f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.52f, -2.88f)
			arcToRelative(3.13f, 3.13f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.27f, -1.89f)
			arcToRelative(2.88f, 2.88f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.09f, -0.37f)
			arcToRelative(4.72f, 4.72f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.63f, -4.32f)
			curveToRelative(1.49f, -2f, 2.11f, -4.95f, 2.31f, -7.56f)
			arcToRelative(35.59f, 35.59f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.17f, -8.48f)
			curveToRelative(1.28f, -3.92f, 4.66f, -7.54f, 9.61f, -6.16f)
			arcToRelative(9.5f, 9.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5.15f, 3.74f)
			curveToRelative(2.63f, 3.74f, 2.91f, 8.41f, 3.13f, 12.88f)
			lineToRelative(0.78f, -1.81f)
			arcToRelative(36.33f, 36.33f, 0f, isMoreThanHalf = false, isPositiveArc = false, 3.36f, 14.13f)
			curveToRelative(-1.09f, -0.15f, -1.78f, 1.34f, -1.38f, 2.37f)
			arcToRelative(6.35f, 6.35f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2.28f, 2.35f)
			curveToRelative(1.09f, 0.86f, 2.15f, 2.17f, 1.25f, 3.63f)
			arcToRelative(2.97f, 2.97f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, 1.29f)
			arcToRelative(56.21f, 56.21f, 0f, isMoreThanHalf = false, isPositiveArc = true, -23f, -0.53f)
			curveToRelative(-1.68f, -0.39f, -3.48f, -0.97f, -4.42f, -2.41f)
			reflectiveCurveTo(462.88f, 321.5f, 464.6f, 321.44f)
			close()
		}
		path(fill = SolidColor(Color(0xFFA0616A))) {
			moveTo(443.76f, 255.78f)
			moveToRelative(-17.77f, 0f)
			arcToRelative(17.77f, 17.77f, 0f, isMoreThanHalf = true, isPositiveArc = true, 35.54f, 0f)
			arcToRelative(17.77f, 17.77f, 0f, isMoreThanHalf = true, isPositiveArc = true, -35.54f, 0f)
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(426.96f, 282.78f)
			curveToRelative(0.01f, -2.08f, 2.74f, -3.17f, 4.72f, -2.52f)
			curveToRelative(1.97f, 0.66f, 3.39f, 2.33f, 4.98f, 3.67f)
			curveToRelative(1.59f, 1.35f, 3.8f, 2.42f, 5.7f, 1.58f)
			curveToRelative(1.8f, -0.8f, 2.53f, -2.93f, 3.88f, -4.37f)
			arcToRelative(9.09f, 9.09f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5.99f, -2.44f)
			curveToRelative(-0.05f, -0.2f, -0.09f, -0.41f, -0.12f, -0.61f)
			arcToRelative(12.82f, 12.82f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.13f, -3.24f)
			arcToRelative(9.91f, 9.91f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.77f, 2.52f)
			arcToRelative(9.25f, 9.25f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.49f, 1.26f)
			arcToRelative(21.95f, 21.95f, 0f, isMoreThanHalf = false, isPositiveArc = false, 6.67f, -0.5f)
			curveToRelative(2.61f, -0.77f, 5.05f, -3.08f, 4.85f, -5.8f)
			arcToRelative(6.4f, 6.4f, 0f, isMoreThanHalf = false, isPositiveArc = false, -3.46f, -4.7f)
			curveToRelative(-1.75f, -1.01f, 4.39f, -5.51f, 2.56f, -6.37f)
			arcToRelative(14.26f, 14.26f, 0f, isMoreThanHalf = false, isPositiveArc = true, -7.65f, -8.18f)
			arcToRelative(10.69f, 10.69f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.47f, -9.62f)
			curveToRelative(1.62f, -0.2f, -4.79f, 0.56f, -3.25f, 0f)
			curveToRelative(-0.18f, -1.07f, -0.26f, 0.73f, -0.16f, -0.35f)
			curveToRelative(0.45f, 0.93f, 1.08f, -1.11f, 1.8f, -0.37f)
			arcToRelative(6.31f, 6.31f, 0f, isMoreThanHalf = false, isPositiveArc = false, 3.01f, -3.29f)
			curveToRelative(1.17f, -3.12f, -0.82f, -3.74f, -3.4f, -5.86f)
			arcToRelative(15.99f, 15.99f, 0f, isMoreThanHalf = false, isPositiveArc = false, -25.06f, 6.64f)
			curveToRelative(-3.91f, -1.96f, -8.94f, -0.11f, -11.68f, 3.31f)
			curveToRelative(-2.73f, 3.42f, -3.55f, 8f, -3.58f, 12.37f)
			arcTo(41.83f, 41.83f, 0f, isMoreThanHalf = false, isPositiveArc = false, 426.96f, 282.78f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(362.46f, 585.86f)
			lineToRelative(-8.33f, -0f)
			lineToRelative(-3.96f, -32.11f)
			lineToRelative(12.29f, 0f)
			lineToRelative(-0f, 32.11f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(362.75f, 594.55f)
			lineToRelative(-25.6f, -0f)
			verticalLineToRelative(-0.32f)
			arcToRelative(9.96f, 9.96f, 0f, isMoreThanHalf = false, isPositiveArc = true, 9.96f, -9.96f)
			horizontalLineToRelative(0f)
			lineToRelative(4.68f, -3.55f)
			lineToRelative(8.72f, 3.55f)
			lineToRelative(2.23f, 0f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(400.29f, 585.86f)
			lineToRelative(-8.32f, -0f)
			lineToRelative(-3.96f, -32.11f)
			lineToRelative(12.29f, 0f)
			lineToRelative(-0f, 32.11f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(400.59f, 594.55f)
			lineToRelative(-25.6f, -0f)
			verticalLineToRelative(-0.32f)
			arcToRelative(9.96f, 9.96f, 0f, isMoreThanHalf = false, isPositiveArc = true, 9.96f, -9.96f)
			horizontalLineToRelative(0f)
			lineToRelative(4.68f, -3.55f)
			lineToRelative(8.72f, 3.55f)
			lineToRelative(2.23f, 0f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(342.22f, 379.72f)
			reflectiveCurveToRelative(-13.45f, 16.69f, -10.84f, 21.01f)
			reflectiveCurveToRelative(6.81f, 2.83f, 2.61f, 7.36f)
			reflectiveCurveToRelative(-5.89f, 4.59f, -2.94f, 7.92f)
			reflectiveCurveToRelative(5.5f, 76.26f, 5.5f, 76.26f)
			lineToRelative(13.42f, 75.89f)
			lineToRelative(14.77f, 1.33f)
			reflectiveCurveToRelative(2.75f, -9.27f, 2.82f, -12.87f)
			reflectiveCurveToRelative(0.07f, -0.46f, 0.07f, -7.91f)
			reflectiveCurveToRelative(-2.66f, -1.68f, -0.07f, -8.77f)
			reflectiveCurveToRelative(5.82f, -27.98f, 0f, -38.13f)
			reflectiveCurveToRelative(-1.25f, -1.25f, -1.25f, -1.25f)
			lineToRelative(4.51f, -68.82f)
			lineTo(379.39f, 502.58f)
			reflectiveCurveToRelative(0f, 24.38f, 4.2f, 42.04f)
			lineToRelative(0.7f, 24.87f)
			lineToRelative(18.12f, -0.69f)
			reflectiveCurveToRelative(4.73f, -7.36f, 3.05f, -15.77f)
			reflectiveCurveToRelative(5.04f, -12.61f, 5.04f, -16.82f)
			reflectiveCurveToRelative(-0.35f, -44.41f, -0.35f, -44.41f)
			lineToRelative(-3.93f, -83.71f)
			reflectiveCurveToRelative(3.44f, -19.01f, -4.12f, -26.58f)
			reflectiveCurveTo(342.22f, 379.72f, 342.22f, 379.72f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(358.34f, 290.93f)
			lineToRelative(11.61f, -13.07f)
			lineToRelative(18.35f, 2.04f)
			lineToRelative(11.75f, 16.44f)
			horizontalLineToRelative(0f)
			curveToRelative(12.98f, 2.99f, 15.38f, 13.6f, 13.1f, 26.73f)
			curveToRelative(-4.56f, 26.25f, -15.81f, 44.59f, -10.7f, 57.54f)
			curveToRelative(7.76f, 19.66f, 4f, 34.78f, -14.59f, 32.03f)
			reflectiveCurveToRelative(-61.41f, -20.96f, -49.34f, -33.03f)
			reflectiveCurveToRelative(8.38f, -27.49f, 8.38f, -27.49f)
			lineToRelative(1.37f, -37.06f)
			curveToRelative(-2.17f, -11.77f, -1.84f, -22.89f, 10.07f, -24.12f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(352.76f, 335.82f)
			lineToRelative(-12.94f, -8.34f)
			curveToRelative(-8.28f, 5.68f, -36.73f, 34.03f, -36.73f, 34.03f)
			lineToRelative(-28.34f, 25.98f)
			arcToRelative(6.8f, 6.8f, 0f, isMoreThanHalf = true, isPositiveArc = false, 7.5f, 9.06f)
			lineToRelative(35.2f, -31.79f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(368.21f, 296.37f)
			lineToRelative(0f, 0f)
			arcToRelative(16.24f, 16.24f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.71f, 24.85f)
			lineToRelative(-6.76f, 6.98f)
			reflectiveCurveToRelative(-32.69f, 37.38f, -42.04f, 41.66f)
			lineToRelative(-23.41f, 16.01f)
			lineToRelative(-12.6f, -9.94f)
			lineToRelative(20.42f, -21.13f)
			reflectiveCurveToRelative(26.45f, -19.35f, 26.63f, -26.68f)
			curveToRelative(0.11f, -4.49f, 6.15f, -17.39f, 12.57f, -27.2f)
			arcTo(16.2f, 16.2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 368.21f, 296.37f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(382.44f, 254.5f)
			moveToRelative(-6.37f, 17.86f)
			arcToRelative(18.96f, 18.96f, 64.63f, isMoreThanHalf = true, isPositiveArc = true, 12.74f, -35.73f)
			arcToRelative(18.96f, 18.96f, 64.63f, isMoreThanHalf = true, isPositiveArc = true, -12.74f, 35.73f)
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(372.62f, 232.88f)
			arcToRelative(10.67f, 10.67f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3.21f, -3.49f)
			arcToRelative(5.22f, 5.22f, 0f, isMoreThanHalf = false, isPositiveArc = true, 7.4f, 2.39f)
			arcToRelative(5.72f, 5.72f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.66f, -2.61f)
			arcToRelative(14.54f, 14.54f, 0f, isMoreThanHalf = false, isPositiveArc = false, 3.7f, 2.7f)
			lineToRelative(-1.99f, -4.44f)
			curveToRelative(7.54f, -0.57f, 12.87f, 1.5f, 15.26f, 7.08f)
			curveToRelative(0.69f, 1.6f, 3.26f, 1.57f, 4.02f, 3.23f)
			curveToRelative(0.75f, 1.66f, -0.12f, 4.33f, -1.88f, 4.48f)
			curveToRelative(3.38f, -0.06f, 2.34f, 2.79f, 3.33f, 6.22f)
			curveToRelative(0.99f, 3.43f, 0.43f, 7.28f, -0.68f, 10.82f)
			curveToRelative(-1.44f, 4.59f, -5.49f, 7.59f, -9.44f, 9.94f)
			curveToRelative(-2.58f, 1.53f, -7.73f, 3.86f, -7.75f, 3.88f)
			curveToRelative(-14.21f, 7.46f, -23.26f, -7.75f, -19.88f, -22.77f)
			curveToRelative(0.76f, -3.39f, 3.53f, -7.07f, 1.55f, -9.61f)
			curveToRelative(-1.55f, -1.98f, -4.47f, -1.61f, -6.93f, -1.09f)
			curveTo(364.16f, 236.32f, 370.77f, 235.74f, 372.62f, 232.88f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(600.49f, 574.92f)
			arcToRelative(1.44f, 10.36f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, 0.11f, -2.88f)
			arcToRelative(1.44f, 10.36f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, -0.11f, 2.88f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(666.07f, 580.72f)
			arcToRelative(1.44f, 10.36f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, 0.11f, -2.88f)
			arcToRelative(1.44f, 10.36f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, -0.11f, 2.88f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFF6584))) {
			moveTo(573.98f, 584.15f)
			arcToRelative(1.44f, 10.36f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, 0.11f, -2.88f)
			arcToRelative(1.44f, 10.36f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, -0.11f, 2.88f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFF6584))) {
			moveTo(644.73f, 593.39f)
			arcToRelative(1.44f, 10.36f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, 0.11f, -2.88f)
			arcToRelative(1.44f, 10.36f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, -0.11f, 2.88f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(547.55f, 586.43f)
			arcToRelative(1.29f, 4.6f, 90.48f, isMoreThanHalf = true, isPositiveArc = false, 0.02f, -2.59f)
			arcToRelative(1.29f, 4.6f, 90.48f, isMoreThanHalf = true, isPositiveArc = false, -0.02f, 2.59f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(573.02f, 591.88f)
			arcToRelative(1.29f, 4.6f, 90.48f, isMoreThanHalf = true, isPositiveArc = false, 0.02f, -2.59f)
			arcToRelative(1.29f, 4.6f, 90.48f, isMoreThanHalf = true, isPositiveArc = false, -0.02f, 2.59f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(531.44f, 593.75f)
			arcToRelative(1.29f, 4.6f, 90.48f, isMoreThanHalf = true, isPositiveArc = false, 0.02f, -2.59f)
			arcToRelative(1.29f, 4.6f, 90.48f, isMoreThanHalf = true, isPositiveArc = false, -0.02f, 2.59f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(618.84f, 584.89f)
			arcToRelative(1.29f, 4.6f, 90.48f, isMoreThanHalf = true, isPositiveArc = false, 0.02f, -2.59f)
			arcToRelative(1.29f, 4.6f, 90.48f, isMoreThanHalf = true, isPositiveArc = false, -0.02f, 2.59f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFF6584))) {
			moveTo(589.13f, 579.57f)
			arcToRelative(1.29f, 4.6f, 90.48f, isMoreThanHalf = true, isPositiveArc = false, 0.02f, -2.59f)
			arcToRelative(1.29f, 4.6f, 90.48f, isMoreThanHalf = true, isPositiveArc = false, -0.02f, 2.59f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(638.47f, 576.86f)
			arcToRelative(1.29f, 4.6f, 90.48f, isMoreThanHalf = true, isPositiveArc = false, 0.02f, -2.59f)
			arcToRelative(1.29f, 4.6f, 90.48f, isMoreThanHalf = true, isPositiveArc = false, -0.02f, 2.59f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(601.02f, 592.22f)
			arcToRelative(1.29f, 4.6f, 90.48f, isMoreThanHalf = true, isPositiveArc = false, 0.02f, -2.59f)
			arcToRelative(1.29f, 4.6f, 90.48f, isMoreThanHalf = true, isPositiveArc = false, -0.02f, 2.59f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(215.14f, 574.68f)
			arcToRelative(1.44f, 9.46f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, 0.11f, -2.88f)
			arcToRelative(1.44f, 9.46f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, -0.11f, 2.88f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(275f, 580.25f)
			arcToRelative(1.44f, 9.46f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, 0.11f, -2.88f)
			arcToRelative(1.44f, 9.46f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, -0.11f, 2.88f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFF6584))) {
			moveTo(190.91f, 583.99f)
			arcToRelative(1.44f, 9.46f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, 0.11f, -2.88f)
			arcToRelative(1.44f, 9.46f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, -0.11f, 2.88f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFF6584))) {
			moveTo(255.46f, 592.99f)
			arcToRelative(1.44f, 9.46f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, 0.11f, -2.88f)
			arcToRelative(1.44f, 9.46f, 92.23f, isMoreThanHalf = true, isPositiveArc = false, -0.11f, 2.88f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(166.78f, 586.36f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, 0.01f, -2.59f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, -0.01f, 2.59f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(190.01f, 591.72f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, 0.01f, -2.59f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, -0.01f, 2.59f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(152.03f, 593.73f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, 0.01f, -2.59f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, -0.01f, 2.59f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(231.86f, 584.58f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, 0.01f, -2.59f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, -0.01f, 2.59f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFF6584))) {
			moveTo(204.76f, 579.37f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, 0.01f, -2.59f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, -0.01f, 2.59f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(249.82f, 576.48f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, 0.01f, -2.59f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, -0.01f, 2.59f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(215.57f, 591.97f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, 0.01f, -2.59f)
			arcToRelative(1.29f, 4.2f, 90.28f, isMoreThanHalf = true, isPositiveArc = false, -0.01f, 2.59f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(687.54f, 508.56f)
			arcToRelative(55.36f, 55.36f, 0f, isMoreThanHalf = false, isPositiveArc = true, -35.98f, -13.29f)
			curveToRelative(-3.35f, -2.86f, -6.91f, -7.84f, -10.59f, -14.79f)
			arcToRelative(33.83f, 33.83f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.07f, -29.21f)
			arcToRelative(38.83f, 38.83f, 0f, isMoreThanHalf = false, isPositiveArc = true, -6.41f, 5.26f)
			lineToRelative(-1.17f, 0.78f)
			lineToRelative(-0.07f, -1.41f)
			curveToRelative(-0.04f, -0.91f, -0.07f, -1.83f, -0.07f, -2.71f)
			curveToRelative(0f, -5.2f, 3.93f, -10.48f, 2.12f, -15.29f)
			curveToRelative(-7.65f, -20.26f, -31.9f, -40.4f, 3.25f, -71.77f)
			curveToRelative(3.25f, -2.9f, -1.09f, -8.7f, -1.09f, -13.09f)
			curveToRelative(0f, -43.02f, 46.74f, -107.55f, 78.03f, -78.02f)
			curveToRelative(18.26f, 17.24f, 56.32f, 29.49f, 74.08f, 53.46f)
			lineToRelative(0.24f, 0.71f)
			lineToRelative(-0.69f, 0.28f)
			arcToRelative(41.82f, 41.82f, 0f, isMoreThanHalf = false, isPositiveArc = true, -11.11f, 2.96f)
			arcToRelative(52.98f, 52.98f, 0f, isMoreThanHalf = false, isPositiveArc = false, 12.39f, 1.04f)
			lineToRelative(0.64f, -0.02f)
			lineToRelative(0.16f, 0.63f)
			arcToRelative(78.37f, 78.37f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.33f, 18.97f)
			lineToRelative(-0f, 0.69f)
			arcToRelative(34.56f, 34.56f, 0f, isMoreThanHalf = false, isPositiveArc = false, 11.42f, 25.77f)
			arcToRelative(55.49f, 55.49f, 0f, isMoreThanHalf = false, isPositiveArc = true, 18.1f, 40.95f)
			curveToRelative(0f, 9.12f, -6.2f, 21.09f, -11.4f, 29.53f)
			arcToRelative(16.7f, 16.7f, 0f, isMoreThanHalf = false, isPositiveArc = true, -12.26f, 7.85f)
			arcToRelative(16.31f, 16.31f, 0f, isMoreThanHalf = false, isPositiveArc = true, -13.16f, -4.25f)
			arcToRelative(53.92f, 53.92f, 0f, isMoreThanHalf = false, isPositiveArc = false, 9.51f, 13.46f)
			lineToRelative(0.71f, 0.73f)
			lineToRelative(-0.88f, 0.51f)
			arcToRelative(55.32f, 55.32f, 0f, isMoreThanHalf = false, isPositiveArc = true, -27.89f, 7.53f)
			lineToRelative(-0.57f, -0f)
			curveToRelative(-14.88f, 0f, -29f, 5.76f, -38.74f, 15.81f)
			arcTo(55.79f, 55.79f, 0f, isMoreThanHalf = false, isPositiveArc = true, 687.54f, 508.56f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(668.02f, 595.29f)
			arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.98f, -0.86f)
			curveToRelative(-0.04f, -0.3f, -4.19f, -30.18f, -0.46f, -69.9f)
			curveToRelative(3.44f, -36.68f, 14.47f, -89.51f, 47.54f, -132.9f)
			arcToRelative(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = true, 1.58f, 1.21f)
			curveToRelative(-32.78f, 43.02f, -43.72f, 95.46f, -47.14f, 131.88f)
			curveToRelative(-3.7f, 39.49f, 0.41f, 69.14f, 0.45f, 69.43f)
			arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.99f, 1.14f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(698.21f, 451.74f)
			arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.59f, -1.8f)
			arcToRelative(137.24f, 137.24f, 0f, isMoreThanHalf = false, isPositiveArc = true, 30.3f, -15.42f)
			curveToRelative(16.72f, -6.12f, 42.13f, -11.67f, 68.3f, -3.34f)
			arcToRelative(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = true, -0.6f, 1.9f)
			curveToRelative(-25.63f, -8.16f, -50.59f, -2.7f, -67.01f, 3.31f)
			arcTo(134.98f, 134.98f, 0f, isMoreThanHalf = false, isPositiveArc = false, 698.79f, 451.55f)
			arcTo(0.99f, 0.99f, 0f, isMoreThanHalf = false, isPositiveArc = true, 698.21f, 451.74f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(651.95f, 334.14f)
			arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.75f, -0.71f)
			arcToRelative(137.24f, 137.24f, 0f, isMoreThanHalf = false, isPositiveArc = true, 17.56f, 29.11f)
			curveToRelative(7.31f, 16.23f, 14.67f, 41.18f, 8.25f, 67.88f)
			arcToRelative(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = true, -1.94f, -0.47f)
			curveToRelative(6.29f, -26.15f, -0.95f, -50.65f, -8.13f, -66.6f)
			arcToRelative(134.98f, 134.98f, 0f, isMoreThanHalf = false, isPositiveArc = false, -17.27f, -28.64f)
			arcTo(0.99f, 0.99f, 0f, isMoreThanHalf = false, isPositiveArc = true, 651.95f, 334.14f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(94.68f, 491.63f)
			curveToRelative(-9.74f, -10.05f, -23.87f, -15.81f, -38.74f, -15.81f)
			lineToRelative(-0.57f, 0f)
			arcToRelative(55.32f, 55.32f, 0f, isMoreThanHalf = false, isPositiveArc = true, -27.89f, -7.53f)
			lineToRelative(-0.88f, -0.51f)
			lineToRelative(0.71f, -0.73f)
			arcToRelative(53.92f, 53.92f, 0f, isMoreThanHalf = false, isPositiveArc = false, 9.51f, -13.46f)
			arcToRelative(16.31f, 16.31f, 0f, isMoreThanHalf = false, isPositiveArc = true, -13.16f, 4.25f)
			arcToRelative(16.7f, 16.7f, 0f, isMoreThanHalf = false, isPositiveArc = true, -12.26f, -7.85f)
			curveToRelative(-5.2f, -8.44f, -11.4f, -20.41f, -11.4f, -29.53f)
			arcToRelative(55.49f, 55.49f, 0f, isMoreThanHalf = false, isPositiveArc = true, 18.1f, -40.95f)
			arcToRelative(34.56f, 34.56f, 0f, isMoreThanHalf = false, isPositiveArc = false, 11.42f, -25.77f)
			lineToRelative(-0f, -0.69f)
			arcToRelative(78.37f, 78.37f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.33f, -18.97f)
			lineToRelative(0.16f, -0.63f)
			lineToRelative(0.64f, 0.02f)
			arcToRelative(52.98f, 52.98f, 0f, isMoreThanHalf = false, isPositiveArc = false, 12.39f, -1.04f)
			arcToRelative(41.82f, 41.82f, 0f, isMoreThanHalf = false, isPositiveArc = true, -11.11f, -2.96f)
			lineToRelative(-0.69f, -0.28f)
			lineToRelative(0.24f, -0.71f)
			curveToRelative(17.77f, -23.98f, 55.82f, -36.22f, 74.08f, -53.46f)
			curveToRelative(31.29f, -29.53f, 78.03f, 35f, 78.03f, 78.02f)
			curveToRelative(0f, 4.39f, -4.34f, 10.19f, -1.09f, 13.09f)
			curveToRelative(35.15f, 31.36f, 10.9f, 51.5f, 3.25f, 71.77f)
			curveToRelative(-1.81f, 4.81f, 2.12f, 10.09f, 2.12f, 15.29f)
			curveToRelative(0f, 0.88f, -0.02f, 1.79f, -0.07f, 2.71f)
			lineToRelative(-0.07f, 1.41f)
			lineToRelative(-1.17f, -0.78f)
			arcToRelative(38.82f, 38.82f, 0f, isMoreThanHalf = false, isPositiveArc = true, -6.41f, -5.26f)
			arcToRelative(33.83f, 33.83f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.07f, 29.21f)
			curveToRelative(-3.68f, 6.95f, -7.25f, 11.93f, -10.59f, 14.79f)
			arcToRelative(55.31f, 55.31f, 0f, isMoreThanHalf = false, isPositiveArc = true, -75.79f, -3.64f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(154.02f, 595.29f)
			arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.98f, -0.86f)
			curveToRelative(0.04f, -0.3f, 4.19f, -30.18f, 0.46f, -69.9f)
			curveToRelative(-3.44f, -36.68f, -14.47f, -89.51f, -47.54f, -132.9f)
			arcToRelative(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = false, -1.58f, 1.21f)
			curveToRelative(32.78f, 43.02f, 43.72f, 95.46f, 47.14f, 131.88f)
			curveToRelative(3.7f, 39.49f, -0.41f, 69.14f, -0.45f, 69.43f)
			arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.99f, 1.14f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(123.83f, 451.74f)
			arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.59f, -1.8f)
			arcToRelative(137.24f, 137.24f, 0f, isMoreThanHalf = false, isPositiveArc = false, -30.3f, -15.42f)
			curveToRelative(-16.72f, -6.12f, -42.13f, -11.67f, -68.3f, -3.34f)
			arcToRelative(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = false, 0.6f, 1.9f)
			curveToRelative(25.63f, -8.16f, 50.59f, -2.7f, 67.01f, 3.31f)
			arcTo(134.98f, 134.98f, 0f, isMoreThanHalf = false, isPositiveArc = true, 123.24f, 451.55f)
			arcTo(0.99f, 0.99f, 0f, isMoreThanHalf = false, isPositiveArc = false, 123.83f, 451.74f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(170.08f, 334.14f)
			arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.75f, -0.71f)
			arcToRelative(137.24f, 137.24f, 0f, isMoreThanHalf = false, isPositiveArc = false, -17.56f, 29.11f)
			curveToRelative(-7.31f, 16.23f, -14.67f, 41.18f, -8.25f, 67.88f)
			arcToRelative(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = false, 1.94f, -0.47f)
			curveToRelative(-6.29f, -26.15f, 0.95f, -50.65f, 8.13f, -66.6f)
			arcTo(134.98f, 134.98f, 0f, isMoreThanHalf = false, isPositiveArc = true, 169.85f, 334.71f)
			arcTo(0.99f, 0.99f, 0f, isMoreThanHalf = false, isPositiveArc = false, 170.08f, 334.14f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(56.39f, 133.25f)
			lineToRelative(102.42f, -1.48f)
			lineToRelative(0.03f, 2f)
			lineToRelative(-102.42f, 1.48f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(160.93f, 137.69f)
			arcToRelative(4.97f, 4.97f, 0f, isMoreThanHalf = true, isPositiveArc = true, 2.19f, 0.51f)
			arcTo(4.97f, 4.97f, 0f, isMoreThanHalf = false, isPositiveArc = true, 160.93f, 137.69f)
			close()
			moveTo(162.14f, 130.36f)
			arcToRelative(2.98f, 2.98f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.72f, 1.52f)
			verticalLineToRelative(0f)
			arcToRelative(3.01f, 3.01f, 0f, isMoreThanHalf = true, isPositiveArc = false, 1.72f, -1.52f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(26.36f, 134.9f)
			moveToRelative(-18f, 0f)
			arcToRelative(18f, 18f, 0f, isMoreThanHalf = true, isPositiveArc = true, 36f, 0f)
			arcToRelative(18f, 18f, 0f, isMoreThanHalf = true, isPositiveArc = true, -36f, 0f)
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(626.87f, 87.77f)
			lineToRelative(0.03f, -2f)
			lineToRelative(102.42f, 1.48f)
			lineToRelative(-0.03f, 2f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(622.6f, 92.2f)
			arcToRelative(5.01f, 5.01f, 0f, isMoreThanHalf = true, isPositiveArc = true, 2.19f, -0.51f)
			arcTo(5f, 5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 622.6f, 92.2f)
			close()
			moveTo(622.59f, 84.19f)
			arcToRelative(3f, 3f, 0f, isMoreThanHalf = true, isPositiveArc = false, 2.7f, 1.69f)
			verticalLineToRelative(-0f)
			arcToRelative(3.01f, 3.01f, 0f, isMoreThanHalf = false, isPositiveArc = false, -2.7f, -1.69f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(759.36f, 88.9f)
			moveToRelative(-18f, 0f)
			arcToRelative(18f, 18f, 0f, isMoreThanHalf = true, isPositiveArc = true, 36f, 0f)
			arcToRelative(18f, 18f, 0f, isMoreThanHalf = true, isPositiveArc = true, -36f, 0f)
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(541.5f, 238.34f)
			lineToRelative(19.71f, -100.51f)
			lineToRelative(1.96f, 0.38f)
			lineToRelative(-19.71f, 100.51f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(557.62f, 132.83f)
			arcToRelative(5.01f, 5.01f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0.11f, 2.24f)
			arcTo(5.01f, 5.01f, 0f, isMoreThanHalf = false, isPositiveArc = true, 557.62f, 132.83f)
			close()
			moveTo(565.5f, 134.25f)
			arcToRelative(3f, 3f, 0f, isMoreThanHalf = true, isPositiveArc = false, -2.14f, 2.35f)
			lineToRelative(0f, 0f)
			arcToRelative(3.01f, 3.01f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2.14f, -2.35f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(536.49f, 267.98f)
			moveToRelative(-18f, 0f)
			arcToRelative(18f, 18f, 0f, isMoreThanHalf = true, isPositiveArc = true, 36f, 0f)
			arcToRelative(18f, 18f, 0f, isMoreThanHalf = true, isPositiveArc = true, -36f, 0f)
		}
	}.build()

	return _StatisticsListEmptyState!!
}

@Suppress("ObjectPropertyName")
private var _StatisticsListEmptyState: ImageVector? = null
