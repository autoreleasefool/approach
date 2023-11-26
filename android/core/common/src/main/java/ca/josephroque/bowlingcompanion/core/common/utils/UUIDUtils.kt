package ca.josephroque.bowlingcompanion.core.common.utils

import java.nio.ByteBuffer
import java.util.UUID

fun UUID.asBytes(): ByteArray {
	val b = ByteBuffer.wrap(ByteArray(16))
	b.putLong(mostSignificantBits)
	b.putLong(leastSignificantBits)
	return b.array()
}