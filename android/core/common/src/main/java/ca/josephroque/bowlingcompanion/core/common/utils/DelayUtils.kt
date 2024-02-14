package ca.josephroque.bowlingcompanion.core.common.utils

import kotlinx.coroutines.delay

suspend inline fun runWithMinimumDuration(
	minimumDuration: Long,
	crossinline block: suspend () -> Unit,
) {
	val startTime = System.currentTimeMillis()
	block()
	val endTime = System.currentTimeMillis()
	val duration = endTime - startTime
	if (duration < minimumDuration) {
		delay(minimumDuration - duration)
	}
}
