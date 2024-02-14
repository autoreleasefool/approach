package ca.josephroque.bowlingcompanion.core.model.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.Avatar

@Composable
fun AvatarImage(avatar: Avatar, modifier: Modifier = Modifier) {
	val initials = remember(avatar.label) { avatar.label.initials() }
	val textColor = remember(avatar.primaryColor, avatar.secondaryColor) {
		avatar.primaryColor.toComposeColor().averagedWith(avatar.secondaryColor.toComposeColor())
			.preferredForegroundColor()
	}

	val brush = Brush.linearGradient(
		listOf(
			avatar.primaryColor.toComposeColor(),
			avatar.secondaryColor.toComposeColor(),
		),
	)

	val textMeasurer = rememberTextMeasurer()

	Canvas(
		modifier = modifier,
		onDraw = {
			drawCircle(brush)

			val textStyle = TextStyle(
				color = textColor,
				fontSize = (minOf(size.width, size.height) / 3).toSp(),
				fontWeight = FontWeight.Bold,
			)
			val textLayoutResult = textMeasurer.measure(
				text = AnnotatedString(initials),
				style = textStyle,
			)
			val textSize = textLayoutResult.size
			drawText(
				textMeasurer = textMeasurer,
				text = initials,
				style = textStyle,
				topLeft = Offset(
					(size.width - textSize.width) / 2f,
					(size.height - textSize.height) / 2f,
				),
			)
		},
	)
}

fun Avatar.RGB.toComposeColor(): Color = Color(red, green, blue)

fun Avatar.RGB.Companion.randomPastel(): Avatar.RGB = Avatar.RGB(
	red = (127..255).random(),
	green = (127..255).random(),
	blue = (127..255).random(),
)

fun Color.averagedWith(other: Color): Color = Color(
	(red + other.red) / 2f,
	(green + other.green) / 2f,
	(blue + other.blue) / 2f,
)

// For determining the best color to use for foreground text when this color is the background
// Source: https://stackoverflow.com/a/3943023
fun Color.intensity(): Int =
	(red * 0.299f * 255f + green * 0.587f * 255f + blue * 0.114f * 255f).toInt()

fun Color.preferredForegroundColor(): Color = if (intensity() > 186) Color.Black else Color.White

fun String.words(): List<String> = split("[^a-zA-Z\\d]".toRegex()).filter { it.isNotBlank() }

fun String.initials(): String {
	val words = words()
	return if (words.size > 1) {
		words[0].first().toString() + words[1].first().toString()
	} else if (words.size == 1) {
		words[0].take(2)
	} else {
		""
	}
}

@Preview
@Composable
private fun AvatarImagePreview() {
	Surface {
		AvatarImage(
			avatar = Avatar.default(),
			modifier = Modifier.size(80.dp),
		)
	}
}
