package ca.josephroque.bowlingcompanion.core.designsystem.modifiers

import android.annotation.SuppressLint
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput

@SuppressLint("ReturnFromAwaitPointerEventScope")
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
