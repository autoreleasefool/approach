package ca.josephroque.bowlingcompanion.core.data.queries.sequence

import androidx.paging.PagingSource

abstract class TrackableSequence<Entity : Any, Model : Any> {
	companion object {
		private const val PAGE_SIZE = 100
	}

	suspend fun applySequence(apply: (Model) -> Unit) {
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
		""".trimIndent()

		val whereArgIndices = whereArgs.keys
			.associateWithTo(mutableMapOf()) { index -> unorderedWhereQuery.indexOf(index) }

		val query = unorderedWhereQuery.replace("\\?\\w+".toRegex(), "?")
		val orderedWhereArgs = whereArgs.keys
			.filter { whereArgIndices[it] != -1 }
			.sortedBy { whereArgIndices[it] }
			.map { whereArgs[it]!! }

		val pagingSource = getPagingSource(query, orderedWhereArgs)
		var result = pagingSource.load(
			PagingSource.LoadParams.Refresh(
				key = 0,
				loadSize = PAGE_SIZE,
				placeholdersEnabled = false,
			),
		)

		while (result is PagingSource.LoadResult.Page) {
			result.data
				.map(::mapEntityToModel)
				.forEach(apply)

			result = result.nextKey
				?.let {
					pagingSource.load(
						PagingSource.LoadParams.Append(
							key = it,
							loadSize = PAGE_SIZE,
							placeholdersEnabled = false,
						),
					)
				} ?: break
		}
	}

	abstract fun buildColumnsStatement(): String
	abstract fun buildTablesStatement(): String
	abstract fun buildWhereStatement(): String
	abstract fun buildWhereArgs(): Map<String, Any>
	abstract fun buildGroupByStatement(): String
	abstract fun buildOrderByStatement(): String

	abstract fun getPagingSource(query: String, whereArgs: List<Any>): PagingSource<Int, Entity>
	abstract fun mapEntityToModel(entity: Entity): Model
}
