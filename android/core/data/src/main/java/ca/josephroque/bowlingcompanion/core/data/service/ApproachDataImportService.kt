package ca.josephroque.bowlingcompanion.core.data.service

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.common.filesystem.FileManager
import ca.josephroque.bowlingcompanion.core.common.filesystem.FileType
import ca.josephroque.bowlingcompanion.core.data.migration.MigrationService
import ca.josephroque.bowlingcompanion.core.database.DATABASE_NAME
import ca.josephroque.bowlingcompanion.core.database.DATABASE_SHM_NAME
import ca.josephroque.bowlingcompanion.core.database.DATABASE_WAL_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile
import javax.inject.Inject

private const val BACKUP_DIRECTORY = "backup"

class ApproachDataImportService @Inject constructor(
	private val exportService: DataExportService,
	private val fileManager: FileManager,
	private val migrationService: MigrationService,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
	@ApplicationContext private val context: Context,
): DataImportService {

	private val databaseFiles = listOf(
		fileManager.getDatabasePath(DATABASE_NAME),
		fileManager.getDatabasePath(DATABASE_SHM_NAME),
		fileManager.getDatabasePath(DATABASE_WAL_NAME),
	)

	private val backupDirectory: File
		get() = fileManager.filesDir
			.resolve(BACKUP_DIRECTORY)

	private val latestBackupFile: MutableStateFlow<File?> = MutableStateFlow(
		backupDirectory
			.listFiles()?.maxOfOrNull { it }
	)

	private val temporaryImportFile: File
		get() = fileManager.cacheDir.resolve("import.tmp")

	override fun getLatestBackup(): Flow<File?> = latestBackupFile

	override suspend fun importData(source: Uri) {
		importData(source = source, performBackup = true)
	}

	override suspend fun restoreData() {
		val backupFile = latestBackupFile.value ?: return
		importData(source = backupFile.toUri(), performBackup = false)
	}

	private suspend fun importData(source: Uri, performBackup: Boolean) = withContext(ioDispatcher) {
		if (performBackup) {
			val backupFile = exportService.getOrCreateExport()
			backupFile.copyTo(backupDirectory.resolve(backupFile.name), overwrite = true)
			latestBackupFile.value = backupFile
		}

		val temporaryFile = temporaryImportFile
		context.contentResolver.openInputStream(source)?.use { inputStream ->
			FileOutputStream(temporaryFile).use { outputStream ->
				inputStream.copyTo(outputStream)
			}
		} ?: throw IllegalStateException("Unable to read file: $source")

		val fileType = fileManager.getFileType(temporaryFile)
			?: throw IllegalStateException("Unsupported file type: $source")

		when (fileType) {
			FileType.Zip -> importZipFile(temporaryFile)
			FileType.SQLite -> importSQLiteFile(temporaryFile)
		}

		// TODO: migrationService
	}

	private fun importZipFile(zipFile: File) {
		databaseFiles.forEach(File::delete)

		// TODO: this probably needs to import to somewhere the migration service can handle
		ZipFile(zipFile).use { zip ->
			zip.entries().asSequence()
				.filter { databaseFiles.map(File::getName).contains(it.name) }
				.forEach { entry ->
					val destinationFile = when (entry.name) {
						DATABASE_NAME -> fileManager.getDatabasePath(DATABASE_NAME)
						DATABASE_SHM_NAME -> fileManager.getDatabasePath(DATABASE_SHM_NAME)
						DATABASE_WAL_NAME -> fileManager.getDatabasePath(DATABASE_WAL_NAME)
						else -> throw IllegalStateException("Unsupported file: ${entry.name}")
					}

					zip.getInputStream(entry).use { inputStream ->
						FileOutputStream(destinationFile).use { outputStream ->
							inputStream.copyTo(outputStream)
						}
					}
				}
		}
	}

	private fun importSQLiteFile(sqliteFile: File) {
		databaseFiles.forEach(File::delete)
		// TODO: this probably needs to import to somewhere the migration service can handle
		sqliteFile.copyTo(fileManager.getDatabasePath(DATABASE_NAME), overwrite = true)
	}
}