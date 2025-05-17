package ca.josephroque.bowlingcompanion.core.data.migration

import android.net.Uri
import java.io.File

enum class DatabaseType {
	BOWLING_COMPANION,
	APPROACH,
}

sealed interface MigrationResult {
	data object Success : MigrationResult
	data class SuccessWithWarnings(val didCreateIssue589Backup: Boolean) : MigrationResult
}

interface MigrationService {
	fun getLegacyDatabasePath(name: String): File
	fun getLegacyDatabaseUri(name: String): Uri
	suspend fun migrateDefaultLegacyDatabase(): MigrationResult
	suspend fun migrateDatabase(name: String): MigrationResult

	suspend fun getDatabaseType(name: String): DatabaseType?
}
