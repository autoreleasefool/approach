package ca.josephroque.bowlingcompanion.core.data.service

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import java.io.File

interface DataImportService {
	fun getLatestBackup(): Flow<File?>
	suspend fun importData(source: Uri)
	suspend fun restoreData()
}