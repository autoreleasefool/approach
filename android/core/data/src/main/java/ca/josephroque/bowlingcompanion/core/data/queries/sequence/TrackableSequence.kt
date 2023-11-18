package ca.josephroque.bowlingcompanion.core.data.queries.sequence

import ca.josephroque.bowlingcompanion.core.database.dao.StatisticsDao
import ca.josephroque.bowlingcompanion.core.database.util.ReadableCursor

abstract class TrackableSequence<T>(
	private val statisticsDao: StatisticsDao,
) {
	fun build(): Sequence<T> = sequence {
		val columnsStatement = buildColumnsStatement()
		val tablesStatement = buildTablesStatement()
		val whereStatement = buildWhereStatement()
		val whereArgs = buildWhereArgs()
		val groupByStatement = buildGroupByStatement()
		val orderByStatement = buildOrderByStatement()

		val unorderedWhereQuery = """
			$columnsStatement
			$tablesStatement
			$whereStatement
			$groupByStatement
			$orderByStatement
		"""
			.trimIndent()

		val whereArgIndices = whereArgs.keys
			.associateWithTo(mutableMapOf()) { index -> unorderedWhereQuery.indexOf(index) }

		val query = unorderedWhereQuery.replace("\\?\\w+".toRegex(), "?")
		val orderedWhereArgs = whereArgs.keys
			.sortedBy { whereArgIndices[it] }

		statisticsDao.getTrackableStatistics(query, orderedWhereArgs).use { cursor ->
			val readableCursor = ReadableCursor(cursor)
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast) {
					val item = parseCursor(readableCursor)
					yield(item)
					cursor.moveToNext()
				}
			}
		}
	}

	abstract fun buildColumnsStatement(): String
	abstract fun buildTablesStatement(): String
	abstract fun buildWhereStatement(): String
	abstract fun buildWhereArgs(): Map<String, String>
	abstract fun buildGroupByStatement(): String
	abstract fun buildOrderByStatement(): String
	abstract fun parseCursor(cursor: ReadableCursor): T
}