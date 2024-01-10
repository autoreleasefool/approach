package ca.josephroque.bowlingcompanion.core.data.repository

import kotlinx.coroutines.flow.Flow
import java.io.File

interface DataTransferRepository {
	fun getExistingDatabaseBackup(): Flow<File?>
	suspend fun getOrCreateDatabaseBackup(): File
}