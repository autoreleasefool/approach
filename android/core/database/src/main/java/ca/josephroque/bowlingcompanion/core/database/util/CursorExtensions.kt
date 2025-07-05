package ca.josephroque.bowlingcompanion.core.database.util

import android.database.Cursor
import androidx.annotation.IntRange
import java.nio.ByteBuffer
import java.util.UUID

fun Cursor.getUUID(@IntRange(from = 0) columnIndex: Int): UUID {
	val blob = getBlob(columnIndex)
	val buffer = ByteBuffer.wrap(blob)
	val mostSigBits = buffer.getLong()
	val leastSigBits = buffer.getLong()
	return UUID(mostSigBits, leastSigBits)
}

fun UUID.toBlob(): ByteArray {
	val buffer = ByteBuffer.allocate(16)
	buffer.putLong(mostSignificantBits)
	buffer.putLong(leastSignificantBits)
	return buffer.array()
}
