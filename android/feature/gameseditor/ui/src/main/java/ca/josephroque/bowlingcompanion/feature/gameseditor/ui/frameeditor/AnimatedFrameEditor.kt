package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedFrameEditor(
	state: FrameEditorUiState,
	onAction: (FrameEditorUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	val currentGameIndex = remember { mutableIntStateOf(-1) }
	val frameEditorWidth = remember { mutableFloatStateOf(0f) }
	val offsetX = remember { Animatable(0f) }

	LaunchedEffect(state.gameIndex) {
		if (currentGameIndex.intValue == -1) {
			currentGameIndex.intValue = state.gameIndex
			return@LaunchedEffect
		}

		val isGameIncrementing = currentGameIndex.intValue < state.gameIndex
		offsetX.animateTo(
			targetValue = frameEditorWidth.floatValue * if (isGameIncrementing) -1 else 1,
		)

		offsetX.animateTo(
			targetValue = frameEditorWidth.floatValue * if (isGameIncrementing) 1 else -1,
			animationSpec = tween(durationMillis = 0),
		)

		offsetX.animateTo(
			targetValue = 0f,
		)

		currentGameIndex.intValue = state.gameIndex
	}

	FrameEditor(
		state = state,
		onAction = onAction,
		modifier = modifier
			.offset {
				IntOffset(
					x = offsetX.value.toInt(),
					y = 0,
				)
			}
			.onSizeChanged { frameEditorWidth.floatValue = it.width.toFloat() + 32.dp.value },
	)
}