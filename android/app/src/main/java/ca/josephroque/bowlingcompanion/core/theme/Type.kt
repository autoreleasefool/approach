package ca.josephroque.bowlingcompanion.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ca.josephroque.bowlingcompanion.R

private val Montserrat = FontFamily(
	Font(R.font.montserrat_light, FontWeight.Light),
	Font(R.font.montserrat_regular, FontWeight.Normal),
	Font(R.font.montserrat_medium, FontWeight.Medium),
	Font(R.font.montserrat_semibold, FontWeight.SemiBold),
)

private val Lato = FontFamily(
	Font(R.font.lato_light, FontWeight.Light),
	Font(R.font.lato_regular, FontWeight.Normal),
	Font(R.font.lato_bold, FontWeight.Bold),
	Font(R.font.lato_black, FontWeight.Black),
)

val Typography = Typography(
	displayLarge = TextStyle(
		fontFamily = Montserrat,
		fontWeight = FontWeight.Light,
		fontSize = 57.sp,
		lineHeight = 64.sp,
		letterSpacing = 0.sp
	),
	displayMedium = TextStyle(
		fontFamily = Montserrat,
		fontWeight = FontWeight.Light,
		fontSize = 45.sp,
		lineHeight = 52.sp,
		letterSpacing = 0.sp
	),
	displaySmall = TextStyle(
		fontFamily = Montserrat,
		fontWeight = FontWeight.Normal,
		fontSize = 36.sp,
		lineHeight = 44.sp,
		letterSpacing = 0.sp
	),
	headlineLarge = TextStyle(
		fontFamily = Montserrat,
		fontWeight = FontWeight.SemiBold,
		fontSize = 32.sp,
		lineHeight = 40.sp,
		letterSpacing = 0.sp
	),
	headlineMedium = TextStyle(
		fontFamily = Montserrat,
		fontWeight = FontWeight.SemiBold,
		fontSize = 28.sp,
		lineHeight = 36.sp,
		letterSpacing = 0.sp
	),
	headlineSmall = TextStyle(
		fontFamily = Montserrat,
		fontWeight = FontWeight.SemiBold,
		fontSize = 24.sp,
		lineHeight = 32.sp,
		letterSpacing = 0.sp
	),
	titleLarge = TextStyle(
		fontFamily = Montserrat,
		fontWeight = FontWeight.SemiBold,
		fontSize = 22.sp,
		lineHeight = 28.sp,
		letterSpacing = 0.sp
	),
	titleMedium = TextStyle(
		fontFamily = Montserrat,
		fontWeight = FontWeight.SemiBold,
		fontSize = 16.sp,
		lineHeight = 24.sp,
		letterSpacing = 0.15.sp
	),
	titleSmall = TextStyle(
		fontFamily = Lato,
		fontWeight = FontWeight.Bold,
		fontSize = 14.sp,
		lineHeight = 20.sp,
		letterSpacing = 0.1.sp
	),
	bodyLarge = TextStyle(
		fontFamily = Lato,
		fontWeight = FontWeight.Normal,
		fontSize = 16.sp,
		lineHeight = 24.sp,
		letterSpacing = 0.15.sp
	),
	bodyMedium = TextStyle(
		fontFamily = Lato,
		fontWeight = FontWeight.Normal,
		fontSize = 14.sp,
		lineHeight = 20.sp,
		letterSpacing = 0.25.sp
	),
	bodySmall = TextStyle(
		fontFamily = Lato,
		fontWeight = FontWeight.Bold,
		fontSize = 12.sp,
		lineHeight = 16.sp,
		letterSpacing = 0.4.sp
	),
	labelLarge = TextStyle(
		fontFamily = Lato,
		fontWeight = FontWeight.Bold,
		fontSize = 14.sp,
		lineHeight = 20.sp,
		letterSpacing = 0.1.sp
	),
	labelMedium = TextStyle(
		fontFamily = Lato,
		fontWeight = FontWeight.Bold,
		fontSize = 12.sp,
		lineHeight = 16.sp,
		letterSpacing = 0.5.sp
	),
	labelSmall = TextStyle(
		fontFamily = Lato,
		fontWeight = FontWeight.Bold,
		fontSize = 11.sp,
		lineHeight = 16.sp,
		letterSpacing = 0.5.sp
	)
)