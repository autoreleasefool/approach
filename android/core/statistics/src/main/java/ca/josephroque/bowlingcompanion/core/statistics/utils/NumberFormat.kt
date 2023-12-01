package ca.josephroque.bowlingcompanion.core.statistics.utils

import java.text.DecimalFormat

val Double.formatAsAverage: String
	get() = if (this == 0.0) "-" else DecimalFormat("0.#").format(this)

val Double.formatAsPercentage: String
	get() = DecimalFormat("0.#%").format(this)