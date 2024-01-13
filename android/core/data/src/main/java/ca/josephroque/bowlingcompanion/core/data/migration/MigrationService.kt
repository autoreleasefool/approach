package ca.josephroque.bowlingcompanion.core.data.migration

enum class DatabaseType {
	BOWLING_COMPANION,
	APPROACH,
}

interface MigrationService {
	suspend fun migrateDefaultLegacyDatabase()
	suspend fun migrateDatabase(name: String)

	suspend fun getDatabaseType(name: String): DatabaseType?
}