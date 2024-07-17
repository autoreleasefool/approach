package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
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
	val frameEditorWidth = remember { mutableFloatStateOf(0f) }
	val offsetX = remember { Animatable(0f) }

	LaunchedEffect(state.nextAnimationDirection) {
		if (state.nextAnimationDirection == null) return@LaunchedEffect

		offsetX.animateTo(
			targetValue = frameEditorWidth.floatValue * when (state.nextAnimationDirection) {
				AnimationDirection.LEFT_TO_RIGHT -> 1
				AnimationDirection.RIGHT_TO_LEFT -> -1
			},
		)

		offsetX.animateTo(
			targetValue = frameEditorWidth.floatValue * when (state.nextAnimationDirection) {
				AnimationDirection.LEFT_TO_RIGHT -> -1
				AnimationDirection.RIGHT_TO_LEFT -> 1
			},
			animationSpec = tween(durationMillis = 0),
		)

		offsetX.animateTo(
			targetValue = 0f,
		)

		onAction(FrameEditorUiAction.AnimationFinished)
	}

	Box(modifier = modifier) {
		FrameEditor(
			state = state,
			onAction = onAction,
			modifier = Modifier
				.offset {
					IntOffset(
						x = offsetX.value.toInt(),
						y = 0,
					)
				}
				.onSizeChanged { frameEditorWidth.floatValue = it.width.toFloat() + 32.dp.value },
		)
	}
}
