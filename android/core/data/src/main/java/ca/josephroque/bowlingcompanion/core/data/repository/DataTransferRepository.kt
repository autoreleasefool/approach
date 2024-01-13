package ca.josephroque.bowlingcompanion.core.data.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import java.io.File

interface DataTransferRepository {
	val exportFileName: String

	fun getLatestDatabaseExport(): Flow<File?>
	suspend fun getOrCreateDatabaseExport(): File
	suspend fun exportData(destination: Uri)

	fun getLatestDatabaseBackup(): Flow<File?>
	suspend fun importData(source: Uri)
	suspend fun restoreData()
}