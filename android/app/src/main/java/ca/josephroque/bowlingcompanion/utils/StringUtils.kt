package ca.josephroque.bowlingcompanion.utils

import java.text.DecimalFormat

fun Double?.formatAsAverage(): String = if (this == null) {
	""
} else {
	val df = DecimalFormat("#.#")
	df.format(this)
}