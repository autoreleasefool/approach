package ca.josephroque.bowlingcompanion.core.data.migration

import android.net.Uri
import java.io.File

enum class DatabaseType {
	BOWLING_COMPANION,
	APPROACH,
}

interface MigrationService {
	fun getLegacyDatabasePath(name: String): File
	fun getLegacyDatabaseUri(name: String): Uri
	suspend fun migrateDefaultLegacyDatabase()
	suspend fun migrateDatabase(name: String)

	suspend fun getDatabaseType(name: String): DatabaseType?
}