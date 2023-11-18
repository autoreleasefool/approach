package ca.josephroque.bowlingcompanion.core.database.util

import android.database.Cursor

data class ReadableCursor(
	private val cursor: Cursor
) {
	val columnNames: Array<String> = cursor.columnNames
	val columnCount: Int = cursor.columnCount

	fun getColumnIndex(columnName: String): Int =
		cursor.getColumnIndex(columnName)

	fun getColumnIndexOrThrow(columnName: String): Int =
		cursor.getColumnIndexOrThrow(columnName)

	fun getColumnName(columnIndex: Int): String =
		cursor.getColumnName(columnIndex)

	fun getBlob(columnIndex: Int): ByteArray =
		cursor.getBlob(columnIndex)

	fun getString(columnIndex: Int): String =
		cursor.getString(columnIndex)

	fun getShort(columnIndex: Int): Short =
		cursor.getShort(columnIndex)

	fun getInt(columnIndex: Int): Int =
		cursor.getInt(columnIndex)

	fun getLong(columnIndex: Int): Long =
		cursor.getLong(columnIndex)

	fun getFloat(columnIndex: Int): Float =
		cursor.getFloat(columnIndex)

	fun getDouble(columnIndex: Int): Double =
		cursor.getDouble(columnIndex)
}