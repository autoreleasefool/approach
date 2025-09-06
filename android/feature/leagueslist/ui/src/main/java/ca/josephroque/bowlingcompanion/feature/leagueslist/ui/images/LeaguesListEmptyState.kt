package ca.josephroque.bowlingcompanion.feature.leagueslist.ui.images

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
fun imageLeaguesListEmptyState(accentColor: Color = MaterialTheme.colorScheme.primary): ImageVector {
	LaunchedEffect(isSystemInDarkTheme()) {
		// Clear cached image when theme changes
		_LeagueListEmptyState = null
	}
	if (_LeagueListEmptyState != null) {
		return _LeagueListEmptyState!!
	}
	_LeagueListEmptyState = ImageVector.Builder(
		name = "LeagueListEmptyState",
		defaultWidth = 853.12.dp,
		defaultHeight = 565.dp,
		viewportWidth = 853.12f,
		viewportHeight = 565f
	).apply {
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(819.77f, 378.11f)
			curveToRelative(-6.16f, 6.73f, -13.8f, 11.74f, -22.09f, 15.63f)
			horizontalLineToRelative(-0.01f)
			curveToRelative(-0.78f, 0.37f, -1.56f, 0.73f, -2.35f, 1.07f)
			horizontalLineToRelative(-0.01f)
			lineToRelative(-0.01f, 0.01f)
			lineToRelative(-0.01f, -0.01f)
			horizontalLineToRelative(-0.01f)
			arcToRelative(0.01f, 0.01f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.01f, 0.01f)
			lineTo(795.26f, 394.82f)
			lineToRelative(-0.01f, -0.01f)
			curveToRelative(-0.01f, 0.01f, -0.01f, 0.01f, -0.02f, 0f)
			lineToRelative(-0.01f, 0.01f)
			lineToRelative(-0.01f, -0.01f)
			horizontalLineToRelative(-0.01f)
			curveToRelative(-0.01f, 0.01f, -0.01f, 0.01f, -0.02f, 0f)
			horizontalLineToRelative(-0.01f)
			arcToRelative(139.77f, 139.77f, 0f, isMoreThanHalf = false, isPositiveArc = true, -17.06f, 6.1f)
			arcToRelative(236.65f, 236.65f, 0f, isMoreThanHalf = false, isPositiveArc = true, -105.11f, 5.49f)
			curveToRelative(-0.67f, -0.12f, -1.33f, -0.24f, -2f, -0.36f)
			lineTo(671f, 324.93f)
			curveToRelative(0.66f, -0.28f, 1.33f, -0.56f, 2f, -0.83f)
			quadToRelative(6.56f, -2.71f, 13.27f, -4.99f)
			quadToRelative(9.66f, -3.3f, 19.59f, -5.66f)
			arcToRelative(212.17f, 212.17f, 0f, isMoreThanHalf = false, isPositiveArc = true, 66.04f, -5.34f)
			quadToRelative(6.05f, 0.45f, 12.06f, 1.3f)
			curveToRelative(8.39f, 1.17f, 17.19f, 3.21f, 24.93f, 6.75f)
			horizontalLineToRelative(0.01f)
			curveToRelative(1.13f, 0.53f, 2.24f, 1.08f, 3.34f, 1.67f)
			curveToRelative(6.88f, 3.73f, 12.68f, 8.86f, 16.22f, 15.89f)
			arcToRelative(30.57f, 30.57f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.72f, 7.99f)
			verticalLineToRelative(0.02f)
			curveToRelative(0.2f, 0.96f, 0.34f, 1.93f, 0.45f, 2.89f)
			curveTo(832.89f, 356.46f, 827.98f, 369.13f, 819.77f, 378.11f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(831.63f, 344.62f)
			arcToRelative(1.36f, 1.36f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.3f, 0.04f)
			quadToRelative(-25.69f, 1.13f, -51.32f, 3.34f)
			horizontalLineToRelative(-0.03f)
			curveToRelative(-0.07f, 0.01f, -0.13f, 0.01f, -0.2f, 0.02f)
			quadTo(753.47f, 350.3f, 727.27f, 353.73f)
			curveToRelative(-0.74f, 0.1f, -1.47f, 0.2f, -2.2f, 0.29f)
			quadToRelative(-18.12f, 2.41f, -36.17f, 5.36f)
			quadToRelative(-7.97f, 1.3f, -15.9f, 2.72f)
			curveToRelative(-0.67f, 0.12f, -1.33f, 0.23f, -2f, 0.36f)
			verticalLineToRelative(-3.03f)
			curveToRelative(0.67f, -0.13f, 1.34f, -0.24f, 2f, -0.36f)
			quadToRelative(26.43f, -4.68f, 53.04f, -8.17f)
			quadToRelative(6.69f, -0.9f, 13.4f, -1.7f)
			quadToRelative(19.38f, -2.34f, 38.82f, -4.04f)
			curveToRelative(0.95f, -0.08f, 1.89f, -0.16f, 2.84f, -0.24f)
			quadToRelative(24.88f, -2.12f, 49.84f, -3.21f)
			arcToRelative(0.94f, 0.94f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.24f, 0.02f)
			curveTo(832.78f, 341.87f, 833.12f, 344.27f, 831.63f, 344.62f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(796.06f, 391.86f)
			curveToRelative(-21.34f, 0.16f, -42.36f, -8.89f, -57.46f, -23.87f)
			arcToRelative(77.83f, 77.83f, 0f, isMoreThanHalf = false, isPositiveArc = true, -11.18f, -14.07f)
			curveToRelative(-1.02f, -1.63f, -3.4f, 0.17f, -2.39f, 1.79f)
			curveToRelative(11.72f, 18.8f, 31.25f, 32.19f, 52.78f, 37.12f)
			arcToRelative(80.81f, 80.81f, 0f, isMoreThanHalf = false, isPositiveArc = false, 18.64f, 1.99f)
			curveToRelative(1.91f, -0.01f, 1.52f, -2.97f, -0.38f, -2.96f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(705.86f, 313.45f)
			arcToRelative(1.69f, 1.69f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.25f, 1.13f)
			arcToRelative(65.22f, 65.22f, 0f, isMoreThanHalf = false, isPositiveArc = false, -14.08f, 5.5f)
			arcToRelative(68.41f, 68.41f, 0f, isMoreThanHalf = false, isPositiveArc = false, -17.53f, 13.25f)
			curveToRelative(-0.27f, 0.27f, -0.53f, 0.54f, -0.78f, 0.82f)
			curveToRelative(-0.42f, 0.44f, -0.82f, 0.89f, -1.22f, 1.35f)
			verticalLineToRelative(-4.36f)
			curveToRelative(0.64f, -0.68f, 1.31f, -1.35f, 2f, -1.99f)
			arcToRelative(71.91f, 71.91f, 0f, isMoreThanHalf = false, isPositiveArc = true, 13.27f, -10.04f)
			quadTo(695.93f, 315.81f, 705.86f, 313.45f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(810.66f, 315.37f)
			arcToRelative(69.4f, 69.4f, 0f, isMoreThanHalf = false, isPositiveArc = false, -32.42f, 28.59f)
			arcToRelative(1.5f, 1.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.79f, 1.95f)
			arcToRelative(1.53f, 1.53f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.95f, -0.79f)
			arcTo(66.05f, 66.05f, 0f, isMoreThanHalf = false, isPositiveArc = true, 811.78f, 318.14f)
			curveToRelative(1.77f, -0.75f, 0.65f, -3.52f, -1.12f, -2.76f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(851.58f, 230.44f)
			curveToRelative(-2.32f, 8.82f, -6.77f, 16.81f, -12.3f, 24.1f)
			lineToRelative(-0.01f, 0.01f)
			curveToRelative(-0.51f, 0.68f, -1.04f, 1.37f, -1.59f, 2.03f)
			verticalLineToRelative(0.01f)
			arcToRelative(139.27f, 139.27f, 0f, isMoreThanHalf = false, isPositiveArc = true, -12.41f, 13.39f)
			arcToRelative(233.43f, 233.43f, 0f, isMoreThanHalf = false, isPositiveArc = true, -53.37f, 38.13f)
			curveToRelative(-30.67f, 16.02f, -64.95f, 25.49f, -98.9f, 26.03f)
			curveToRelative(-0.26f, 0.01f, -0.52f, 0.01f, -0.78f, 0.01f)
			curveToRelative(-0.41f, 0.01f, -0.82f, 0.01f, -1.22f, 0.01f)
			lineTo(671f, 288.71f)
			curveToRelative(0.65f, -1.24f, 1.32f, -2.48f, 2f, -3.71f)
			arcToRelative(219.52f, 219.52f, 0f, isMoreThanHalf = false, isPositiveArc = true, 32.03f, -43.82f)
			curveToRelative(0.3f, -0.33f, 0.61f, -0.66f, 0.92f, -0.98f)
			quadToRelative(7.02f, -7.41f, 14.72f, -14.11f)
			arcToRelative(210.82f, 210.82f, 0f, isMoreThanHalf = false, isPositiveArc = true, 67.29f, -39.85f)
			curveToRelative(7.97f, -2.86f, 16.71f, -5.15f, 25.21f, -5.6f)
			curveToRelative(1.25f, -0.07f, 2.49f, -0.1f, 3.74f, -0.09f)
			curveToRelative(7.83f, 0.11f, 15.34f, 1.96f, 21.75f, 6.54f)
			arcToRelative(31f, 31f, 0f, isMoreThanHalf = false, isPositiveArc = true, 6.12f, 5.81f)
			curveToRelative(0.62f, 0.77f, 1.19f, 1.56f, 1.74f, 2.37f)
			verticalLineToRelative(0.01f)
			curveTo(853.14f, 205.18f, 854.68f, 218.67f, 851.58f, 230.44f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(846.52f, 195.27f)
			verticalLineToRelative(0.01f)
			arcToRelative(1.46f, 1.46f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.24f, 0.17f)
			quadToRelative(-22.2f, 12.91f, -43.84f, 26.77f)
			curveToRelative(-0.02f, 0.01f, -0.03f, 0.02f, -0.05f, 0.03f)
			arcToRelative(1.74f, 1.74f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.18f, 0.11f)
			quadToRelative(-22.25f, 14.25f, -43.86f, 29.46f)
			curveToRelative(-0.6f, 0.42f, -1.21f, 0.85f, -1.82f, 1.28f)
			quadToRelative(-14.93f, 10.55f, -29.54f, 21.54f)
			quadToRelative(-27.6f, 20.79f, -53.99f, 43.12f)
			curveToRelative(-0.67f, 0.56f, -1.33f, 1.13f, -2f, 1.7f)
			verticalLineToRelative(-3.89f)
			curveToRelative(0.66f, -0.57f, 1.33f, -1.14f, 2f, -1.7f)
			quadToRelative(10.11f, -8.53f, 20.4f, -16.83f)
			curveToRelative(2.05f, -1.65f, 4.11f, -3.3f, 6.17f, -4.93f)
			quadToRelative(27.45f, -21.85f, 56.13f, -42.08f)
			horizontalLineToRelative(0.01f)
			quadToRelative(5.64f, -3.97f, 11.3f, -7.88f)
			quadToRelative(16.08f, -11.07f, 32.52f, -21.61f)
			curveToRelative(0.79f, -0.51f, 1.59f, -1.02f, 2.39f, -1.53f)
			quadToRelative(21.05f, -13.44f, 42.64f, -26f)
			arcToRelative(1.49f, 1.49f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.22f, -0.11f)
			curveTo(846.28f, 192.3f, 847.69f, 194.28f, 846.52f, 195.27f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(836.97f, 253.63f)
			curveToRelative(-18.82f, 10.05f, -41.65f, 11.8f, -61.97f, 5.55f)
			arcToRelative(77.83f, 77.83f, 0f, isMoreThanHalf = false, isPositiveArc = true, -16.44f, -7.26f)
			curveToRelative(-1.66f, -0.97f, -2.93f, 1.73f, -1.29f, 2.69f)
			curveToRelative(19.11f, 11.2f, 42.63f, 13.99f, 63.98f, 8.35f)
			arcToRelative(80.81f, 80.81f, 0f, isMoreThanHalf = false, isPositiveArc = false, 17.43f, -6.89f)
			curveToRelative(1.69f, -0.9f, -0.04f, -3.34f, -1.71f, -2.44f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(696.12f, 290.22f)
			arcToRelative(68.55f, 68.55f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.95f, -15.87f)
			arcToRelative(72.32f, 72.32f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10.78f, -34.15f)
			quadToRelative(7.03f, -7.41f, 14.72f, -14.11f)
			arcToRelative(1.7f, 1.7f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.59f, 1.58f)
			arcToRelative(65.45f, 65.45f, 0f, isMoreThanHalf = false, isPositiveArc = false, -9.91f, 11.41f)
			arcToRelative(69.12f, 69.12f, 0f, isMoreThanHalf = false, isPositiveArc = false, -11.08f, 50.89f)
			arcToRelative(1.21f, 1.21f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.31f, 1.14f)
			arcTo(1.62f, 1.62f, 0f, isMoreThanHalf = false, isPositiveArc = true, 696.12f, 290.22f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(814.37f, 179.11f)
			arcToRelative(69.4f, 69.4f, 0f, isMoreThanHalf = false, isPositiveArc = false, -15.43f, 40.37f)
			arcToRelative(1.5f, 1.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.61f, 1.36f)
			arcToRelative(1.53f, 1.53f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.36f, -1.61f)
			arcToRelative(66.05f, 66.05f, 0f, isMoreThanHalf = false, isPositiveArc = true, 14.74f, -38.2f)
			curveToRelative(1.21f, -1.49f, -1.06f, -3.42f, -2.28f, -1.93f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(710.81f, 211.97f)
			verticalLineToRelative(0.01f)
			curveToRelative(-0.07f, 0.86f, -0.15f, 1.71f, -0.24f, 2.56f)
			verticalLineToRelative(0.02f)
			arcToRelative(141.3f, 141.3f, 0f, isMoreThanHalf = false, isPositiveArc = true, -3.25f, 17.96f)
			curveToRelative(-0.7f, 2.9f, -1.46f, 5.78f, -2.29f, 8.66f)
			curveToRelative(-0.1f, 0.37f, -0.21f, 0.74f, -0.32f, 1.1f)
			verticalLineToRelative(0.01f)
			arcToRelative(229.52f, 229.52f, 0f, isMoreThanHalf = false, isPositiveArc = true, -8.74f, 24.65f)
			arcToRelative(238.92f, 238.92f, 0f, isMoreThanHalf = false, isPositiveArc = true, -22.97f, 42.22f)
			curveToRelative(-0.65f, 0.97f, -1.32f, 1.94f, -2f, 2.9f)
			lineTo(671f, 155.46f)
			curveToRelative(0.67f, -0.04f, 1.33f, -0.04f, 2f, -0.02f)
			quadToRelative(0.48f, 0f, 0.96f, 0.03f)
			arcToRelative(30.32f, 30.32f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8.28f, 1.61f)
			curveToRelative(0.94f, 0.31f, 1.85f, 0.66f, 2.75f, 1.05f)
			curveToRelative(10.91f, 4.78f, 19.48f, 15.32f, 23.2f, 26.91f)
			curveTo(710.98f, 193.72f, 711.54f, 202.85f, 710.81f, 211.97f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(684.99f, 158.13f)
			arcToRelative(1.08f, 1.08f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.11f, 0.28f)
			quadToRelative(-6.07f, 11.81f, -11.88f, 23.73f)
			curveToRelative(-0.67f, 1.37f, -1.34f, 2.75f, -2f, 4.12f)
			verticalLineToRelative(-6.8f)
			quadToRelative(0.99f, -2.04f, 2f, -4.08f)
			quadToRelative(4.49f, -9.09f, 9.12f, -18.1f)
			arcToRelative(1.3f, 1.3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.12f, -0.2f)
			curveTo(683.16f, 155.76f, 685.44f, 156.66f, 684.99f, 158.13f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(711.13f, 213.58f)
			curveToRelative(-0.18f, 0.32f, -0.36f, 0.64f, -0.56f, 0.96f)
			arcToRelative(79.82f, 79.82f, 0f, isMoreThanHalf = false, isPositiveArc = true, -10.42f, 14.24f)
			arcToRelative(85.92f, 85.92f, 0f, isMoreThanHalf = false, isPositiveArc = true, -27.15f, 19.77f)
			curveToRelative(-0.66f, 0.32f, -1.33f, 0.62f, -2f, 0.9f)
			lineTo(671f, 246.2f)
			curveToRelative(0.67f, -0.3f, 1.34f, -0.61f, 2f, -0.93f)
			arcToRelative(80.87f, 80.87f, 0f, isMoreThanHalf = false, isPositiveArc = false, 35.37f, -32.82f)
			arcToRelative(1.64f, 1.64f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.44f, -0.47f)
			arcTo(1.15f, 1.15f, 0f, isMoreThanHalf = false, isPositiveArc = true, 711.13f, 213.58f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(673f, 33f)
			verticalLineToRelative(76f)
			horizontalLineToRelative(-2f)
			verticalLineToRelative(-0.81f)
			horizontalLineToRelative(-669f)
			verticalLineToRelative(0.81f)
			horizontalLineToRelative(-2f)
			verticalLineToRelative(-76f)
			arcToRelative(33.03f, 33.03f, 0f, isMoreThanHalf = false, isPositiveArc = true, 33f, -33f)
			horizontalLineToRelative(607f)
			arcTo(33.03f, 33.03f, 0f, isMoreThanHalf = false, isPositiveArc = true, 673f, 33f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(640f, 0f)
			horizontalLineToRelative(-607f)
			arcToRelative(33.03f, 33.03f, 0f, isMoreThanHalf = false, isPositiveArc = false, -33f, 33f)
			verticalLineToRelative(434f)
			arcToRelative(33.03f, 33.03f, 0f, isMoreThanHalf = false, isPositiveArc = false, 33f, 33f)
			horizontalLineToRelative(607f)
			arcToRelative(33.03f, 33.03f, 0f, isMoreThanHalf = false, isPositiveArc = false, 33f, -33f)
			verticalLineToRelative(-434f)
			arcTo(33.03f, 33.03f, 0f, isMoreThanHalf = false, isPositiveArc = false, 640f, 0f)
			close()
			moveTo(671f, 467f)
			arcToRelative(31.04f, 31.04f, 0f, isMoreThanHalf = false, isPositiveArc = true, -31f, 31f)
			horizontalLineToRelative(-607f)
			arcToRelative(31.04f, 31.04f, 0f, isMoreThanHalf = false, isPositiveArc = true, -31f, -31f)
			verticalLineToRelative(-434f)
			arcToRelative(31.04f, 31.04f, 0f, isMoreThanHalf = false, isPositiveArc = true, 31f, -31f)
			horizontalLineToRelative(607f)
			arcToRelative(31.04f, 31.04f, 0f, isMoreThanHalf = false, isPositiveArc = true, 31f, 31f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(136f, 54.5f)
			moveToRelative(-20f, 0f)
			arcToRelative(20f, 20f, 0f, isMoreThanHalf = true, isPositiveArc = true, 40f, 0f)
			arcToRelative(20f, 20f, 0f, isMoreThanHalf = true, isPositiveArc = true, -40f, 0f)
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(537f, 54.5f)
			moveToRelative(-20f, 0f)
			arcToRelative(20f, 20f, 0f, isMoreThanHalf = true, isPositiveArc = true, 40f, 0f)
			arcToRelative(20f, 20f, 0f, isMoreThanHalf = true, isPositiveArc = true, -40f, 0f)
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(193.78f, 277.5f)
			lineTo(74.81f, 277.5f)
			arcToRelative(19.03f, 19.03f, 0f, isMoreThanHalf = false, isPositiveArc = true, -19.01f, -19.01f)
			lineTo(55.8f, 202.26f)
			arcToRelative(19.03f, 19.03f, 0f, isMoreThanHalf = false, isPositiveArc = true, 19.01f, -19.01f)
			lineTo(193.78f, 183.25f)
			arcToRelative(19.03f, 19.03f, 0f, isMoreThanHalf = false, isPositiveArc = true, 19.01f, 19.01f)
			verticalLineToRelative(56.23f)
			arcTo(19.03f, 19.03f, 0f, isMoreThanHalf = false, isPositiveArc = true, 193.78f, 277.5f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(396.18f, 277.5f)
			lineTo(277.21f, 277.5f)
			arcToRelative(19.03f, 19.03f, 0f, isMoreThanHalf = false, isPositiveArc = true, -19.01f, -19.01f)
			lineTo(258.2f, 202.26f)
			arcToRelative(19.03f, 19.03f, 0f, isMoreThanHalf = false, isPositiveArc = true, 19.01f, -19.01f)
			lineTo(396.18f, 183.25f)
			arcToRelative(19.03f, 19.03f, 0f, isMoreThanHalf = false, isPositiveArc = true, 19.01f, 19.01f)
			verticalLineToRelative(56.23f)
			arcTo(19.03f, 19.03f, 0f, isMoreThanHalf = false, isPositiveArc = true, 396.18f, 277.5f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(396.18f, 422.79f)
			lineTo(277.21f, 422.79f)
			arcToRelative(19.61f, 19.61f, 0f, isMoreThanHalf = false, isPositiveArc = true, -19.59f, -19.59f)
			lineTo(257.62f, 346.97f)
			arcTo(19.61f, 19.61f, 0f, isMoreThanHalf = false, isPositiveArc = true, 277.21f, 327.39f)
			lineTo(396.18f, 327.39f)
			arcTo(19.61f, 19.61f, 0f, isMoreThanHalf = false, isPositiveArc = true, 415.77f, 346.97f)
			verticalLineToRelative(56.23f)
			arcTo(19.61f, 19.61f, 0f, isMoreThanHalf = false, isPositiveArc = true, 396.18f, 422.79f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(193.39f, 422.79f)
			lineTo(74.42f, 422.79f)
			arcTo(19.61f, 19.61f, 0f, isMoreThanHalf = false, isPositiveArc = true, 54.83f, 403.21f)
			lineTo(54.83f, 346.97f)
			arcTo(19.61f, 19.61f, 0f, isMoreThanHalf = false, isPositiveArc = true, 74.42f, 327.39f)
			lineTo(193.39f, 327.39f)
			arcToRelative(19.61f, 19.61f, 0f, isMoreThanHalf = false, isPositiveArc = true, 19.59f, 19.59f)
			verticalLineToRelative(56.23f)
			arcTo(19.61f, 19.61f, 0f, isMoreThanHalf = false, isPositiveArc = true, 193.39f, 422.79f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(598.58f, 422.79f)
			lineTo(479.61f, 422.79f)
			arcToRelative(19.61f, 19.61f, 0f, isMoreThanHalf = false, isPositiveArc = true, -19.59f, -19.59f)
			lineTo(460.02f, 346.97f)
			arcTo(19.61f, 19.61f, 0f, isMoreThanHalf = false, isPositiveArc = true, 479.61f, 327.39f)
			lineTo(598.58f, 327.39f)
			arcToRelative(19.61f, 19.61f, 0f, isMoreThanHalf = false, isPositiveArc = true, 19.59f, 19.59f)
			verticalLineToRelative(56.23f)
			arcTo(19.61f, 19.61f, 0f, isMoreThanHalf = false, isPositiveArc = true, 598.58f, 422.79f)
			close()
		}
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(598.58f, 277.61f)
			lineTo(479.61f, 277.61f)
			arcToRelative(19.61f, 19.61f, 0f, isMoreThanHalf = false, isPositiveArc = true, -19.59f, -19.59f)
			lineTo(460.02f, 201.79f)
			arcToRelative(19.61f, 19.61f, 0f, isMoreThanHalf = false, isPositiveArc = true, 19.59f, -19.59f)
			lineTo(598.58f, 182.21f)
			arcToRelative(19.61f, 19.61f, 0f, isMoreThanHalf = false, isPositiveArc = true, 19.59f, 19.59f)
			verticalLineToRelative(56.23f)
			arcTo(19.61f, 19.61f, 0f, isMoreThanHalf = false, isPositiveArc = true, 598.58f, 277.61f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(134.3f, 230.37f)
			moveToRelative(-32.26f, 0f)
			arcToRelative(32.26f, 32.26f, 0f, isMoreThanHalf = true, isPositiveArc = true, 64.52f, 0f)
			arcToRelative(32.26f, 32.26f, 0f, isMoreThanHalf = true, isPositiveArc = true, -64.52f, 0f)
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(131.1f, 242.68f)
			arcToRelative(3.35f, 3.35f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2.01f, -0.67f)
			lineToRelative(-0.04f, -0.03f)
			lineToRelative(-7.58f, -5.8f)
			arcToRelative(3.37f, 3.37f, 0f, isMoreThanHalf = true, isPositiveArc = true, 4.1f, -5.35f)
			lineToRelative(4.91f, 3.77f)
			lineToRelative(11.61f, -15.13f)
			arcToRelative(3.37f, 3.37f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4.72f, -0.62f)
			lineToRelative(-0.07f, 0.1f)
			lineToRelative(0.07f, -0.1f)
			arcToRelative(3.37f, 3.37f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.62f, 4.72f)
			lineToRelative(-13.65f, 17.8f)
			arcTo(3.37f, 3.37f, 0f, isMoreThanHalf = false, isPositiveArc = true, 131.1f, 242.68f)
			close()
		}
		path(fill = SolidColor(Color.White)) {
			moveTo(337.27f, 229.91f)
			moveToRelative(-32.26f, 0f)
			arcToRelative(32.26f, 32.26f, 0f, isMoreThanHalf = true, isPositiveArc = true, 64.52f, 0f)
			arcToRelative(32.26f, 32.26f, 0f, isMoreThanHalf = true, isPositiveArc = true, -64.52f, 0f)
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(334.07f, 242.22f)
			arcToRelative(3.35f, 3.35f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2.01f, -0.67f)
			lineToRelative(-0.04f, -0.03f)
			lineToRelative(-7.58f, -5.8f)
			arcToRelative(3.37f, 3.37f, 0f, isMoreThanHalf = true, isPositiveArc = true, 4.1f, -5.35f)
			lineToRelative(4.91f, 3.77f)
			lineToRelative(11.61f, -15.13f)
			arcToRelative(3.37f, 3.37f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4.72f, -0.62f)
			lineToRelative(-0.07f, 0.1f)
			lineToRelative(0.07f, -0.1f)
			arcToRelative(3.37f, 3.37f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.62f, 4.72f)
			lineToRelative(-13.65f, 17.8f)
			arcTo(3.37f, 3.37f, 0f, isMoreThanHalf = false, isPositiveArc = true, 334.07f, 242.22f)
			close()
		}
		path(fill = SolidColor(Color(0xFFA0616A))) {
			moveTo(555.31f, 382.15f)
			arcToRelative(10.74f, 10.74f, 0f, isMoreThanHalf = false, isPositiveArc = false, -2.18f, -16.33f)
			lineToRelative(-18.01f, -96.17f)
			lineToRelative(-21.9f, 8.12f)
			lineToRelative(24.59f, 91.98f)
			arcToRelative(10.8f, 10.8f, 0f, isMoreThanHalf = false, isPositiveArc = false, 17.51f, 12.4f)
			close()
		}
		path(fill = SolidColor(Color(0xFFA0616A))) {
			moveTo(380.53f, 235.4f)
			arcToRelative(11.41f, 11.41f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.02f, 1.8f)
			lineToRelative(42.59f, 32.78f)
			lineToRelative(12.08f, -4.96f)
			lineToRelative(9.87f, 17.25f)
			lineToRelative(-20.53f, 10.86f)
			arcToRelative(8.67f, 8.67f, 0f, isMoreThanHalf = false, isPositiveArc = true, -10.28f, -1.63f)
			lineToRelative(-42.57f, -43.89f)
			arcToRelative(11.38f, 11.38f, 0f, isMoreThanHalf = true, isPositiveArc = true, 8.82f, -12.21f)
			close()
		}
		path(fill = SolidColor(Color(0xFFA0616A))) {
			moveTo(480.03f, 552.85f)
			lineToRelative(-12.26f, 0f)
			lineToRelative(-5.83f, -37.29f)
			lineToRelative(18.1f, 0f)
			lineToRelative(-0f, 37.29f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(483.15f, 564.73f)
			lineToRelative(-39.53f, -0f)
			verticalLineToRelative(-0.5f)
			arcToRelative(15.39f, 15.39f, 0f, isMoreThanHalf = false, isPositiveArc = true, 15.39f, -15.39f)
			horizontalLineToRelative(0f)
			lineToRelative(24.14f, 0f)
			close()
		}
		path(fill = SolidColor(Color(0xFFA0616A))) {
			moveTo(526.03f, 552.85f)
			lineToRelative(-12.26f, 0f)
			lineToRelative(-5.83f, -37.29f)
			lineToRelative(18.1f, 0f)
			lineToRelative(-0f, 37.29f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(529.15f, 564.73f)
			lineToRelative(-39.53f, -0f)
			verticalLineToRelative(-0.5f)
			arcToRelative(15.39f, 15.39f, 0f, isMoreThanHalf = false, isPositiveArc = true, 15.39f, -15.39f)
			horizontalLineToRelative(0f)
			lineToRelative(24.14f, 0f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(465.22f, 545.48f)
			arcToRelative(4.49f, 4.49f, 0f, isMoreThanHalf = false, isPositiveArc = true, -4.47f, -4.07f)
			lineTo(450.06f, 397.72f)
			lineToRelative(0.5f, -0.04f)
			lineToRelative(73.52f, -6.04f)
			lineToRelative(0.02f, 0.52f)
			lineTo(529.95f, 541.01f)
			arcToRelative(4.5f, 4.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, -4.5f, 4.67f)
			lineTo(511.04f, 545.67f)
			arcToRelative(4.47f, 4.47f, 0f, isMoreThanHalf = false, isPositiveArc = true, -4.45f, -3.82f)
			lineTo(487.02f, 421.61f)
			arcToRelative(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, -0.99f, 0.07f)
			lineTo(485.14f, 540.41f)
			arcToRelative(4.5f, 4.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, -4.26f, 4.46f)
			lineToRelative(-15.41f, 0.6f)
			curveTo(465.38f, 545.48f, 465.3f, 545.48f, 465.22f, 545.48f)
			close()
		}
		path(fill = SolidColor(Color(0xFFA0616A))) {
			moveTo(477.77f, 191.56f)
			moveToRelative(-24.56f, 0f)
			arcToRelative(24.56f, 24.56f, 0f, isMoreThanHalf = true, isPositiveArc = true, 49.12f, 0f)
			arcToRelative(24.56f, 24.56f, 0f, isMoreThanHalf = true, isPositiveArc = true, -49.12f, 0f)
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(482.53f, 406.88f)
			arcToRelative(121.04f, 121.04f, 0f, isMoreThanHalf = false, isPositiveArc = true, -31.77f, -4.34f)
			arcToRelative(4.51f, 4.51f, 0f, isMoreThanHalf = false, isPositiveArc = true, -3.24f, -4.69f)
			curveToRelative(3.31f, -49.69f, 4.08f, -88.26f, -2.87f, -114.94f)
			curveToRelative(-2.96f, -11.37f, -1.62f, -23.35f, 3.69f, -32.86f)
			curveToRelative(7.99f, -14.31f, 22.68f, -23.02f, 38.34f, -22.72f)
			horizontalLineToRelative(0f)
			quadToRelative(1.12f, 0.02f, 2.27f, 0.08f)
			curveToRelative(23.77f, 1.22f, 42.3f, 22.73f, 41.29f, 47.94f)
			lineToRelative(-4.78f, 120.17f)
			arcToRelative(4.44f, 4.44f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2.82f, 4.04f)
			arcTo(114.25f, 114.25f, 0f, isMoreThanHalf = false, isPositiveArc = true, 482.53f, 406.88f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(448.7f, 292.14f)
			lineToRelative(-18.4f, -22.54f)
			arcToRelative(5.76f, 5.76f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.51f, -8.59f)
			lineToRelative(24.92f, -14.85f)
			arcToRelative(16f, 16f, 0f, isMoreThanHalf = false, isPositiveArc = true, 20.16f, 24.85f)
			lineToRelative(-19.48f, 21.37f)
			arcToRelative(5.76f, 5.76f, 0f, isMoreThanHalf = false, isPositiveArc = true, -8.72f, -0.24f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(506.57f, 289.31f)
			arcToRelative(5.76f, 5.76f, 0f, isMoreThanHalf = false, isPositiveArc = true, -3.16f, -3.61f)
			lineToRelative(-8.28f, -27.71f)
			arcTo(16f, 16f, 0f, isMoreThanHalf = false, isPositiveArc = true, 524.07f, 244.35f)
			lineTo(540.08f, 268.54f)
			arcToRelative(5.76f, 5.76f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2.36f, 8.4f)
			lineToRelative(-26.35f, 12.34f)
			arcTo(5.76f, 5.76f, 0f, isMoreThanHalf = false, isPositiveArc = true, 506.57f, 289.31f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(489.91f, 211.72f)
			lineToRelative(-18.21f, -4.17f)
			curveToRelative(-1.88f, -0.43f, -4.13f, -1.25f, -4.39f, -3.16f)
			curveToRelative(-0.35f, -2.57f, 3.34f, -4.35f, 3f, -6.92f)
			curveToRelative(-0.33f, -2.49f, -3.69f, -2.81f, -6.09f, -3.54f)
			arcToRelative(9.11f, 9.11f, 0f, isMoreThanHalf = false, isPositiveArc = true, -5.67f, -11.34f)
			curveToRelative(-2.59f, 3.66f, -8.52f, 3.97f, -11.89f, 1.01f)
			reflectiveCurveToRelative(-4.01f, -8.34f, -1.99f, -12.34f)
			arcToRelative(14.29f, 14.29f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10.72f, -7.24f)
			arcToRelative(22.62f, 22.62f, 0f, isMoreThanHalf = false, isPositiveArc = true, 13.02f, 2.43f)
			curveToRelative(0.27f, -2.83f, 3.8f, -3.98f, 6.65f, -4.01f)
			arcToRelative(28.43f, 28.43f, 0f, isMoreThanHalf = false, isPositiveArc = true, 26.64f, 19.45f)
			curveToRelative(3.49f, 11.25f, 1.16f, 23.59f, -8.61f, 30.17f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(673.29f, 565f)
			horizontalLineToRelative(-381f)
			arcToRelative(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, -2f)
			horizontalLineToRelative(381f)
			arcToRelative(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = true, 0f, 2f)
			close()
		}
	}.build()

	return _LeagueListEmptyState!!
}

@Suppress("ObjectPropertyName")
private var _LeagueListEmptyState: ImageVector? = null
