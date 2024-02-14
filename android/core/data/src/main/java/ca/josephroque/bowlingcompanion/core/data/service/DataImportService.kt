package ca.josephroque.bowlingcompanion.core.data.service

import android.net.Uri
import java.io.File
import kotlinx.coroutines.flow.Flow

interface DataImportService {
	fun getLatestBackup(): Flow<File?>
	suspend fun importData(source: Uri)
	suspend fun restoreData()
}
