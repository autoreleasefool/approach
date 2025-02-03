package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.Pin

@Composable
fun FrameEditor(
	state: FrameEditorUiState,
	onAction: (FrameEditorUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	var downedPins by remember(state.downedPins) { mutableStateOf(state.downedPins) }
	var maxX by remember { mutableFloatStateOf(1.0f) }
	var isDragging by remember { mutableStateOf(false) }
	var isKnockingDownPins by remember { mutableStateOf(false) }
	var toggledPins by remember { mutableStateOf(setOf<Pin>()) }

	BoxWithConstraints(
		modifier = modifier
			.fillMaxWidth()
			.onSizeChanged { maxX = it.width.toFloat().coerceAtLeast(1.0f) },
	) {
		val maxWidth = this.maxWidth
		val maxHeight = this.maxHeight
		val ratio = maxWidth / maxHeight
		val isWide = ratio > 2.12

		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.fillMaxWidth()
				.widthIn(max = if (isWide) maxHeight * 2.12f else maxWidth)
				.pointerInput(state) {
					awaitEachGesture {
						awaitFirstDown()

						onAction(FrameEditorUiAction.FrameEditorInteractionStarted)
						if (!state.isEnabled) return@awaitEachGesture

						do {
							val event: PointerEvent = awaitPointerEvent()
							event.changes.forEach {
								val x = it.position.x.coerceIn(0f, maxX - 1)
								val pinIndex = (x / (maxX / 5)).toInt()
								val pin = Pin.entries[pinIndex]

								if (!state.lockedPins.contains(pin)) {
									if (!isDragging) {
										isDragging = true
										isKnockingDownPins = !downedPins.contains(pin)
									}

									if (!toggledPins.contains(pin) && isKnockingDownPins != downedPins.contains(pin)) {
										toggledPins = toggledPins
											.toMutableSet()
											.apply { add(pin) }
										downedPins = downedPins
											.toMutableSet()
											.toggle(pin)
									}
								}
							}
						} while (event.changes.any { it.pressed })

						isDragging = false
						toggledPins = setOf()

						if (downedPins != state.downedPins) {
							onAction(FrameEditorUiAction.DownedPinsChanged(downedPins.toSet()))
						}
					}
				},
		) {
			Pin.entries.forEach {
				val isPinLocked = state.lockedPins.contains(it)
				val isPinDown = isPinLocked || downedPins.contains(it)

				Image(
					painter = painterResource(
						if (isPinDown) {
							ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.pin_down
						} else {
							ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.pin
						},
					),
					contentDescription = it.contentDescription(),
					modifier = Modifier
						.padding(horizontal = 4.dp)
						.weight(it.weight())
						.alpha(if (isPinLocked) 0.25f else 1f),
				)
			}
		}
	}
}

private fun Pin.weight(): Float = when (this) {
	Pin.LEFT_TWO_PIN, Pin.RIGHT_TWO_PIN -> 0.1855f
	Pin.LEFT_THREE_PIN, Pin.RIGHT_THREE_PIN -> 0.207f
	Pin.HEAD_PIN -> 0.215f
}

@Preview
@Composable
private fun FrameEditorPreview() {
	var state by remember {
		mutableStateOf(
			FrameEditorUiState(
				lockedPins = setOf(Pin.LEFT_TWO_PIN),
				downedPins = setOf(Pin.LEFT_THREE_PIN),
			),
		)
	}

	Surface {
		FrameEditor(
			state = state,
			onAction = {
				when (it) {
					FrameEditorUiAction.FrameEditorInteractionStarted,
					FrameEditorUiAction.AnimationFinished,
					FrameEditorUiAction.DragHintDismissed,
					-> Unit
					is FrameEditorUiAction.DownedPinsChanged -> state = state.copy(downedPins = it.downedPins)
				}
			},
			modifier = Modifier.size(300.dp),
		)
	}
}
