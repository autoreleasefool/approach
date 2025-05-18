package ca.josephroque.bowlingcompanion.core.common.utils

import kotlinx.coroutines.delay

suspend inline fun <T> runWithMinimumDuration(minimumDuration: Long, crossinline block: suspend () -> T): T {
	val startTime = System.currentTimeMillis()
	val result = block()
	val endTime = System.currentTimeMillis()
	val duration = endTime - startTime
	if (duration < minimumDuration) {
		delay(minimumDuration - duration)
	}
	return result
}
