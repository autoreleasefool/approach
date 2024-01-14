package ca.josephroque.bowlingcompanion.core.data.service

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import java.io.File

interface DataExportService {
	val exportDestination: String

	fun getLatestExport(): Flow<File?>
	suspend fun getOrCreateExport(): File
	suspend fun exportDataToUri(uri: Uri)
}