package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.model.Pin

@Composable
fun FrameEditor(
	frameEditorState: FrameEditorUiState,
	onDownedPinsChanged: (Set<Pin>) -> Unit,
	modifier: Modifier = Modifier,
) {
	when (frameEditorState) {
		FrameEditorUiState.Loading -> Unit
		is FrameEditorUiState.Edit -> FrameEditor(
			state = frameEditorState,
			onDownedPinsChanged = onDownedPinsChanged,
			modifier = modifier,
		)
	}
}

@Composable
private fun FrameEditor(
	state: FrameEditorUiState.Edit,
	onDownedPinsChanged: (Set<Pin>) -> Unit,
	modifier: Modifier = Modifier,
) {
	var downedPins by remember(state.downedPins) { mutableStateOf(state.downedPins) }
	var maxX by remember { mutableFloatStateOf(0f) }
	var isDragging by remember { mutableStateOf(false) }
	var isKnockingDownPins by remember { mutableStateOf(false) }
	var toggledPins by remember { mutableStateOf(setOf<Pin>()) }

	LaunchedEffect(isDragging) {
		if (!isDragging && downedPins != state.downedPins) {
			onDownedPinsChanged(downedPins.toSet())
		}
	}

	Box(modifier = modifier
		.fillMaxSize()
		.onSizeChanged { maxX = it.width.toFloat() },
	) {
		Row(
			modifier = Modifier
				.fillMaxSize()
				.pointerInput(state.downedPins) {
					awaitEachGesture {
						awaitFirstDown()
						do {
							val event: PointerEvent = awaitPointerEvent()
							event.changes.forEach {
								val x = it.position.x.coerceIn(0f, maxX - 1)
								val pinIndex = (x / (maxX / 5)).toInt()
								val pin = Pin.values()[pinIndex]

								if (!state.lockedPins.contains(pin)) {
									if (!isDragging) {
										isDragging = true
										isKnockingDownPins = !downedPins.contains(pin)
									}

									if (!toggledPins.contains(pin) && isKnockingDownPins != downedPins.contains(pin)) {
										toggledPins = toggledPins
											.toMutableSet()
											.apply { add(pin) }
										downedPins = downedPins.toMutableSet().toggle(pin)
									}
								}
							}
						} while (event.changes.any { it.pressed })

						isDragging = false
						toggledPins = setOf()
					}
				}
		) {
			Pin.values().forEach {
				val isPinLocked = state.lockedPins.contains(it)
				val isPinDown = isPinLocked || downedPins.contains(it)

				Image(
					painter = painterResource(if (isPinDown) R.drawable.pin_down else R.drawable.pin),
					contentDescription = it.contentDescription(),
					modifier = Modifier
						.padding(horizontal = 4.dp)
						.weight(1f)
						.alpha(if (isPinLocked) 0.25f else 1f),
				)
			}
		}
	}
}

sealed interface FrameEditorUiState {
	data object Loading: FrameEditorUiState

	data class Edit(
		val lockedPins: Set<Pin>,
		val downedPins: Set<Pin>,
	): FrameEditorUiState
}

@Preview
@Composable
private fun FrameEditorPreview() {
	var state by remember {
		mutableStateOf(
			FrameEditorUiState.Edit(
				lockedPins = setOf(Pin.LEFT_TWO_PIN),
				downedPins = setOf(Pin.LEFT_THREE_PIN),
			)
		)
	}

	Surface {
		FrameEditor(
			state = state,
			onDownedPinsChanged = { state = state.copy(downedPins = it) },
			modifier = Modifier.size(300.dp),
		)
	}
}

