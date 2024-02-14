package ca.josephroque.bowlingcompanion.core.designsystem.animation

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun Wiggle(
	modifier: Modifier = Modifier,
	infiniteTransition: InfiniteTransition = rememberInfiniteTransition(),
	content: @Composable (Modifier) -> Unit,
) {
	val randomizer = remember { Random.nextFloat() }

	val yPosition by infiniteTransition.animateFloat(
		initialValue = -randomizer,
		targetValue = randomizer,
		animationSpec = infiniteRepeatable(
			animation = tween(
				durationMillis = 400 + (randomizer * 100).toInt(),
				easing = FastOutSlowInEasing,
			),
			repeatMode = RepeatMode.Reverse,
		),
		label = "yPosition",
	)

	val xPosition by infiniteTransition.animateFloat(
		initialValue = -3f,
		targetValue = 3f,
		animationSpec = infiniteRepeatable(
			animation = tween(
				durationMillis = 200 + (randomizer * 50).toInt(),
				easing = FastOutLinearInEasing,
			),
			repeatMode = RepeatMode.Reverse,
		),
		label = "xPosition",
	)

	val angle by infiniteTransition.animateFloat(
		initialValue = randomizer * -4,
		targetValue = randomizer * 4,
		animationSpec = infiniteRepeatable(
			animation = tween(
				durationMillis = 150 + (randomizer * 100).toInt(),
				easing = LinearEasing,
			),
			repeatMode = RepeatMode.Reverse,
		),
		label = "angle",
	)

	content(
		modifier
			.offset(x = xPosition.dp, y = yPosition.dp)
			.graphicsLayer {
				rotationZ = angle
			},
	)
}
