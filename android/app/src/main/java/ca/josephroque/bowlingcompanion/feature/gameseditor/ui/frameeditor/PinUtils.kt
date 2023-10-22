package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.frameeditor

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.model.Pin

@Composable
fun Pin.contentDescription() = when (this) {
	Pin.LEFT_TWO_PIN -> stringResource(R.string.cd_pin_left_two_pin)
	Pin.LEFT_THREE_PIN -> stringResource(R.string.cd_pin_left_three_pin)
	Pin.HEAD_PIN -> stringResource(R.string.cd_pin_head_pin)
	Pin.RIGHT_THREE_PIN -> stringResource(R.string.cd_pin_right_three_pin)
	Pin.RIGHT_TWO_PIN -> stringResource(R.string.cd_pin_right_two_pin)
}

fun <T> MutableSet<T>.toggle(element: T): MutableSet<T> {
	if (this.contains(element)) {
		this.remove(element)
	} else {
		this.add(element)
	}

	return this
}