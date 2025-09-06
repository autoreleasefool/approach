package ca.josephroque.bowlingcompanion.feature.gearlist.ui.images

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
fun imageGearListEmptyState(accentColor: Color = MaterialTheme.colorScheme.primary): ImageVector {
	LaunchedEffect(isSystemInDarkTheme()) {
		// Clear cached image when theme changes
		_GearListEmptyState = null
	}
	if (_GearListEmptyState != null) {
		return _GearListEmptyState!!
	}
	_GearListEmptyState = ImageVector.Builder(
		name = "GearListEmptyState",
		defaultWidth = 638.67.dp,
		defaultHeight = 460.39.dp,
		viewportWidth = 638.67f,
		viewportHeight = 460.39f
	).apply {
		path(fill = SolidColor(Color.LightGray)) {
			moveTo(314.9f, 458.44f)
			lineTo(290.37f, 458.44f)
			lineTo(290.37f, 269.62f)
			horizontalLineToRelative(19.95f)
			arcToRelative(3.66f, 3.66f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3.66f, 3.64f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(292.66f, 459.44f)
			lineTo(29.2f, 459.44f)
			lineTo(29.2f, 324.97f)
			lineTo(292.66f, 324.97f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(188.82f, 458.94f)
			lineTo(29.7f, 458.94f)
			lineTo(29.7f, 325.47f)
			lineTo(292.16f, 325.47f)
			verticalLineToRelative(30.13f)
			arcTo(103.46f, 103.46f, 0f, isMoreThanHalf = false, isPositiveArc = true, 188.82f, 458.94f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(310.91f, 274.98f)
			horizontalLineToRelative(0f)
			arcToRelative(3.66f, 3.66f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.31f, 0.79f)
			lineToRelative(-9.08f, 35.02f)
			lineToRelative(-9.85f, 18.66f)
			lineTo(30.2f, 329.45f)
			lineTo(30.2f, 324.81f)
			lineToRelative(14.89f, -14.01f)
			lineToRelative(17.37f, -33.22f)
			arcToRelative(14.82f, 14.82f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.98f, -3.93f)
			lineToRelative(0f, 0f)
			arcToRelative(14.82f, 14.82f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10.16f, -4.03f)
			lineTo(307.68f, 269.62f)
			arcTo(3.66f, 3.66f, 0f, isMoreThanHalf = false, isPositiveArc = true, 310.91f, 274.98f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(292.66f, 459.44f)
			lineTo(29.2f, 459.44f)
			lineTo(29.2f, 324.97f)
			lineTo(292.66f, 324.97f)
			close()
			moveTo(31.2f, 457.44f)
			lineTo(290.66f, 457.44f)
			lineTo(290.66f, 326.97f)
			lineTo(31.2f, 326.97f)
			close()
		}
		path(fill = SolidColor(Color.LightGray)) {
			moveTo(306.37f, 292.9f)
			lineToRelative(-4.85f, 17.89f)
			lineToRelative(-256.43f, 0f)
			lineToRelative(8.29f, -14.89f)
			lineToRelative(253f, -3f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(38.91f, 395.44f)
			horizontalLineToRelative(42f)
			verticalLineToRelative(2.32f)
			horizontalLineToRelative(-42f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(38.91f, 403.57f)
			horizontalLineToRelative(42f)
			verticalLineToRelative(2.32f)
			horizontalLineToRelative(-42f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(38.91f, 411.7f)
			horizontalLineToRelative(42f)
			verticalLineToRelative(2.32f)
			horizontalLineToRelative(-42f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(55.18f, 354.44f)
			moveToRelative(-15.11f, 0f)
			arcToRelative(15.11f, 15.11f, 0f, isMoreThanHalf = true, isPositiveArc = true, 30.21f, 0f)
			arcToRelative(15.11f, 15.11f, 0f, isMoreThanHalf = true, isPositiveArc = true, -30.21f, 0f)
		}
		path(fill = SolidColor(Color.LightGray)) {
			moveTo(220.76f, 300.64f)
			horizontalLineToRelative(-12.83f)
			lineTo(207.93f, 201.9f)
			horizontalLineToRelative(10.43f)
			arcToRelative(1.91f, 1.91f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.91f, 1.9f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(209.12f, 301.17f)
			lineTo(71.35f, 301.17f)
			verticalLineToRelative(-70.32f)
			lineTo(209.12f, 230.84f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(154.82f, 300.9f)
			lineTo(71.61f, 300.9f)
			verticalLineToRelative(-69.8f)
			lineTo(208.86f, 231.1f)
			verticalLineToRelative(15.76f)
			arcTo(54.11f, 54.11f, 0f, isMoreThanHalf = false, isPositiveArc = true, 154.82f, 300.9f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(218.67f, 204.7f)
			horizontalLineToRelative(0f)
			arcToRelative(1.91f, 1.91f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.16f, 0.41f)
			lineToRelative(-4.75f, 18.31f)
			lineToRelative(-5.15f, 9.76f)
			lineTo(71.87f, 233.19f)
			verticalLineToRelative(-2.43f)
			lineToRelative(7.78f, -7.33f)
			lineToRelative(9.08f, -17.37f)
			arcToRelative(7.75f, 7.75f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.56f, -2.05f)
			verticalLineToRelative(0f)
			arcToRelative(7.75f, 7.75f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5.31f, -2.11f)
			lineTo(216.98f, 201.9f)
			arcTo(1.91f, 1.91f, 0f, isMoreThanHalf = false, isPositiveArc = true, 218.67f, 204.7f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(209.12f, 301.17f)
			lineTo(71.35f, 301.17f)
			verticalLineToRelative(-70.32f)
			lineTo(209.12f, 230.84f)
			close()
			moveTo(72.39f, 300.12f)
			lineTo(208.08f, 300.12f)
			lineTo(208.08f, 231.89f)
			lineTo(72.39f, 231.89f)
			close()
		}
		path(fill = SolidColor(Color.LightGray)) {
			moveTo(216.29f, 214.07f)
			lineToRelative(-2.54f, 9.36f)
			lineToRelative(-134.1f, 0f)
			lineToRelative(4.33f, -7.79f)
			lineToRelative(132.31f, -1.57f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(76.43f, 267.69f)
			horizontalLineToRelative(21.96f)
			verticalLineToRelative(1.22f)
			horizontalLineToRelative(-21.96f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(76.43f, 271.95f)
			horizontalLineToRelative(21.96f)
			verticalLineToRelative(1.22f)
			horizontalLineToRelative(-21.96f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(76.43f, 276.2f)
			horizontalLineToRelative(21.96f)
			verticalLineToRelative(1.22f)
			horizontalLineToRelative(-21.96f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(84.93f, 246.25f)
			moveToRelative(-7.9f, 0f)
			arcToRelative(7.9f, 7.9f, 0f, isMoreThanHalf = true, isPositiveArc = true, 15.8f, 0f)
			arcToRelative(7.9f, 7.9f, 0f, isMoreThanHalf = true, isPositiveArc = true, -15.8f, 0f)
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(598.88f, 459.38f)
			lineToRelative(-0.08f, -0.43f)
			arcToRelative(50.1f, 50.1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10.6f, -40.77f)
			arcToRelative(48.61f, 48.61f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8.93f, -8.3f)
			lineToRelative(0.32f, -0.23f)
			lineToRelative(0.14f, 0.36f)
			curveToRelative(1.65f, 4.35f, 3.73f, 8.7f, 4.68f, 10.63f)
			lineToRelative(0.64f, -14.38f)
			lineToRelative(0.37f, -0.19f)
			arcToRelative(9.5f, 9.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10.59f, 1.09f)
			arcToRelative(9.67f, 9.67f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3.19f, 10.35f)
			curveToRelative(-0.5f, 1.69f, -0.97f, 3.44f, -1.42f, 5.14f)
			curveToRelative(-1.56f, 5.82f, -3.17f, 11.85f, -6.3f, 16.92f)
			arcToRelative(30.29f, 30.29f, 0f, isMoreThanHalf = false, isPositiveArc = true, -22.04f, 13.92f)
			close()
		}
		path(fill = SolidColor(Color.LightGray)) {
			moveTo(638.67f, 459.2f)
			arcToRelative(1.19f, 1.19f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.19f, 1.19f)
			lineTo(1.19f, 460.39f)
			arcToRelative(1.19f, 1.19f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, -2.38f)
			horizontalLineToRelative(636.29f)
			arcTo(1.19f, 1.19f, 0f, isMoreThanHalf = false, isPositiveArc = true, 638.67f, 459.2f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB8B8))) {
			moveTo(494.34f, 154.72f)
			lineToRelative(-18.21f, -2.61f)
			curveToRelative(-6.16f, 10.3f, -8.35f, 37.71f, -8.35f, 37.71f)
			lineToRelative(-17.82f, 42.36f)
			arcToRelative(8.12f, 8.12f, 0f, isMoreThanHalf = true, isPositiveArc = false, 12.66f, 6.1f)
			lineToRelative(29.27f, -49.31f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(491.45f, 104.16f)
			lineToRelative(0f, 0f)
			arcTo(19.41f, 19.41f, 0f, isMoreThanHalf = false, isPositiveArc = true, 506.78f, 129.8f)
			lineToRelative(-3.87f, 10.95f)
			reflectiveCurveToRelative(-2.1f, 32.98f, -10.13f, 42.3f)
			lineToRelative(-24.62f, -2.23f)
			reflectiveCurveToRelative(4.5f, -16.64f, 1.05f, -24.69f)
			curveToRelative(-2.12f, -4.93f, -2f, -21.96f, 0.09f, -35.82f)
			arcTo(19.36f, 19.36f, 0f, isMoreThanHalf = false, isPositiveArc = true, 491.45f, 104.16f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(551.64f, 446.42f)
			lineToRelative(-10.97f, -0f)
			lineToRelative(-5.22f, -42.32f)
			lineToRelative(16.19f, 0f)
			lineToRelative(-0f, 42.32f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(552.02f, 457.86f)
			lineToRelative(-33.74f, -0f)
			verticalLineToRelative(-0.43f)
			arcToRelative(13.13f, 13.13f, 0f, isMoreThanHalf = false, isPositiveArc = true, 13.13f, -13.13f)
			horizontalLineToRelative(0f)
			lineToRelative(6.16f, -4.68f)
			lineToRelative(11.5f, 4.68f)
			lineToRelative(2.94f, 0f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(503.94f, 393.97f)
			lineToRelative(-10.13f, 4.2f)
			lineToRelative(-21.03f, -37.09f)
			lineToRelative(14.96f, -6.2f)
			lineToRelative(16.21f, 39.09f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(508.68f, 404.4f)
			lineToRelative(-31.16f, 12.92f)
			lineToRelative(-0.16f, -0.39f)
			arcToRelative(13.13f, 13.13f, 0f, isMoreThanHalf = false, isPositiveArc = true, 7.1f, -17.16f)
			lineToRelative(0f, -0f)
			lineToRelative(3.9f, -6.68f)
			lineToRelative(12.41f, -0.09f)
			lineToRelative(2.72f, -1.13f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(564.18f, 205.69f)
			lineToRelative(-77.86f, 8.13f)
			lineToRelative(-10.41f, 35.08f)
			lineTo(439.84f, 306.87f)
			lineToRelative(39.82f, 73.17f)
			lineToRelative(14.02f, -12.59f)
			lineTo(480.23f, 308.37f)
			lineToRelative(40.18f, -43.38f)
			lineToRelative(7.62f, -3.16f)
			horizontalLineToRelative(0f)
			lineToRelative(6.05f, 160.97f)
			lineToRelative(22.37f, 0.37f)
			lineToRelative(9.79f, -177.62f)
			curveTo(570.94f, 229.85f, 569.52f, 216.9f, 564.18f, 205.69f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(535.63f, 94.65f)
			lineToRelative(-12.78f, -14.39f)
			lineToRelative(-20.19f, 2.24f)
			lineTo(489.73f, 100.59f)
			lineToRelative(0f, 0f)
			arcToRelative(27.6f, 27.6f, 0f, isMoreThanHalf = false, isPositiveArc = false, -21.01f, 31.62f)
			curveToRelative(5.02f, 28.89f, 22.07f, 56.91f, 16.44f, 71.16f)
			curveToRelative(-8.55f, 21.64f, -9.07f, 30.46f, 11.39f, 27.43f)
			reflectiveCurveToRelative(88.99f, -10.03f, 75.7f, -23.31f)
			reflectiveCurveToRelative(-16.21f, -35.49f, -16.21f, -35.49f)
			lineToRelative(-0.53f, -50.8f)
			arcToRelative(22.58f, 22.58f, 0f, isMoreThanHalf = false, isPositiveArc = false, -19.88f, -26.55f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB8B8))) {
			moveTo(510.66f, 54.16f)
			moveToRelative(-22.42f, 0f)
			arcToRelative(22.42f, 22.42f, 0f, isMoreThanHalf = true, isPositiveArc = true, 44.83f, 0f)
			arcToRelative(22.42f, 22.42f, 0f, isMoreThanHalf = true, isPositiveArc = true, -44.83f, 0f)
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(538.28f, 52.83f)
			arcToRelative(38.98f, 38.98f, 0f, isMoreThanHalf = false, isPositiveArc = true, -5.33f, 12.77f)
			arcToRelative(8.69f, 8.69f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2.91f, 3.3f)
			arcToRelative(3.18f, 3.18f, 0f, isMoreThanHalf = false, isPositiveArc = true, -4.04f, -0.5f)
			lineToRelative(-0.55f, -0.44f)
			arcToRelative(11.57f, 11.57f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2.83f, -2.45f)
			arcToRelative(3.94f, 3.94f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.76f, -3.52f)
			arcToRelative(2.43f, 2.43f, 0f, isMoreThanHalf = false, isPositiveArc = false, -2.97f, -1.59f)
			curveToRelative(-1.31f, 0.51f, -2.11f, 2.41f, -3.48f, 2.12f)
			curveToRelative(-1.11f, -0.24f, -1.34f, -1.71f, -1.34f, -2.86f)
			curveToRelative(0.04f, -6.02f, -2.87f, -14.57f, -5.12f, -14.31f)
			arcToRelative(18.16f, 18.16f, 0f, isMoreThanHalf = false, isPositiveArc = true, -6.71f, -0.9f)
			arcToRelative(17.59f, 17.59f, 0f, isMoreThanHalf = false, isPositiveArc = false, -6.72f, -0.86f)
			curveToRelative(-0.16f, 0.02f, -0.32f, 0.05f, -0.49f, 0.08f)
			arcToRelative(14.89f, 14.89f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.49f, -3.26f)
			arcToRelative(17.42f, 17.42f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.18f, 3.7f)
			arcToRelative(34.56f, 34.56f, 0f, isMoreThanHalf = false, isPositiveArc = true, -5.59f, 1.77f)
			curveToRelative(-2.08f, 0.29f, -6.65f, 6.51f, -6.87f, 5.88f)
			arcToRelative(14.89f, 14.89f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.49f, -3.26f)
			arcToRelative(17.42f, 17.42f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.18f, 3.7f)
			curveToRelative(-0.01f, 0.05f, -0.01f, 0.09f, -0.02f, 0.14f)
			curveToRelative(-1.01f, -1.34f, -1.63f, -2.98f, -1.28f, -1.73f)
			curveToRelative(-3.38f, -7.65f, -2.07f, -13.14f, 3.25f, -19.59f)
			arcToRelative(10.23f, 10.23f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3.99f, -3.2f)
			arcToRelative(5.03f, 5.03f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4.91f, 0.48f)
			arcToRelative(16.51f, 16.51f, 0f, isMoreThanHalf = false, isPositiveArc = true, 16.38f, -3.49f)
			curveToRelative(5.54f, 1.93f, 9.81f, 3.06f, 10.66f, 8.88f)
			curveToRelative(4.58f, -0.74f, 9.3f, 1.87f, 11.82f, 5.76f)
			curveTo(538.6f, 43.33f, 539.12f, 48.28f, 538.28f, 52.83f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(520.1f, 239.01f)
			horizontalLineToRelative(-7.44f)
			lineTo(512.66f, 181.75f)
			lineTo(518.71f, 181.75f)
			arcToRelative(1.11f, 1.11f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.11f, 1.1f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(513.36f, 239.31f)
			lineTo(433.47f, 239.31f)
			lineTo(433.47f, 198.53f)
			lineTo(513.36f, 198.53f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(481.87f, 239.16f)
			lineTo(433.62f, 239.16f)
			lineTo(433.62f, 198.69f)
			horizontalLineToRelative(79.59f)
			verticalLineToRelative(9.14f)
			arcTo(31.37f, 31.37f, 0f, isMoreThanHalf = false, isPositiveArc = true, 481.87f, 239.16f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(518.89f, 183.38f)
			horizontalLineToRelative(0f)
			arcToRelative(1.11f, 1.11f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.09f, 0.24f)
			lineToRelative(-2.75f, 10.62f)
			lineToRelative(-2.99f, 5.66f)
			horizontalLineToRelative(-79.29f)
			lineTo(433.77f, 198.49f)
			lineToRelative(4.51f, -4.25f)
			lineToRelative(5.27f, -10.07f)
			arcToRelative(4.5f, 4.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.9f, -1.19f)
			horizontalLineToRelative(0f)
			arcToRelative(4.5f, 4.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3.08f, -1.22f)
			horizontalLineToRelative(70.38f)
			arcTo(1.11f, 1.11f, 0f, isMoreThanHalf = false, isPositiveArc = true, 518.89f, 183.38f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(513.36f, 239.31f)
			lineTo(433.47f, 239.31f)
			lineTo(433.47f, 198.53f)
			lineTo(513.36f, 198.53f)
			close()
			moveTo(434.07f, 238.71f)
			horizontalLineToRelative(78.68f)
			lineTo(512.75f, 199.14f)
			lineTo(434.07f, 199.14f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(517.52f, 188.81f)
			lineToRelative(-1.47f, 5.43f)
			lineToRelative(-77.76f, 0f)
			lineToRelative(2.51f, -4.52f)
			lineToRelative(76.72f, -0.91f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(436.41f, 219.9f)
			horizontalLineToRelative(12.74f)
			verticalLineToRelative(0.7f)
			horizontalLineToRelative(-12.74f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(436.41f, 222.37f)
			horizontalLineToRelative(12.74f)
			verticalLineToRelative(0.7f)
			horizontalLineToRelative(-12.74f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(436.41f, 224.84f)
			horizontalLineToRelative(12.74f)
			verticalLineToRelative(0.7f)
			horizontalLineToRelative(-12.74f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(441.34f, 207.47f)
			moveToRelative(-4.58f, 0f)
			arcToRelative(4.58f, 4.58f, 0f, isMoreThanHalf = true, isPositiveArc = true, 9.16f, 0f)
			arcToRelative(4.58f, 4.58f, 0f, isMoreThanHalf = true, isPositiveArc = true, -9.16f, 0f)
		}
		path(fill = SolidColor(Color(0xFFFFB8B8))) {
			moveTo(551.18f, 149.49f)
			lineToRelative(-18.21f, -2.61f)
			curveToRelative(-6.16f, 10.3f, -8.35f, 37.71f, -8.35f, 37.71f)
			lineToRelative(-17.82f, 42.36f)
			arcToRelative(8.12f, 8.12f, 0f, isMoreThanHalf = true, isPositiveArc = false, 12.66f, 6.1f)
			lineToRelative(29.27f, -49.31f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(541.31f, 98.93f)
			lineToRelative(0f, 0f)
			arcToRelative(19.41f, 19.41f, 0f, isMoreThanHalf = false, isPositiveArc = true, 15.34f, 25.64f)
			lineToRelative(-3.87f, 10.95f)
			reflectiveCurveToRelative(4.87f, 32.98f, -3.15f, 42.3f)
			lineToRelative(-24.62f, -2.23f)
			reflectiveCurveToRelative(4.5f, -16.64f, 1.05f, -24.69f)
			curveToRelative(-2.12f, -4.93f, -2f, -21.96f, 0.09f, -35.82f)
			curveTo(527.73f, 104.5f, 530.74f, 97.29f, 541.31f, 98.93f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(386.66f, 44.35f)
			arcTo(37.49f, 37.49f, 0f, isMoreThanHalf = false, isPositiveArc = false, 369.84f, 94.63f)
			curveToRelative(9.24f, 18.53f, 42.64f, 50.13f, 69.28f, 54.91f)
			curveToRelative(-11.46f, -34.8f, 4.47f, -70.17f, -2.18f, -88.37f)
			curveTo(429.84f, 41.72f, 405.19f, 35.11f, 386.66f, 44.35f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(399.15f, 76.19f)
			moveToRelative(-12.23f, 0f)
			arcToRelative(12.23f, 12.23f, 0f, isMoreThanHalf = true, isPositiveArc = true, 24.46f, 0f)
			arcToRelative(12.23f, 12.23f, 0f, isMoreThanHalf = true, isPositiveArc = true, -24.46f, 0f)
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(438.89f, 161.16f)
			moveToRelative(-8.01f, 0f)
			arcToRelative(8.01f, 8.01f, 0f, isMoreThanHalf = true, isPositiveArc = true, 16.02f, 0f)
			arcToRelative(8.01f, 8.01f, 0f, isMoreThanHalf = true, isPositiveArc = true, -16.02f, 0f)
		}
		path(
			fill = SolidColor(Color(0xFF231F20)),
			fillAlpha = 0.2f,
			strokeAlpha = 0.2f
		) {
			moveTo(402.88f, 63.34f)
			arcToRelative(12.23f, 12.23f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0.83f, 24.44f)
			arcToRelative(12.23f, 12.23f, 0f, isMoreThanHalf = false, isPositiveArc = false, -4.69f, -23.68f)
			arcTo(12.23f, 12.23f, 0f, isMoreThanHalf = false, isPositiveArc = true, 402.88f, 63.34f)
			close()
		}
		path(
			fill = SolidColor(Color(0xFF231F20)),
			fillAlpha = 0.2f,
			strokeAlpha = 0.2f
		) {
			moveTo(437.01f, 61.18f)
			curveToRelative(6.65f, 18.2f, -9.28f, 53.57f, 2.18f, 88.37f)
			arcToRelative(51.86f, 51.86f, 0f, isMoreThanHalf = false, isPositiveArc = true, -7.41f, -1.94f)
			curveToRelative(-9.34f, -33.64f, 5.32f, -67.26f, -1.11f, -84.84f)
			curveTo(424.91f, 47.03f, 407.67f, 39.7f, 391.49f, 42.37f)
			curveTo(409.25f, 36.31f, 430.49f, 43.35f, 437.01f, 61.18f)
			close()
		}
		path(fill = SolidColor(Color(0xFFD1D3D4))) {
			moveTo(61.66f, 230.04f)
			arcToRelative(34.74f, 34.74f, 0f, isMoreThanHalf = false, isPositiveArc = false, -35.56f, 33.9f)
			curveToRelative(-0.46f, 19.18f, 13.2f, 59.54f, 32.95f, 74.99f)
			curveToRelative(5.69f, -33.47f, 34.07f, -55.53f, 36.51f, -73.32f)
			curveTo(98.16f, 246.6f, 80.84f, 230.5f, 61.66f, 230.04f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(58.09f, 261.53f)
			moveToRelative(-11.33f, 0f)
			arcToRelative(11.33f, 11.33f, 0f, isMoreThanHalf = true, isPositiveArc = true, 22.66f, 0f)
			arcToRelative(11.33f, 11.33f, 0f, isMoreThanHalf = true, isPositiveArc = true, -22.66f, 0f)
		}
		path(fill = SolidColor(Color(0xFFD1D3D4))) {
			moveTo(53.82f, 348.34f)
			moveToRelative(-7.42f, 0f)
			arcToRelative(7.42f, 7.42f, 0f, isMoreThanHalf = true, isPositiveArc = true, 14.84f, 0f)
			arcToRelative(7.42f, 7.42f, 0f, isMoreThanHalf = true, isPositiveArc = true, -14.84f, 0f)
		}
		path(
			fill = SolidColor(Color(0xFF231F20)),
			fillAlpha = 0.2f,
			strokeAlpha = 0.2f
		) {
			moveTo(66.71f, 252.62f)
			arcToRelative(11.33f, 11.33f, 0f, isMoreThanHalf = true, isPositiveArc = true, -9.91f, 20.38f)
			arcToRelative(11.33f, 11.33f, 0f, isMoreThanHalf = false, isPositiveArc = false, 6.42f, -21.42f)
			arcTo(11.33f, 11.33f, 0f, isMoreThanHalf = false, isPositiveArc = true, 66.71f, 252.62f)
			close()
		}
		path(
			fill = SolidColor(Color(0xFF231F20)),
			fillAlpha = 0.2f,
			strokeAlpha = 0.2f
		) {
			moveTo(95.6f, 265.64f)
			curveToRelative(-2.44f, 17.79f, -30.82f, 39.86f, -36.51f, 73.32f)
			arcToRelative(48.06f, 48.06f, 0f, isMoreThanHalf = false, isPositiveArc = true, -5.23f, -4.8f)
			curveToRelative(6.93f, -31.6f, 33.5f, -52.78f, 35.86f, -69.97f)
			curveToRelative(2.11f, -15.38f, -8.83f, -28.86f, -23.25f, -33.68f)
			curveTo(83.65f, 233.25f, 97.99f, 248.22f, 95.6f, 265.64f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(193.46f, 82.41f)
			arcToRelative(9.96f, 9.96f, 0f, isMoreThanHalf = false, isPositiveArc = false, -9.3f, 10.32f)
			arcToRelative(9.55f, 9.55f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.22f, 1.56f)
			lineTo(154.98f, 112.82f)
			lineToRelative(16.25f, 7.19f)
			lineToRelative(25.42f, -18.69f)
			arcToRelative(9.93f, 9.93f, 0f, isMoreThanHalf = false, isPositiveArc = false, 7.02f, -9.88f)
			arcToRelative(9.5f, 9.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, -9.95f, -9.03f)
			quadTo(193.59f, 82.4f, 193.46f, 82.41f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(165.11f, 104.4f)
			lineToRelative(7.29f, 15.03f)
			lineToRelative(-28.81f, 18.54f)
			arcToRelative(14.33f, 14.33f, 0f, isMoreThanHalf = false, isPositiveArc = true, -13.93f, 0.93f)
			horizontalLineToRelative(0f)
			arcToRelative(13.73f, 13.73f, 0f, isMoreThanHalf = false, isPositiveArc = true, -7.62f, -14.65f)
			lineToRelative(9.35f, -53.37f)
			arcToRelative(14.8f, 14.8f, 0f, isMoreThanHalf = false, isPositiveArc = true, 16.25f, -12.26f)
			horizontalLineToRelative(0f)
			arcToRelative(14.04f, 14.04f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12.43f, 15.48f)
			quadToRelative(-0.02f, 0.22f, -0.05f, 0.43f)
			arcToRelative(14.4f, 14.4f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.29f, 1.52f)
			lineToRelative(-10.2f, 37.95f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB8B8))) {
			moveTo(125.96f, 127.7f)
			horizontalLineToRelative(41.2f)
			verticalLineToRelative(41.2f)
			horizontalLineToRelative(-41.2f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(264.86f, 249.04f)
			lineToRelative(15.73f, -5.29f)
			lineToRelative(8.76f, 42.83f)
			lineToRelative(-10.66f, 3.59f)
			lineToRelative(-13.83f, -41.12f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(238.2f, 245.74f)
			lineToRelative(15.61f, -5.66f)
			lineToRelative(9.76f, 42.61f)
			lineToRelative(-10.58f, 3.83f)
			lineToRelative(-14.79f, -40.79f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(127.44f, 179.66f)
			curveToRelative(-8.26f, 55.05f, 101.7f, 22.5f, 101.7f, 22.5f)
			lineToRelative(34.48f, 47.56f)
			lineToRelative(20.18f, -3.67f)
			reflectiveCurveToRelative(-9.08f, -28.55f, -25.3f, -66.83f)
			curveTo(251.3f, 162.23f, 169.51f, 166.39f, 169.51f, 166.39f)
			lineTo(167.81f, 153.06f)
			lineToRelative(-46.79f, -0.92f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(206.87f, 205.86f)
			lineToRelative(25.43f, -15.84f)
			curveToRelative(18.61f, 37.17f, 24.1f, 53.27f, 24.1f, 53.27f)
			lineToRelative(-17.6f, 4.95f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(150.98f, 60.76f)
			lineToRelative(-8.29f, -10.66f)
			lineToRelative(-11.83f, -0.51f)
			lineToRelative(-1.18f, 10.03f)
			lineToRelative(-5.86f, 2.7f)
			arcToRelative(157.19f, 157.19f, 0f, isMoreThanHalf = false, isPositiveArc = false, -2.65f, 68.1f)
			lineToRelative(0f, 0f)
			horizontalLineToRelative(0f)
			arcToRelative(6.06f, 6.06f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.72f, 5.89f)
			lineToRelative(-0.38f, 1.51f)
			lineToRelative(0.04f, 0.06f)
			arcToRelative(8.87f, 8.87f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.18f, 6.62f)
			verticalLineToRelative(0f)
			lineToRelative(-2.8f, 4.97f)
			arcToRelative(14.12f, 14.12f, 0f, isMoreThanHalf = false, isPositiveArc = false, 7.52f, 20.2f)
			curveToRelative(17.21f, 6.24f, 33.33f, 8.56f, 45.27f, -8.94f)
			lineToRelative(3.75f, -60.99f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB8B8))) {
			moveTo(142.29f, 31.06f)
			moveToRelative(-21.87f, 0f)
			arcToRelative(21.87f, 21.87f, 0f, isMoreThanHalf = true, isPositiveArc = true, 43.74f, 0f)
			arcToRelative(21.87f, 21.87f, 0f, isMoreThanHalf = true, isPositiveArc = true, -43.74f, 0f)
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(131.31f, 4.69f)
			curveToRelative(-9.26f, 0.18f, -16.61f, 8.77f, -16.41f, 19.19f)
			curveToRelative(-6.85f, 7.15f, -8.51f, 42.32f, -2.79f, 51.68f)
			curveToRelative(10.47f, 1.22f, 16.85f, -24.7f, 26.03f, -20.95f)
			lineToRelative(1.21f, -5.66f)
			lineToRelative(2.74f, 7.36f)
			quadToRelative(3.5f, 1.61f, 6.95f, 3.54f)
			curveToRelative(1.65f, -7.58f, 1.24f, -14.95f, -3.2f, -21.36f)
			curveToRelative(-3.95f, -5.7f, 5.66f, -12.47f, 12.52f, -11.51f)
			horizontalLineToRelative(0f)
			lineToRelative(-0.1f, -5.24f)
			lineToRelative(1.72f, 5.2f)
			curveToRelative(2.68f, 1.97f, 5.64f, 1.25f, 8.67f, -0.17f)
			verticalLineToRelative(0f)
			curveTo(173.6f, 1.2f, 135.48f, -5.8f, 131.31f, 4.69f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(273.85f, 297.4f)
			lineToRelative(7.01f, -0.92f)
			lineToRelative(0.39f, -6.78f)
			lineToRelative(4.07f, 6.2f)
			lineToRelative(18.59f, -2.43f)
			arcToRelative(4.21f, 4.21f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.38f, -7.93f)
			lineToRelative(-16.19f, -8.31f)
			lineToRelative(-0.87f, -6.69f)
			lineToRelative(-15.49f, 2.97f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(255.34f, 297.46f)
			lineToRelative(6.36f, -3.09f)
			lineToRelative(-1.78f, -6.56f)
			lineToRelative(5.82f, 4.59f)
			lineToRelative(16.87f, -8.19f)
			arcToRelative(4.21f, 4.21f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.2f, -7.96f)
			lineToRelative(-17.99f, -2.76f)
			lineToRelative(-2.95f, -6.07f)
			lineToRelative(-13.76f, 7.72f)
			close()
		}
		path(fill = SolidColor(Color.LightGray)) {
			moveTo(159.33f, 151.62f)
			horizontalLineToRelative(7.8f)
			lineTo(167.13f, 91.6f)
			horizontalLineToRelative(-6.34f)
			arcToRelative(1.16f, 1.16f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.16f, 1.16f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(166.4f, 109.19f)
			horizontalLineToRelative(83.74f)
			verticalLineToRelative(42.74f)
			lineTo(166.4f, 151.93f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(166.56f, 118.93f)
			verticalLineToRelative(-9.58f)
			lineTo(249.99f, 109.35f)
			verticalLineToRelative(42.42f)
			lineTo(199.41f, 151.77f)
			arcTo(32.89f, 32.89f, 0f, isMoreThanHalf = false, isPositiveArc = true, 166.56f, 118.93f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(160.6f, 93.3f)
			horizontalLineToRelative(0f)
			arcToRelative(1.16f, 1.16f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.1f, 0.25f)
			lineToRelative(2.89f, 11.13f)
			lineToRelative(3.13f, 5.93f)
			horizontalLineToRelative(83.11f)
			lineTo(249.83f, 109.14f)
			lineToRelative(-4.73f, -4.45f)
			lineToRelative(-5.52f, -10.56f)
			arcToRelative(4.71f, 4.71f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.95f, -1.25f)
			horizontalLineToRelative(0f)
			arcToRelative(4.71f, 4.71f, 0f, isMoreThanHalf = false, isPositiveArc = false, -3.23f, -1.28f)
			lineTo(161.63f, 91.6f)
			arcTo(1.16f, 1.16f, 0f, isMoreThanHalf = false, isPositiveArc = false, 160.6f, 93.3f)
			close()
		}
		path(fill = SolidColor(Color(0xFFE6E6E6))) {
			moveTo(166.4f, 109.19f)
			horizontalLineToRelative(83.74f)
			verticalLineToRelative(42.74f)
			lineTo(166.4f, 151.93f)
			close()
			moveTo(249.51f, 151.3f)
			lineTo(167.04f, 151.3f)
			lineTo(167.04f, 109.83f)
			horizontalLineToRelative(82.47f)
			close()
		}
		path(fill = SolidColor(Color.LightGray)) {
			moveTo(162.04f, 99f)
			lineToRelative(1.54f, 5.69f)
			lineToRelative(81.51f, 0f)
			lineToRelative(-2.63f, -4.73f)
			lineToRelative(-80.42f, -0.95f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(233.71f, 131.59f)
			horizontalLineToRelative(13.35f)
			verticalLineToRelative(0.74f)
			horizontalLineToRelative(-13.35f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(233.71f, 134.17f)
			horizontalLineToRelative(13.35f)
			verticalLineToRelative(0.74f)
			horizontalLineToRelative(-13.35f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(233.71f, 136.76f)
			horizontalLineToRelative(13.35f)
			verticalLineToRelative(0.74f)
			horizontalLineToRelative(-13.35f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(241.89f, 118.56f)
			moveToRelative(-4.8f, 0f)
			arcToRelative(4.8f, 4.8f, 0f, isMoreThanHalf = true, isPositiveArc = true, 9.6f, 0f)
			arcToRelative(4.8f, 4.8f, 0f, isMoreThanHalf = true, isPositiveArc = true, -9.6f, 0f)
		}
		path(fill = SolidColor(Color(0xFFD1D3D4))) {
			moveTo(203.15f, 46.01f)
			arcTo(21.29f, 21.29f, 0f, isMoreThanHalf = false, isPositiveArc = true, 224.94f, 66.78f)
			curveToRelative(0.28f, 11.75f, -8.09f, 36.48f, -20.19f, 45.95f)
			curveToRelative(-3.49f, -20.51f, -20.88f, -34.03f, -22.37f, -44.93f)
			curveTo(180.78f, 56.15f, 191.4f, 46.29f, 203.15f, 46.01f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(205.34f, 65.3f)
			moveToRelative(-6.94f, 0f)
			arcToRelative(6.94f, 6.94f, 0f, isMoreThanHalf = true, isPositiveArc = true, 13.88f, 0f)
			arcToRelative(6.94f, 6.94f, 0f, isMoreThanHalf = true, isPositiveArc = true, -13.88f, 0f)
		}
		path(fill = SolidColor(Color(0xFFD1D3D4))) {
			moveTo(207.95f, 118.49f)
			moveToRelative(-4.55f, 0f)
			arcToRelative(4.55f, 4.55f, 0f, isMoreThanHalf = true, isPositiveArc = true, 9.09f, 0f)
			arcToRelative(4.55f, 4.55f, 0f, isMoreThanHalf = true, isPositiveArc = true, -9.09f, 0f)
		}
		path(
			fill = SolidColor(Color(0xFF231F20)),
			fillAlpha = 0.2f,
			strokeAlpha = 0.2f
		) {
			moveTo(200.05f, 59.84f)
			arcToRelative(6.94f, 6.94f, 0f, isMoreThanHalf = true, isPositiveArc = false, 6.07f, 12.49f)
			arcToRelative(6.94f, 6.94f, 0f, isMoreThanHalf = false, isPositiveArc = true, -3.94f, -13.13f)
			arcTo(6.94f, 6.94f, 0f, isMoreThanHalf = false, isPositiveArc = false, 200.05f, 59.84f)
			close()
		}
		path(
			fill = SolidColor(Color(0xFF231F20)),
			fillAlpha = 0.2f,
			strokeAlpha = 0.2f
		) {
			moveTo(182.35f, 67.82f)
			curveToRelative(1.49f, 10.9f, 18.88f, 24.42f, 22.37f, 44.93f)
			arcToRelative(29.45f, 29.45f, 0f, isMoreThanHalf = false, isPositiveArc = false, 3.2f, -2.94f)
			curveToRelative(-4.25f, -19.36f, -20.53f, -32.34f, -21.97f, -42.87f)
			curveToRelative(-1.29f, -9.43f, 5.41f, -17.68f, 14.25f, -20.64f)
			curveTo(189.68f, 47.97f, 180.89f, 57.14f, 182.35f, 67.82f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB6B6))) {
			moveTo(190.78f, 103.74f)
			arcToRelative(9.96f, 9.96f, 0f, isMoreThanHalf = false, isPositiveArc = false, -10.64f, 8.93f)
			arcToRelative(9.56f, 9.56f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0f, 1.58f)
			lineToRelative(-31.68f, 14.26f)
			lineToRelative(15.1f, 9.37f)
			lineToRelative(27.77f, -14.98f)
			arcToRelative(9.93f, 9.93f, 0f, isMoreThanHalf = false, isPositiveArc = false, 8.33f, -8.81f)
			arcToRelative(9.5f, 9.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, -8.6f, -10.33f)
			quadTo(190.91f, 103.74f, 190.78f, 103.74f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(159.66f, 121.57f)
			lineTo(164.79f, 137.47f)
			lineToRelative(-31.1f, 14.36f)
			arcToRelative(14.33f, 14.33f, 0f, isMoreThanHalf = false, isPositiveArc = true, -13.93f, -1.01f)
			horizontalLineToRelative(0f)
			arcToRelative(13.73f, 13.73f, 0f, isMoreThanHalf = false, isPositiveArc = true, -5.51f, -15.56f)
			lineToRelative(16.67f, -51.55f)
			arcToRelative(14.8f, 14.8f, 0f, isMoreThanHalf = false, isPositiveArc = true, 17.8f, -9.88f)
			horizontalLineToRelative(0f)
			arcToRelative(14.04f, 14.04f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10.16f, 17.06f)
			quadToRelative(-0.05f, 0.21f, -0.11f, 0.42f)
			arcToRelative(14.4f, 14.4f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.5f, 1.47f)
			lineToRelative(-15.37f, 36.17f)
			close()
		}
	}.build()

	return _GearListEmptyState!!
}

@Suppress("ObjectPropertyName")
private var _GearListEmptyState: ImageVector? = null
