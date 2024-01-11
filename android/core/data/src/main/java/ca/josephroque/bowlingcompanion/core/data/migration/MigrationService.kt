package ca.josephroque.bowlingcompanion.core.data.migration

interface MigrationService {
	suspend fun migrateDefaultLegacyDatabase()
	suspend fun migrateDatabase(name: String)
}