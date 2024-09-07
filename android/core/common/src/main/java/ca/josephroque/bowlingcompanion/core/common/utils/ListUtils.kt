package ca.josephroque.bowlingcompanion.core.common.utils

fun List<Int>.range(): IntRange {
	val min = minOrNull() ?: 0
	val max = maxOrNull() ?: 0
	return min..max
}
