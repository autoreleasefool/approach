package ca.josephroque.bowlingcompanion.core.data.repository

import java.io.File

interface DataTransferRepository {
	suspend fun getExistingDatabaseBackup(): File?
	suspend fun getOrCreateDatabaseBackup(): File

	suspend fun recordCheckpoint()
}