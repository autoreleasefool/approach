package ca.josephroque.bowlingcompanion.core.data.service

import android.content.Context
import android.net.Uri
import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers.IO
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.common.filesystem.FileManager
import ca.josephroque.bowlingcompanion.core.common.utils.toLocalDate
import ca.josephroque.bowlingcompanion.core.database.DATABASE_NAME
import ca.josephroque.bowlingcompanion.core.database.DATABASE_SHM_NAME
import ca.josephroque.bowlingcompanion.core.database.DATABASE_WAL_NAME
import ca.josephroque.bowlingcompanion.core.database.dao.CheckpointDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.io.File
import javax.inject.Inject

class ApproachDataExportService @Inject constructor(
	private val fileManager: FileManager,
	private val checkpointDao: CheckpointDao,
	@Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
	@ApplicationContext private val context: Context,
): DataExportService {

	private val databaseFiles = listOf(
		fileManager.getDatabasePath(DATABASE_NAME),
		fileManager.getDatabasePath(DATABASE_SHM_NAME),
		fileManager.getDatabasePath(DATABASE_WAL_NAME),
	)

	private val latestExportFile: MutableStateFlow<File?> = MutableStateFlow(
		fileManager.exportsDir
			.listFiles()?.maxOfOrNull { it }
	)

	override val exportDestination: String
		get() {
			val currentDate = Clock.System.now().toLocalDate()
			return "approach_data_$currentDate.zip"
		}

	override fun getLatestExport(): Flow<File?> = latestExportFile

	override suspend fun getOrCreateExport(): File = withContext(ioDispatcher) {
		checkpointDao.recordCheckpoint()

		val destinationFile = fileManager.exportsDir
			.resolve(exportDestination)

		destinationFile.parentFile?.mkdirs()

		fileManager.zipFiles(
			destinationFile,
			databaseFiles,
		)

		latestExportFile.value = destinationFile
		destinationFile
	}

	override suspend fun exportDataToUri(uri: Uri) {
		val exportFile = getOrCreateExport()
		context.contentResolver.openOutputStream(uri)?.use {
			exportFile.inputStream().use { inputStream ->
				inputStream.copyTo(it)
			}
		} ?: throw IllegalArgumentException("Unable to write to uri: $uri")
	}
}