package ca.josephroque.bowlingcompanion.feature.alleyslist.ui.images

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
fun imageAlleysListEmptyState(accentColor: Color = MaterialTheme.colorScheme.primary): ImageVector {
	LaunchedEffect(isSystemInDarkTheme()) {
		// Clear cached image when theme changes
		_AlleysListEmptyState = null
	}
	if (_AlleysListEmptyState != null) {
		return _AlleysListEmptyState!!
	}
	_AlleysListEmptyState = ImageVector.Builder(
		name = "AlleysListEmptyState",
		defaultWidth = 1123.dp,
		defaultHeight = 672.4.dp,
		viewportWidth = 1123f,
		viewportHeight = 672.4f
	).apply {
		path(fill = SolidColor(Color(0xFFF2F2F2))) {
			moveTo(923.6f, 77f)
			curveTo(794.6f, -22.5f, 615.1f, -26.5f, 484.1f, 70.4f)
			curveTo(414.9f, 121.6f, 361.2f, 200f, 370.9f, 319.3f)
			curveToRelative(3.5f, 42.2f, 13.1f, 76.8f, 27.1f, 105.2f)
			curveToRelative(56.8f, 114.9f, 289.9f, 110.5f, 302.9f, 110.5f)
			quadToRelative(12.6f, -0f, 25.2f, 0.6f)
			arcToRelative(551.9f, 551.9f, 0f, isMoreThanHalf = false, isPositiveArc = true, 87.4f, 11.1f)
			curveToRelative(41.1f, 8.6f, 93.3f, 12.9f, 140f, -3.4f)
			arcToRelative(162.3f, 162.3f, 0f, isMoreThanHalf = false, isPositiveArc = false, 23.1f, -10f)
			curveToRelative(25.7f, -13.6f, 54.4f, -26.7f, 71.2f, -59f)
			curveToRelative(12.6f, -24.3f, 21.8f, -54.7f, 26.2f, -92.6f)
			curveTo(1090.1f, 243.5f, 1009.6f, 143.3f, 923.6f, 77f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(357.6f, 537.2f)
			arcToRelative(369f, 34.6f, 0f, isMoreThanHalf = true, isPositiveArc = false, 738f, 0f)
			arcToRelative(369f, 34.6f, 0f, isMoreThanHalf = true, isPositiveArc = false, -738f, 0f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(365.4f, 537.2f)
			arcToRelative(343.2f, 16.3f, 0f, isMoreThanHalf = true, isPositiveArc = false, 686.4f, 0f)
			arcToRelative(343.2f, 16.3f, 0f, isMoreThanHalf = true, isPositiveArc = false, -686.4f, 0f)
			close()
		}
		path(fill = SolidColor(Color(0xFF575A89))) {
			moveTo(898.4f, 73.3f)
			lineToRelative(0f, 38.9f)
			lineToRelative(-50.6f, 0f)
			lineToRelative(0f, 423.8f)
			lineToRelative(105.6f, 0f)
			lineToRelative(0f, -407.7f)
			lineToRelative(0f, -16.1f)
			lineToRelative(0f, -38.9f)
			lineToRelative(-55f, 0f)
			close()
		}
		path(fill = SolidColor(Color(0xFF575A89))) {
			moveTo(619.8f, 334.4f)
			lineToRelative(0f, -66.7f)
			lineToRelative(-64.5f, 0f)
			lineToRelative(0f, 66.7f)
			lineToRelative(-24.2f, 0f)
			lineToRelative(0f, 202.4f)
			lineToRelative(112.2f, 0f)
			lineToRelative(0f, -202.4f)
			lineToRelative(-23.5f, 0f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(543.6f, 442.9f)
			horizontalLineToRelative(87.3f)
			verticalLineToRelative(94.6f)
			horizontalLineToRelative(-87.3f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(543.6f, 442.9f)
			horizontalLineToRelative(13.9f)
			verticalLineToRelative(94.6f)
			horizontalLineToRelative(-13.9f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(810.5f, 52.1f)
			lineToRelative(0f, 22.7f)
			lineToRelative(-129f, 0f)
			lineToRelative(0f, -22.7f)
			lineToRelative(-38.1f, 0f)
			lineToRelative(0f, 485.4f)
			lineToRelative(204.6f, 0f)
			lineToRelative(0f, -485.4f)
			lineToRelative(-37.4f, 0f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(810.5f, 52.1f)
			horizontalLineToRelative(28.6f)
			verticalLineToRelative(22.7f)
			horizontalLineToRelative(-28.6f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(643.3f, 52.1f)
			horizontalLineToRelative(28.6f)
			verticalLineToRelative(485.4f)
			horizontalLineToRelative(-28.6f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(497.4f, 190.7f)
			lineToRelative(0f, -18.3f)
			lineToRelative(-19.1f, 0f)
			lineToRelative(0f, 18.3f)
			lineToRelative(-34.5f, 0f)
			lineToRelative(0f, 346.8f)
			lineToRelative(87.3f, 0f)
			lineToRelative(0f, -346.8f)
			lineToRelative(-33.7f, 0f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(478.3f, 172.3f)
			horizontalLineToRelative(10.3f)
			verticalLineToRelative(18.3f)
			horizontalLineToRelative(-10.3f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(443.9f, 190.7f)
			horizontalLineToRelative(10.3f)
			verticalLineToRelative(346.8f)
			horizontalLineToRelative(-10.3f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(810.5f, 239f)
			horizontalLineToRelative(28.6f)
			verticalLineToRelative(298.4f)
			horizontalLineToRelative(-28.6f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(504.7f, 190.7f)
			horizontalLineToRelative(26.4f)
			verticalLineToRelative(63.8f)
			horizontalLineToRelative(-26.4f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFF6584))) {
			moveTo(674.8f, 113.7f)
			horizontalLineToRelative(41.1f)
			verticalLineToRelative(17.6f)
			horizontalLineToRelative(-41.1f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFF6584))) {
			moveTo(762.1f, 294.8f)
			horizontalLineToRelative(41.1f)
			verticalLineToRelative(17.6f)
			horizontalLineToRelative(-41.1f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFF6584))) {
			moveTo(674.8f, 421.6f)
			horizontalLineToRelative(41.1f)
			verticalLineToRelative(17.6f)
			horizontalLineToRelative(-41.1f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFF6584))) {
			moveTo(467.3f, 269.8f)
			horizontalLineToRelative(41.1f)
			verticalLineToRelative(17.6f)
			horizontalLineToRelative(-41.1f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFF6584))) {
			moveTo(567f, 453.1f)
			horizontalLineToRelative(41.1f)
			verticalLineToRelative(17.6f)
			horizontalLineToRelative(-41.1f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(552.4f, 349.8f)
			horizontalLineToRelative(70.4f)
			verticalLineToRelative(29.3f)
			horizontalLineToRelative(-70.4f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(860.3f, 145.9f)
			horizontalLineToRelative(70.4f)
			verticalLineToRelative(29.3f)
			horizontalLineToRelative(-70.4f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(860.3f, 208.2f)
			horizontalLineToRelative(70.4f)
			verticalLineToRelative(29.3f)
			horizontalLineToRelative(-70.4f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(860.3f, 270.6f)
			horizontalLineToRelative(70.4f)
			verticalLineToRelative(29.3f)
			horizontalLineToRelative(-70.4f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(860.3f, 332.9f)
			horizontalLineToRelative(70.4f)
			verticalLineToRelative(29.3f)
			horizontalLineToRelative(-70.4f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(860.3f, 395.2f)
			horizontalLineToRelative(70.4f)
			verticalLineToRelative(29.3f)
			horizontalLineToRelative(-70.4f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(860.3f, 457.5f)
			horizontalLineToRelative(70.4f)
			verticalLineToRelative(29.3f)
			horizontalLineToRelative(-70.4f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(977.6f, 278.6f)
			lineToRelative(0f, -79.9f)
			lineToRelative(-51.3f, 0f)
			lineToRelative(0f, 79.9f)
			lineToRelative(-18.3f, 0f)
			lineToRelative(0f, 258.8f)
			lineToRelative(87.3f, 0f)
			lineToRelative(0f, -258.8f)
			lineToRelative(-17.6f, 0f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(926.3f, 198.7f)
			horizontalLineToRelative(16.1f)
			verticalLineToRelative(79.9f)
			horizontalLineToRelative(-16.1f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(908f, 278.6f)
			horizontalLineToRelative(16.1f)
			verticalLineToRelative(258.8f)
			horizontalLineToRelative(-16.1f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(952f, 408.4f)
			horizontalLineToRelative(43.3f)
			verticalLineToRelative(25.7f)
			horizontalLineToRelative(-43.3f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFF6584))) {
			moveTo(953.4f, 370.3f)
			horizontalLineToRelative(41.1f)
			verticalLineToRelative(17.6f)
			horizontalLineToRelative(-41.1f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(1123f, 388.1f)
			curveToRelative(0f, 65.3f, -38.8f, 88.1f, -86.7f, 88.1f)
			reflectiveCurveToRelative(-86.7f, -22.8f, -86.7f, -88.1f)
			reflectiveCurveToRelative(86.7f, -148.4f, 86.7f, -148.4f)
			reflectiveCurveTo(1123f, 322.7f, 1123f, 388.1f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(1033.1f, 466.2f)
			lineToRelative(0.9f, -54.7f)
			lineToRelative(37f, -67.6f)
			lineToRelative(-36.8f, 59.1f)
			lineToRelative(0.4f, -24.6f)
			lineToRelative(25.5f, -48.9f)
			lineToRelative(-25.4f, 42.4f)
			lineToRelative(0f, 0f)
			lineToRelative(0.7f, -44.2f)
			lineToRelative(27.3f, -39f)
			lineToRelative(-27.2f, 32f)
			lineToRelative(0.4f, -81.1f)
			lineToRelative(-2.8f, 107.3f)
			lineToRelative(0.2f, -4.4f)
			lineToRelative(-27.7f, -42.5f)
			lineToRelative(27.3f, 51f)
			lineToRelative(-2.6f, 49.4f)
			lineToRelative(-0.1f, -1.3f)
			lineToRelative(-32f, -44.7f)
			lineToRelative(31.9f, 49.3f)
			lineToRelative(-0.3f, 6.2f)
			lineToRelative(-0.1f, 0.1f)
			lineToRelative(0f, 0.5f)
			lineToRelative(-6.6f, 125.3f)
			lineToRelative(8.8f, 0f)
			lineToRelative(1.1f, -64.7f)
			lineToRelative(31.8f, -49.2f)
			lineToRelative(-31.7f, 44.3f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(487.3f, 388.8f)
			curveToRelative(0f, 65.3f, -38.8f, 88.1f, -86.7f, 88.1f)
			reflectiveCurveToRelative(-86.7f, -22.8f, -86.7f, -88.1f)
			reflectiveCurveToRelative(86.7f, -148.4f, 86.7f, -148.4f)
			reflectiveCurveTo(487.3f, 323.4f, 487.3f, 388.8f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(397.4f, 466.9f)
			lineToRelative(0.9f, -54.7f)
			lineToRelative(37f, -67.6f)
			lineToRelative(-36.8f, 59.1f)
			lineToRelative(0.4f, -24.6f)
			lineToRelative(25.5f, -48.9f)
			lineToRelative(-25.4f, 42.4f)
			lineToRelative(0f, 0f)
			lineToRelative(0.7f, -44.2f)
			lineToRelative(27.3f, -39f)
			lineToRelative(-27.2f, 32f)
			lineToRelative(0.4f, -81.1f)
			lineToRelative(-2.8f, 107.3f)
			lineToRelative(0.2f, -4.4f)
			lineToRelative(-27.7f, -42.5f)
			lineToRelative(27.3f, 51f)
			lineToRelative(-2.6f, 49.4f)
			lineToRelative(-0.1f, -1.3f)
			lineToRelative(-32f, -44.7f)
			lineToRelative(31.9f, 49.3f)
			lineToRelative(-0.3f, 6.2f)
			lineToRelative(-0.1f, 0.1f)
			lineToRelative(0f, 0.5f)
			lineToRelative(-6.6f, 125.3f)
			lineToRelative(8.8f, 0f)
			lineToRelative(1.1f, -64.7f)
			lineToRelative(31.8f, -49.2f)
			lineToRelative(-31.7f, 44.3f)
			close()
		}
		path(fill = SolidColor(Color(0xFF3F3D56))) {
			moveTo(0f, 644.4f)
			arcToRelative(207f, 28f, 0f, isMoreThanHalf = true, isPositiveArc = false, 414f, 0f)
			arcToRelative(207f, 28f, 0f, isMoreThanHalf = true, isPositiveArc = false, -414f, 0f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(45f, 644.4f)
			arcToRelative(162f, 21.9f, 0f, isMoreThanHalf = true, isPositiveArc = false, 324f, 0f)
			arcToRelative(162f, 21.9f, 0f, isMoreThanHalf = true, isPositiveArc = false, -324f, 0f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(177.7f, 201.8f)
			moveToRelative(-44f, 0f)
			arcToRelative(44f, 44f, 0f, isMoreThanHalf = true, isPositiveArc = true, 87.9f, 0f)
			arcToRelative(44f, 44f, 0f, isMoreThanHalf = true, isPositiveArc = true, -87.9f, 0f)
		}
		path(fill = SolidColor(Color(0xFFFFB9B9))) {
			moveTo(189.7f, 216.6f)
			moveToRelative(-24.6f, 0f)
			arcToRelative(24.6f, 24.6f, 0f, isMoreThanHalf = true, isPositiveArc = true, 49.2f, 0f)
			arcToRelative(24.6f, 24.6f, 0f, isMoreThanHalf = true, isPositiveArc = true, -49.2f, 0f)
		}
		path(fill = SolidColor(Color(0xFFFFB9B9))) {
			moveTo(176f, 229.5f)
			reflectiveCurveToRelative(6.3f, 15f, -3.3f, 19.2f)
			reflectiveCurveToRelative(-5.8f, 28.8f, -5.8f, 28.8f)
			lineTo(204.3f, 269.5f)
			reflectiveCurveToRelative(0f, -10.4f, -7.5f, -15f)
			reflectiveCurveTo(206.8f, 229.5f, 206.8f, 229.5f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB9B9))) {
			moveTo(163.9f, 340.4f)
			lineToRelative(1.3f, 13.8f)
			lineToRelative(56.3f, 9.6f)
			lineToRelative(-5.4f, -25.4f)
			lineToRelative(-52.1f, 2.1f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB9B9))) {
			moveTo(163f, 451f)
			lineToRelative(2.1f, 58.4f)
			reflectiveCurveToRelative(-10f, 23.8f, -2.1f, 67.6f)
			reflectiveCurveToRelative(2.1f, 55.9f, 2.1f, 55.9f)
			lineToRelative(28.4f, 3.8f)
			lineTo(191.8f, 620.3f)
			lineToRelative(-6.7f, -14.2f)
			lineToRelative(2.9f, -80.9f)
			reflectiveCurveToRelative(12.9f, -30f, 13.3f, -35.9f)
			reflectiveCurveToRelative(-0.8f, -48.4f, -0.8f, -48.4f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(163f, 451f)
			lineToRelative(2.1f, 58.4f)
			reflectiveCurveToRelative(-10f, 23.8f, -2.1f, 67.6f)
			reflectiveCurveToRelative(2.1f, 55.9f, 2.1f, 55.9f)
			lineToRelative(28.4f, 3.8f)
			lineTo(191.8f, 620.3f)
			lineToRelative(-6.7f, -14.2f)
			lineToRelative(2.9f, -80.9f)
			reflectiveCurveToRelative(12.9f, -30f, 13.3f, -35.9f)
			reflectiveCurveToRelative(-0.8f, -48.4f, -0.8f, -48.4f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(171.8f, 612.8f)
			reflectiveCurveToRelative(0f, -5.4f, -2.5f, -5.8f)
			reflectiveCurveToRelative(-5.8f, -1.7f, -5.8f, 0f)
			reflectiveCurveToRelative(-0.4f, 13.3f, -1.3f, 15f)
			reflectiveCurveToRelative(-2.5f, 7.9f, -1.3f, 10.4f)
			curveToRelative(0f, 0f, -3.8f, 9.6f, -0.8f, 10.4f)
			reflectiveCurveToRelative(19.6f, 0.8f, 19.6f, 0.8f)
			lineToRelative(0.8f, -4.6f)
			reflectiveCurveToRelative(37.1f, 11.7f, 45.1f, 1.3f)
			curveToRelative(0f, 0f, 3.8f, -7.5f, -2.9f, -9.2f)
			curveToRelative(0f, 0f, -15f, 0.4f, -20.4f, -5.8f)
			reflectiveCurveTo(186.4f, 604.5f, 186.4f, 604.5f)
			reflectiveCurveToRelative(-8.3f, 1.3f, -7.5f, 6.7f)
			verticalLineToRelative(1.7f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB9B9))) {
			moveTo(186f, 452.2f)
			reflectiveCurveToRelative(4.2f, 29.2f, 18.8f, 50.5f)
			curveToRelative(0f, 0f, 7.5f, 17.5f, 6.3f, 22.9f)
			reflectiveCurveToRelative(-1.3f, 35f, 11.3f, 61.3f)
			lineToRelative(11.7f, 38.4f)
			lineToRelative(5.8f, 13.8f)
			lineToRelative(10f, 4.2f)
			lineToRelative(16.3f, 1.3f)
			lineToRelative(-9.2f, -19.2f)
			lineToRelative(-4.6f, -10f)
			reflectiveCurveToRelative(-8.3f, -17.1f, -8.3f, -32.1f)
			reflectiveCurveToRelative(-5.4f, -50.5f, -5.4f, -50.5f)
			reflectiveCurveToRelative(0f, -80.1f, -8.8f, -85.1f)
			reflectiveCurveTo(186f, 452.2f, 186f, 452.2f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(167.2f, 350f)
			lineToRelative(-4.2f, -0.8f)
			lineTo(163f, 368f)
			reflectiveCurveToRelative(-1.7f, 14.6f, -5.4f, 17.5f)
			reflectiveCurveTo(139.3f, 446f, 144.3f, 449.3f)
			reflectiveCurveToRelative(72.2f, 20.9f, 88.4f, 2.9f)
			lineToRelative(-11.3f, -54.6f)
			reflectiveCurveToRelative(0.8f, -37.5f, 0f, -37.5f)
			reflectiveCurveTo(167.2f, 350f, 167.2f, 350f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(206.8f, 265.8f)
			reflectiveCurveToRelative(-20.9f, -14.2f, -41.7f, 4.2f)
			curveToRelative(0f, 0f, -7.5f, 30.9f, -8.8f, 38.4f)
			reflectiveCurveToRelative(-9.2f, 38.4f, -3.3f, 41.3f)
			reflectiveCurveToRelative(5.8f, -7.1f, 20.9f, -5f)
			reflectiveCurveToRelative(47.1f, 1.3f, 47.1f, -1.3f)
			reflectiveCurveToRelative(-7.5f, -22.5f, -4.6f, -25.9f)
			reflectiveCurveToRelative(7.9f, -19.2f, 2.9f, -27.1f)
			reflectiveCurveTo(206.8f, 265.8f, 206.8f, 265.8f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB9B9))) {
			moveTo(179.7f, 350f)
			lineToRelative(41.7f, 19.6f)
			reflectiveCurveToRelative(16.7f, 21.3f, 20.4f, 9.2f)
			reflectiveCurveToRelative(-12.5f, -20.9f, -12.5f, -20.9f)
			lineTo(201.4f, 342.1f)
			lineToRelative(-21.7f, 7.1f)
			close()
		}
		path(
			fill = SolidColor(Color.Black),
			fillAlpha = 0.1f,
			strokeAlpha = 0.1f
		) {
			moveTo(182.6f, 269.9f)
			reflectiveCurveToRelative(-23.8f, 73.4f, -2.1f, 85.1f)
			reflectiveCurveToRelative(25f, -10.4f, 25f, -10.4f)
			reflectiveCurveToRelative(-14.6f, -9.2f, -10f, -20.4f)
			reflectiveCurveToRelative(8.8f, -54.2f, 8.8f, -54.2f)
			close()
		}
		path(fill = SolidColor(accentColor)) {
			moveTo(181.4f, 267.4f)
			reflectiveCurveToRelative(-23.8f, 73.4f, -2.1f, 85.1f)
			reflectiveCurveTo(204.3f, 342.1f, 204.3f, 342.1f)
			reflectiveCurveToRelative(-14.6f, -9.2f, -10f, -20.4f)
			reflectiveCurveToRelative(8.8f, -54.2f, 8.8f, -54.2f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(238.7f, 621.4f)
			reflectiveCurveToRelative(-0.4f, -5.7f, -3.1f, -5.9f)
			reflectiveCurveToRelative(-6.2f, -1.3f, -6.1f, 0.5f)
			reflectiveCurveToRelative(0.6f, 14f, -0.2f, 15.8f)
			reflectiveCurveToRelative(-2f, 8.5f, -0.5f, 11f)
			curveToRelative(0f, 0f, -3.2f, 10.3f, -0.1f, 11f)
			reflectiveCurveToRelative(20.6f, -0.6f, 20.6f, -0.6f)
			lineToRelative(0.5f, -4.9f)
			reflectiveCurveToRelative(39.8f, 9.4f, 47.3f, -2.2f)
			curveToRelative(0f, 0f, 3.4f, -8.2f, -3.8f, -9.4f)
			curveToRelative(0f, 0f, -15.7f, 1.6f, -21.9f, -4.5f)
			reflectiveCurveToRelative(-18.2f, -20.6f, -18.2f, -20.6f)
			reflectiveCurveToRelative(-8.6f, 2f, -7.3f, 7.6f)
			lineToRelative(0.1f, 1.7f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(160f, 200.6f)
			arcToRelative(29.7f, 15.8f, 0f, isMoreThanHalf = true, isPositiveArc = false, 59.4f, 0f)
			arcToRelative(29.7f, 15.8f, 0f, isMoreThanHalf = true, isPositiveArc = false, -59.4f, 0f)
			close()
		}
		path(fill = SolidColor(Color(0xFFFFB9B9))) {
			moveTo(160.8f, 220.7f)
			arcToRelative(4.2f, 6.6f, 0f, isMoreThanHalf = true, isPositiveArc = false, 8.5f, 0f)
			arcToRelative(4.2f, 6.6f, 0f, isMoreThanHalf = true, isPositiveArc = false, -8.5f, 0f)
			close()
		}
		path(fill = SolidColor(Color(0xFF2F2E41))) {
			moveTo(150.7f, 157.8f)
			moveToRelative(-20.8f, 0f)
			arcToRelative(20.8f, 20.8f, 0f, isMoreThanHalf = true, isPositiveArc = true, 41.7f, 0f)
			arcToRelative(20.8f, 20.8f, 0f, isMoreThanHalf = true, isPositiveArc = true, -41.7f, 0f)
		}
	}.build()

	return _AlleysListEmptyState!!
}

@Suppress("ObjectPropertyName")
private var _AlleysListEmptyState: ImageVector? = null
