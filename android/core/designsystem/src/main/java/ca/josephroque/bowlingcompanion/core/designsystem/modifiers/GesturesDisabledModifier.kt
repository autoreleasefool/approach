package ca.josephroque.bowlingcompanion.core.designsystem.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.disableGestures(disabled: Boolean) = if (disabled) {
	pointerInput(Unit) {
		awaitPointerEventScope {
			while (true) {
				val event = awaitPointerEvent(PointerEventPass.Initial)
				event.changes.forEach(PointerInputChange::consume)
			}
		}
	}
} else {
	this
}
