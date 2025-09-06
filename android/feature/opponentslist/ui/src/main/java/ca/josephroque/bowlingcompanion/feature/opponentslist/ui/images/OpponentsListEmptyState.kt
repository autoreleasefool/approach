package ca.josephroque.bowlingcompanion.feature.opponentslist.ui.images

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
fun imageOpponentsListEmptyState(accentColor: Color = MaterialTheme.colorScheme.primary): ImageVector {
	LaunchedEffect(isSystemInDarkTheme()) {
		// Clear cached image when theme changes
		_OpponentListEmptyState = null
	}
	if (_OpponentListEmptyState != null) {
		return _OpponentListEmptyState!!
	}
	_OpponentListEmptyState = ImageVector.Builder(
		name = "OpponentListEmptyState",
		defaultWidth = 877.dp,
		defaultHeight = 732.81.dp,
		viewportWidth = 877f,
		viewportHeight = 732.81f
	).apply {
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(229.06f, 216.95f)
			lineToRelative(84.87f, 49f)
			lineToRelative(-49f, 84.87f)
			lineToRelative(-84.87f, -49f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(749.59f, 150.8f)
			moveToRelative(-72f, 0f)
			arcToRelative(72f, 72f, 0f, isMoreThanHalf = true, isPositiveArc = true, 144f, 0f)
			arcToRelative(72f, 72f, 0f, isMoreThanHalf = true, isPositiveArc = true, -144f, 0f)
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(705.57f, 50.96f)
			curveToRelative(8.97f, 22f, 31.81f, 33.5f, 31.81f, 33.5f)
			reflectiveCurveToRelative(8.29f, -24.19f, -0.69f, -46.19f)
			reflectiveCurveToRelative(-31.81f, -33.5f, -31.81f, -33.5f)
			reflectiveCurveTo(696.59f, 28.95f, 705.57f, 50.96f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(714.01f, 44.95f)
			curveToRelative(18.93f, 14.36f, 24.13f, 39.39f, 24.13f, 39.39f)
			reflectiveCurveToRelative(-25.51f, 1.75f, -44.44f, -12.61f)
			reflectiveCurveToRelative(-24.13f, -39.39f, -24.13f, -39.39f)
			reflectiveCurveTo(695.08f, 30.58f, 714.01f, 44.95f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(740.98f, 38.38f)
			curveToRelative(-8.79f, 22.08f, -0.31f, 46.19f, -0.31f, 46.19f)
			reflectiveCurveToRelative(22.74f, -11.68f, 31.54f, -33.76f)
			reflectiveCurveToRelative(0.31f, -46.19f, 0.31f, -46.19f)
			reflectiveCurveTo(749.78f, 16.3f, 740.98f, 38.38f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(751.23f, 39.9f)
			curveToRelative(3.68f, 23.48f, -9.94f, 45.11f, -9.94f, 45.11f)
			reflectiveCurveToRelative(-19.59f, -16.43f, -23.27f, -39.9f)
			reflectiveCurveToRelative(9.94f, -45.11f, 9.94f, -45.11f)
			reflectiveCurveTo(747.55f, 16.43f, 751.23f, 39.9f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(745.63f, 143.23f)
			moveToRelative(-37f, 0f)
			arcToRelative(37f, 37f, 0f, isMoreThanHalf = true, isPositiveArc = true, 74f, 0f)
			arcToRelative(37f, 37f, 0f, isMoreThanHalf = true, isPositiveArc = true, -74f, 0f)
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(730.09f, 132.91f)
			moveToRelative(-12f, 0f)
			arcToRelative(12f, 12f, 0f, isMoreThanHalf = true, isPositiveArc = true, 24f, 0f)
			arcToRelative(12f, 12f, 0f, isMoreThanHalf = true, isPositiveArc = true, -24f, 0f)
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(855f, 594.88f)
			arcToRelative(22f, 22f, 0f, isMoreThanHalf = true, isPositiveArc = true, 22f, -22f)
			arcTo(22.02f, 22.02f, 0f, isMoreThanHalf = false, isPositiveArc = true, 855f, 594.88f)
			close()
			moveTo(855f, 552.88f)
			arcToRelative(20f, 20f, 0f, isMoreThanHalf = true, isPositiveArc = false, 20f, 20f)
			arcTo(20.02f, 20.02f, 0f, isMoreThanHalf = false, isPositiveArc = false, 855f, 552.88f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(267f, 367.88f)
			arcToRelative(22f, 22f, 0f, isMoreThanHalf = true, isPositiveArc = true, 22f, -22f)
			arcTo(22.02f, 22.02f, 0f, isMoreThanHalf = false, isPositiveArc = true, 267f, 367.88f)
			close()
			moveTo(267f, 325.88f)
			arcToRelative(20f, 20f, 0f, isMoreThanHalf = true, isPositiveArc = false, 20f, 20f)
			arcTo(20.02f, 20.02f, 0f, isMoreThanHalf = false, isPositiveArc = false, 267f, 325.88f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(795.54f, 161.52f)
			lineTo(678.56f, 292.08f)
			arcToRelative(57.99f, 57.99f, 0f, isMoreThanHalf = true, isPositiveArc = true, -77.2f, 86.16f)
			lineToRelative(-162.94f, 181.86f)
			arcToRelative(267.1f, 267.1f, 0f, isMoreThanHalf = false, isPositiveArc = false, 164.3f, 56.2f)
			curveTo(750.89f, 616.3f, 871f, 496.19f, 871f, 348.03f)
			arcTo(267.37f, 267.37f, 0f, isMoreThanHalf = false, isPositiveArc = false, 795.54f, 161.52f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(645f, 340.88f)
			moveToRelative(-35f, 0f)
			arcToRelative(35f, 35f, 0f, isMoreThanHalf = true, isPositiveArc = true, 70f, 0f)
			arcToRelative(35f, 35f, 0f, isMoreThanHalf = true, isPositiveArc = true, -70f, 0f)
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(461.97f, 71.97f)
			moveToRelative(-33.78f, 0f)
			arcToRelative(33.78f, 33.78f, 0f, isMoreThanHalf = true, isPositiveArc = true, 67.56f, 0f)
			arcToRelative(33.78f, 33.78f, 0f, isMoreThanHalf = true, isPositiveArc = true, -67.56f, 0f)
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(398.44f, 310.73f)
			lineToRelative(7.84f, 152.81f)
			reflectiveCurveToRelative(-1.31f, 109.71f, 6.53f, 111.02f)
			reflectiveCurveToRelative(22.2f, 10.45f, 26.12f, 3.92f)
			reflectiveCurveToRelative(23.51f, -207.67f, 23.51f, -207.67f)
			reflectiveCurveToRelative(5.22f, 208.98f, 20.9f, 210.28f)
			reflectiveCurveToRelative(33.96f, -6.53f, 36.57f, -5.22f)
			reflectiveCurveTo(521.22f, 314.65f, 521.22f, 314.65f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(421.95f, 573.26f)
			reflectiveCurveToRelative(-9.14f, 0f, -11.76f, 3.92f)
			reflectiveCurveToRelative(-11.76f, 23.51f, -11.76f, 23.51f)
			reflectiveCurveTo(381.46f, 616.28f, 399.75f, 617.59f)
			reflectiveCurveToRelative(27.43f, -2.53f, 27.43f, -5.14f)
			reflectiveCurveToRelative(0f, -9.14f, 3.92f, -10.45f)
			reflectiveCurveToRelative(7.84f, -7.84f, 7.84f, -10.45f)
			reflectiveCurveTo(435.01f, 571.95f, 421.95f, 573.26f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(492.48f, 574.57f)
			reflectiveCurveToRelative(-6.53f, 26.12f, 0f, 27.43f)
			reflectiveCurveToRelative(10.45f, 3.92f, 11.76f, 7.84f)
			reflectiveCurveToRelative(14.37f, 8.45f, 24.82f, 7.14f)
			reflectiveCurveToRelative(15.67f, -8.45f, 11.76f, -12.37f)
			reflectiveCurveToRelative(-23.51f, -26.12f, -24.82f, -30.04f)
			reflectiveCurveTo(492.48f, 574.57f, 492.48f, 574.57f)
			close()
		}
		path(fill = SolidColor(Color(0xFF9F616A))) {
			moveTo(446.77f, 96.53f)
			reflectiveCurveToRelative(-2.61f, 19.59f, -7.84f, 22.2f)
			reflectiveCurveToRelative(2.61f, 16.98f, 2.61f, 16.98f)
			lineToRelative(23.51f, 3.92f)
			lineToRelative(20.9f, -16.98f)
			reflectiveCurveToRelative(-3.92f, -24.82f, -3.92f, -26.12f)
			reflectiveCurveTo(446.77f, 96.53f, 446.77f, 96.53f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(446.77f, 96.53f)
			reflectiveCurveToRelative(-2.61f, 19.59f, -7.84f, 22.2f)
			reflectiveCurveToRelative(2.61f, 16.98f, 2.61f, 16.98f)
			lineToRelative(23.51f, 3.92f)
			lineToRelative(20.9f, -16.98f)
			reflectiveCurveToRelative(-3.92f, -24.82f, -3.92f, -26.12f)
			reflectiveCurveTo(446.77f, 96.53f, 446.77f, 96.53f)
			close()
		}
		path(fill = SolidColor(Color(0xFF9F616A))) {
			moveTo(461.14f, 82.16f)
			moveToRelative(-31.35f, 0f)
			arcToRelative(31.35f, 31.35f, 0f, isMoreThanHalf = true, isPositiveArc = true, 62.69f, 0f)
			arcToRelative(31.35f, 31.35f, 0f, isMoreThanHalf = true, isPositiveArc = true, -62.69f, 0f)
		}
		path(fill = SolidColor(Color(0xFF575A88))) {
			moveTo(460.48f, 135.06f)
			reflectiveCurveTo(447.42f, 112.86f, 440.89f, 114.16f)
			reflectiveCurveToRelative(-26.78f, 4.57f, -29.39f, 3.27f)
			reflectiveCurveToRelative(-19.59f, 58.77f, -19.59f, 58.77f)
			reflectiveCurveToRelative(-18.29f, 118.86f, -6.53f, 133.22f)
			reflectiveCurveToRelative(52.24f, 6.53f, 57.47f, 20.9f)
			reflectiveCurveTo(530.36f, 322.49f, 530.36f, 314.65f)
			reflectiveCurveToRelative(1.31f, -128f, 2.61f, -137.14f)
			reflectiveCurveToRelative(-11.76f, -57.47f, -11.76f, -57.47f)
			reflectiveCurveToRelative(-30.69f, -8.49f, -35.92f, -5.88f)
			reflectiveCurveTo(477.46f, 124.61f, 460.48f, 135.06f)
			close()
		}
		path(fill = SolidColor(Color(0xFF575A88))) {
			moveTo(414.12f, 121.35f)
			lineToRelative(-2.61f, -3.92f)
			reflectiveCurveToRelative(-27.43f, 3.92f, -35.26f, 2.61f)
			reflectiveCurveToRelative(-31.35f, 1.31f, -31.35f, 1.31f)
			reflectiveCurveToRelative(5.22f, -14.37f, 10.45f, -15.67f)
			reflectiveCurveToRelative(11.76f, -5.22f, 11.76f, -5.22f)
			reflectiveCurveToRelative(-13.06f, -19.59f, 0f, -26.12f)
			curveToRelative(0f, 0f, -60.08f, 23.51f, -64f, 45.71f)
			reflectiveCurveToRelative(91.43f, 56.16f, 91.43f, 56.16f)
			close()
		}
		path(fill = SolidColor(Color(0xFF575A88))) {
			moveTo(510.77f, 122.65f)
			lineToRelative(10.45f, -2.61f)
			lineToRelative(52.24f, 5.22f)
			lineTo(556.48f, 106.98f)
			reflectiveCurveToRelative(10.45f, -13.06f, 3.92f, -19.59f)
			curveToRelative(0f, 0f, 69.22f, 26.12f, 61.39f, 43.1f)
			reflectiveCurveToRelative(-90.12f, 47.02f, -94.04f, 48.33f)
			reflectiveCurveTo(510.77f, 122.65f, 510.77f, 122.65f)
			close()
		}
		path(fill = SolidColor(Color(0xFF9F616A))) {
			moveTo(374.21f, 77.84f)
			curveToRelative(3.23f, 0.64f, 6f, 2.66f, 8.64f, 4.63f)
			arcToRelative(83.96f, 83.96f, 0f, isMoreThanHalf = false, isPositiveArc = true, 7.37f, 5.92f)
			curveToRelative(11.88f, 11.11f, 16.25f, 27.86f, 20.02f, 43.68f)
			curveToRelative(-2.69f, 2.27f, -7.08f, 0.38f, -8.89f, -2.64f)
			reflectiveCurveTo(399.43f, 122.72f, 398.72f, 119.28f)
			reflectiveCurveToRelative(-2.44f, -7.15f, -5.8f, -8.21f)
			curveToRelative(0.7f, 2.19f, -1f, 4.56f, -3.12f, 5.43f)
			reflectiveCurveToRelative(-4.52f, 0.63f, -6.81f, 0.37f)
			arcToRelative(9.4f, 9.4f, 0f, isMoreThanHalf = false, isPositiveArc = true, -3.09f, -0.7f)
			curveToRelative(-1.77f, -0.86f, -2.81f, -2.7f, -3.77f, -4.42f)
			arcToRelative(135.84f, 135.84f, 0f, isMoreThanHalf = false, isPositiveArc = false, -8.28f, -12.96f)
			arcToRelative(9.62f, 9.62f, 0f, isMoreThanHalf = false, isPositiveArc = false, -2.61f, -2.73f)
			curveToRelative(-4.18f, -2.55f, -8.68f, -5.89f, -6.59f, -11.96f)
			curveTo(360.36f, 79.09f, 369.65f, 76.94f, 374.21f, 77.84f)
			close()
		}
		path(fill = SolidColor(Color(0xFF9F616A))) {
			moveTo(547.87f, 85.62f)
			curveToRelative(-4.7f, 0.23f, -8.75f, 3.29f, -12.2f, 6.5f)
			arcToRelative(85.49f, 85.49f, 0f, isMoreThanHalf = false, isPositiveArc = false, -26.25f, 51.22f)
			curveToRelative(-0.17f, 1.31f, -0.22f, 2.86f, 0.79f, 3.72f)
			curveToRelative(1.13f, 0.97f, 3f, 0.4f, 3.92f, -0.77f)
			arcToRelative(9.55f, 9.55f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.49f, -4.16f)
			arcToRelative(46.53f, 46.53f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4.62f, -12.72f)
			curveToRelative(1.1f, -2.05f, 2.64f, -4.2f, 4.94f, -4.56f)
			curveToRelative(3.42f, -0.54f, 5.98f, 3.08f, 9.25f, 4.23f)
			curveToRelative(3.86f, 1.35f, 8.18f, -1.07f, 10.52f, -4.42f)
			reflectiveCurveToRelative(3.26f, -7.45f, 4.55f, -11.33f)
			curveToRelative(1.29f, -3.88f, 3.21f, -7.85f, 6.73f, -9.92f)
			curveToRelative(3.75f, -2.2f, 12.39f, -3.73f, 8.73f, -9.91f)
			curveTo(562.3f, 89f, 552.79f, 85.37f, 547.87f, 85.62f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(427.18f, 64.82f)
			reflectiveCurveToRelative(-8.33f, -21.66f, 3.75f, -28.74f)
			lineToRelative(3.33f, 9.58f)
			reflectiveCurveToRelative(30.4f, -18.33f, 38.32f, -14.16f)
			lineToRelative(-7.5f, 10f)
			reflectiveCurveToRelative(35.4f, 0f, 37.9f, 13.74f)
			lineToRelative(-12.49f, 0.42f)
			reflectiveCurveToRelative(13.33f, 8.75f, 13.74f, 22.91f)
			lineToRelative(-26.66f, 1.67f)
			lineToRelative(12.91f, 7.5f)
			reflectiveCurveToRelative(-48.31f, 6.66f, -59.97f, -12.49f)
			close()
		}
		path(fill = SolidColor(Color(0xFF575A88))) {
			moveTo(346.85f, 89.35f)
			lineToRelative(19.59f, -15.67f)
			reflectiveCurveToRelative(-6.53f, 19.59f, 0f, 26.12f)
			lineToRelative(-24.82f, 5.22f)
			close()
		}
		path(fill = SolidColor(Color(0xFF575A88))) {
			moveTo(575.42f, 98.49f)
			lineToRelative(-16.98f, -13.06f)
			reflectiveCurveToRelative(2.61f, 16.98f, -2.61f, 20.9f)
			reflectiveCurveToRelative(14.37f, 5.22f, 14.37f, 5.22f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(179.63f, 580.42f)
			moveToRelative(-72f, 0f)
			arcToRelative(72f, 72f, 0f, isMoreThanHalf = true, isPositiveArc = true, 144f, 0f)
			arcToRelative(72f, 72f, 0f, isMoreThanHalf = true, isPositiveArc = true, -144f, 0f)
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(111.31f, 495.35f)
			curveToRelative(14.36f, 18.94f, 39.39f, 24.14f, 39.39f, 24.14f)
			reflectiveCurveToRelative(1.76f, -25.51f, -12.6f, -44.44f)
			reflectiveCurveToRelative(-39.39f, -24.14f, -39.39f, -24.14f)
			reflectiveCurveTo(96.95f, 476.41f, 111.31f, 495.35f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(117.91f, 487.36f)
			curveToRelative(22f, 8.98f, 33.49f, 31.82f, 33.49f, 31.82f)
			reflectiveCurveToRelative(-24.19f, 8.28f, -46.19f, -0.7f)
			reflectiveCurveTo(71.72f, 486.66f, 71.72f, 486.66f)
			reflectiveCurveTo(95.91f, 478.38f, 117.91f, 487.36f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(142.27f, 474.05f)
			curveToRelative(-2.79f, 23.6f, 11.64f, 44.7f, 11.64f, 44.7f)
			reflectiveCurveToRelative(18.95f, -17.16f, 21.74f, -40.76f)
			reflectiveCurveToRelative(-11.64f, -44.7f, -11.64f, -44.7f)
			reflectiveCurveTo(145.06f, 450.45f, 142.27f, 474.05f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(152.57f, 472.87f)
			curveToRelative(9.62f, 21.73f, 2.06f, 46.15f, 2.06f, 46.15f)
			reflectiveCurveToRelative(-23.17f, -10.81f, -32.79f, -32.53f)
			reflectiveCurveToRelative(-2.06f, -46.15f, -2.06f, -46.15f)
			reflectiveCurveTo(142.94f, 451.14f, 152.57f, 472.87f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(151.85f, 584.13f)
			moveToRelative(-37f, 0f)
			arcToRelative(37f, 37f, 0f, isMoreThanHalf = true, isPositiveArc = true, 74f, 0f)
			arcToRelative(37f, 37f, 0f, isMoreThanHalf = true, isPositiveArc = true, -74f, 0f)
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(137.82f, 595.88f)
			moveToRelative(-12f, 0f)
			arcToRelative(12f, 12f, 0f, isMoreThanHalf = true, isPositiveArc = true, 24f, 0f)
			arcToRelative(12f, 12f, 0f, isMoreThanHalf = true, isPositiveArc = true, -24f, 0f)
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(463f, 546.88f)
			moveToRelative(-72f, 0f)
			arcToRelative(72f, 72f, 0f, isMoreThanHalf = true, isPositiveArc = true, 144f, 0f)
			arcToRelative(72f, 72f, 0f, isMoreThanHalf = true, isPositiveArc = true, -144f, 0f)
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(431.56f, 442.39f)
			curveToRelative(6.21f, 22.94f, 27.46f, 37.15f, 27.46f, 37.15f)
			reflectiveCurveToRelative(11.19f, -22.99f, 4.99f, -45.93f)
			reflectiveCurveToRelative(-27.46f, -37.15f, -27.46f, -37.15f)
			reflectiveCurveTo(425.36f, 419.45f, 431.56f, 442.39f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(440.68f, 437.46f)
			curveToRelative(17.03f, 16.58f, 19.11f, 42.06f, 19.11f, 42.06f)
			reflectiveCurveToRelative(-25.53f, -1.39f, -42.56f, -17.97f)
			reflectiveCurveToRelative(-19.11f, -42.06f, -19.11f, -42.06f)
			reflectiveCurveTo(423.65f, 420.89f, 440.68f, 437.46f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(468.25f, 434.26f)
			curveToRelative(-11.44f, 20.83f, -5.97f, 45.81f, -5.97f, 45.81f)
			reflectiveCurveToRelative(24.01f, -8.8f, 35.44f, -29.63f)
			reflectiveCurveToRelative(5.97f, -45.81f, 5.97f, -45.81f)
			reflectiveCurveTo(479.69f, 413.43f, 468.25f, 434.26f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(478.24f, 437.03f)
			curveToRelative(0.77f, 23.75f, -15.4f, 43.55f, -15.4f, 43.55f)
			reflectiveCurveToRelative(-17.43f, -18.71f, -18.2f, -42.46f)
			reflectiveCurveToRelative(15.4f, -43.55f, 15.4f, -43.55f)
			reflectiveCurveTo(477.47f, 413.28f, 478.24f, 437.03f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(460f, 538.88f)
			moveToRelative(-37f, 0f)
			arcToRelative(37f, 37f, 0f, isMoreThanHalf = true, isPositiveArc = true, 74f, 0f)
			arcToRelative(37f, 37f, 0f, isMoreThanHalf = true, isPositiveArc = true, -74f, 0f)
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(460f, 517.88f)
			moveToRelative(-12f, 0f)
			arcToRelative(12f, 12f, 0f, isMoreThanHalf = true, isPositiveArc = true, 24f, 0f)
			arcToRelative(12f, 12f, 0f, isMoreThanHalf = true, isPositiveArc = true, -24f, 0f)
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(287f, 616.88f)
			horizontalLineToRelative(375f)
			verticalLineToRelative(2f)
			horizontalLineToRelative(-375f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(793f, 184.88f)
			arcToRelative(20.5f, 16f, 0f, isMoreThanHalf = true, isPositiveArc = false, 41f, 0f)
			arcToRelative(20.5f, 16f, 0f, isMoreThanHalf = true, isPositiveArc = false, -41f, 0f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(247.14f, 522.91f)
			lineToRelative(32.79f, -26.26f)
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(249.24f, 543.15f)
			quadTo(264.1f, 531.14f, 280.01f, 520.5f)
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(271.11f, 541.02f)
			arcToRelative(113.67f, 113.67f, 0f, isMoreThanHalf = false, isPositiveArc = false, 31.25f, -21.97f)
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(189f, 630.88f)
			moveToRelative(-18f, 0f)
			arcToRelative(18f, 18f, 0f, isMoreThanHalf = true, isPositiveArc = true, 36f, 0f)
			arcToRelative(18f, 18f, 0f, isMoreThanHalf = true, isPositiveArc = true, -36f, 0f)
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(0f, 720.88f)
			arcToRelative(99f, 11.93f, 0f, isMoreThanHalf = true, isPositiveArc = false, 198f, 0f)
			arcToRelative(99f, 11.93f, 0f, isMoreThanHalf = true, isPositiveArc = false, -198f, 0f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(632f, 617.88f)
			arcToRelative(22f, 22f, 0f, isMoreThanHalf = true, isPositiveArc = true, 22f, -22f)
			arcTo(22.02f, 22.02f, 0f, isMoreThanHalf = false, isPositiveArc = true, 632f, 617.88f)
			close()
			moveTo(632f, 575.88f)
			arcToRelative(20f, 20f, 0f, isMoreThanHalf = true, isPositiveArc = false, 20f, 20f)
			arcTo(20.02f, 20.02f, 0f, isMoreThanHalf = false, isPositiveArc = false, 632f, 575.88f)
			close()
		}
	}.build()

	return _OpponentListEmptyState!!
}

@Suppress("ObjectPropertyName")
private var _OpponentListEmptyState: ImageVector? = null
