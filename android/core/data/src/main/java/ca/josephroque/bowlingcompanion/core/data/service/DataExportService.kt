package ca.josephroque.bowlingcompanion.core.data.service

import android.net.Uri
import java.io.File
import kotlinx.coroutines.flow.Flow

interface DataExportService {
	val exportDestination: String

	fun getLatestExport(): Flow<File?>
	suspend fun getOrCreateExport(): File
	suspend fun exportDataToUri(uri: Uri)
}
