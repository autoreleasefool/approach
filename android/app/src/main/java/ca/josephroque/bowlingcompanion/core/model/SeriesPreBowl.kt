package ca.josephroque.bowlingcompanion.core.model

enum class SeriesPreBowl {
	REGULAR,
	PRE_BOWL,
}

fun String?.asSeriesPreBowl() = when (this) {
	null -> null
	else -> SeriesPreBowl.values()
		.firstOrNull { it.name == this }
}