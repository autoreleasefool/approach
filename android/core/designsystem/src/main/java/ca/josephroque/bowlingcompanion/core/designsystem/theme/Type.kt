package ca.josephroque.bowlingcompanion.core.designsystem.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import ca.josephroque.bowlingcompanion.core.designsystem.R

private val Montserrat = FontFamily(
	Font(R.font.montserrat_regular, FontWeight.Normal),
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
		fontWeight = FontWeight.Bold,
		fontSize = 57.sp,
		lineHeight = 64.sp,
		letterSpacing = 0.sp
	),
	displayMedium = TextStyle(
		fontFamily = Montserrat,
		fontWeight = FontWeight.Bold,
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
		fontWeight = FontWeight.Bold,
		fontSize = 32.sp,
		lineHeight = 40.sp,
		letterSpacing = 0.sp
	),
	headlineMedium = TextStyle(
		fontFamily = Montserrat,
		fontWeight = FontWeight.Bold,
		fontSize = 28.sp,
		lineHeight = 36.sp,
		letterSpacing = 0.sp
	),
	headlineSmall = TextStyle(
		fontFamily = Montserrat,
		fontWeight = FontWeight.Normal,
		fontSize = 24.sp,
		lineHeight = 32.sp,
		letterSpacing = 0.sp
	),
	titleLarge = TextStyle(
		fontFamily = Montserrat,
		fontWeight = FontWeight.Bold,
		fontSize = 22.sp,
		lineHeight = 28.sp,
		letterSpacing = 0.sp
	),
	titleMedium = TextStyle(
		fontFamily = Montserrat,
		fontWeight = FontWeight.Bold,
		fontSize = 16.sp,
		lineHeight = 24.sp,
		letterSpacing = 0.15.sp
	),
	titleSmall = TextStyle(
		fontFamily = Montserrat,
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
		fontWeight = FontWeight.Normal,
		fontSize = 12.sp,
		lineHeight = 16.sp,
		letterSpacing = 0.4.sp
	),
	labelLarge = TextStyle(
		fontFamily = Montserrat,
		fontWeight = FontWeight.Bold,
		fontSize = 14.sp,
		lineHeight = 20.sp,
		letterSpacing = 0.1.sp
	),
	labelMedium = TextStyle(
		fontFamily = Montserrat,
		fontWeight = FontWeight.Bold,
		fontSize = 12.sp,
		lineHeight = 16.sp,
		letterSpacing = 0.5.sp
	),
	labelSmall = TextStyle(
		fontFamily = Montserrat,
		fontWeight = FontWeight.Bold,
		fontSize = 11.sp,
		lineHeight = 16.sp,
		letterSpacing = 0.5.sp
	)
)

@Preview
@Composable
private fun TypePreview() {
	Surface {
		Column {
			Text(text = "Display Large", style = Typography.displayLarge)
			Text(text = "Display Medium", style = Typography.displayMedium)
			Text(text = "Display Small", style = Typography.displaySmall)

			Text(text = "Headline Large", style = Typography.headlineLarge)
			Text(text = "Headline Medium", style = Typography.headlineMedium)
			Text(text = "Headline Small", style = Typography.headlineSmall)

			Text(text = "Title Large", style = Typography.titleLarge)
			Text(text = "Title Medium", style = Typography.titleMedium)
			Text(text = "Title Small", style = Typography.titleSmall)

			Text(text = "Body Large", style = Typography.bodyLarge)
			Text(text = "Body Medium", style = Typography.bodyMedium)
			Text(text = "Body Small", style = Typography.bodySmall)

			Text(text = "Label Large", style = Typography.labelLarge)
			Text(text = "Label Medium", style = Typography.labelMedium)
			Text(text = "Label Small", style = Typography.labelSmall)
		}
	}
}