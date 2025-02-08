package ca.josephroque.bowlingcompanion.core.data.queries

interface QueryComponent {
	val tableAlias: String

	fun buildFromClause(): String
	fun buildJoinClause(parentTable: String, parentColumn: String, childColumn: String): String
	fun buildWhereClauses(): List<String>
	fun whereClauseArgs(): Map<String, Any>
	fun buildOrderClause(): List<String>
}

data object StubQueryComponent : QueryComponent {
	override val tableAlias: String = ""
	override fun buildFromClause(): String = ""
	override fun buildJoinClause(parentTable: String, parentColumn: String, childColumn: String): String = ""
	override fun buildWhereClauses(): List<String> = emptyList()
	override fun whereClauseArgs(): Map<String, Any> = emptyMap()
	override fun buildOrderClause(): List<String> = emptyList()
}
