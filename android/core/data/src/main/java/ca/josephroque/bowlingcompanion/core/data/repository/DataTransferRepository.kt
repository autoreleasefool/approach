package ca.josephroque.bowlingcompanion.core.data.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import java.io.File

interface DataTransferRepository {
	fun getLatestDatabaseExport(): Flow<File?>
	suspend fun getOrCreateDatabaseExport(): File
}