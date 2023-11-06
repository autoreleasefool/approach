package ca.josephroque.bowlingcompanion.core.model.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import ca.josephroque.bowlingcompanion.core.model.Avatar

@Composable
fun AvatarImage(
	avatar: Avatar,
	modifier: Modifier = Modifier,
) {
	val brush = Brush.linearGradient(
		listOf(
			avatar.primaryColor.toComposeColor(),
			avatar.secondaryColor.toComposeColor(),
		)
	)
	Canvas(
		modifier = modifier,
		onDraw = {
			drawCircle(brush)
		}
	)
}

fun Avatar.RGB.toComposeColor(): Color =
	Color(red, green, blue)