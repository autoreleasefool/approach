package ca.josephroque.bowlingcompanion.core.data.repository

import android.content.Context
import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
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
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject

private const val EXPORT_DIRECTORY = "exports"

class OfflineFirstDataTransferRepository @Inject constructor(
	private val fileManager: FileManager,
	private val checkpointDao: CheckpointDao,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
	@ApplicationContext private val context: Context,
): DataTransferRepository {
	private val databaseFile: File
		get() = fileManager.getDatabasePath(DATABASE_NAME)
	private val databaseShmFile: File
		get() = fileManager.getDatabasePath(DATABASE_SHM_NAME)
	private val databaseWalFile: File
		get() = fileManager.getDatabasePath(DATABASE_WAL_NAME)

	private val exportDirectory: File
		get() = context.cacheDir
			.resolve(EXPORT_DIRECTORY)

	private val latestExportFile: MutableStateFlow<File?> = MutableStateFlow(
		exportDirectory
			.listFiles()?.maxOfOrNull { it }
	)

	private val latestBackupFile: MutableStateFlow<File?> = MutableStateFlow(
		backupDirectory
			.listFiles()?.maxOfOrNull { it }
	)

	override fun getLatestDatabaseExport(): Flow<File?> = latestExportFile

	override suspend fun getOrCreateDatabaseExport(): File = withContext(ioDispatcher) {
		getOrCreateDatabaseExportWithSuffix()
	}

	private fun getOrCreateDatabaseExportWithSuffix(suffix: String = ""): File {
		recordCheckpoint()
		val currentDate = Clock.System.now().toLocalDate()

		val destinationFile = exportDirectory
			.resolve("approach_data_$currentDate$suffix.zip")

		destinationFile.parentFile?.mkdirs()

		zipFiles(
			listOf(databaseFile, databaseShmFile, databaseWalFile),
			destinationFile
		)

		latestExportFile.value = destinationFile
		return destinationFile
	}

	private fun recordCheckpoint() {
		checkpointDao.recordCheckpoint()
	}

	private fun zipFiles(files: List<File>, destination: File): File {
		val zipOutputStream = ZipOutputStream(destination.outputStream())
		for (file in files) {
			if (!file.exists()) {
				continue
			}

			val zipEntry = ZipEntry(file.name)
			zipOutputStream.putNextEntry(zipEntry)

			file.inputStream().use { inputStream ->
				inputStream.copyTo(zipOutputStream)
			}

			zipOutputStream.closeEntry()
		}

		zipOutputStream.close()
		return destination
	}
}