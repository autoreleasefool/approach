package ca.josephroque.bowlingcompanion.core.data.repository

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.common.filesystem.FileManager
import ca.josephroque.bowlingcompanion.core.common.utils.toLocalDate
import ca.josephroque.bowlingcompanion.core.data.migration.DatabaseType
import ca.josephroque.bowlingcompanion.core.data.migration.MigrationService
import ca.josephroque.bowlingcompanion.core.database.DATABASE_NAME
import ca.josephroque.bowlingcompanion.core.database.DATABASE_SHM_NAME
import ca.josephroque.bowlingcompanion.core.database.DATABASE_WAL_NAME
import ca.josephroque.bowlingcompanion.core.database.dao.CheckpointDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile
import javax.inject.Inject

private const val EXPORT_DIRECTORY = "exports"
private const val BACKUP_DIRECTORY = "backup"

class OfflineFirstDataTransferRepository @Inject constructor(
	private val fileManager: FileManager,
	private val migrationService: MigrationService,
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
	private val databaseFiles
		get() = listOf(databaseFile, databaseShmFile, databaseWalFile)

	private val exportDirectory: File
		get() = fileManager.cacheDir
			.resolve(EXPORT_DIRECTORY)

	private val backupDirectory: File
		get() = fileManager.filesDir
			.resolve(BACKUP_DIRECTORY)

	private val latestExportFile: MutableStateFlow<File?> = MutableStateFlow(
		exportDirectory
			.listFiles()?.maxOfOrNull { it }
	)

	private val latestBackupFile: MutableStateFlow<File?> = MutableStateFlow(
		backupDirectory
			.listFiles()?.maxOfOrNull { it }
	)

	private fun getTemporaryImportFile(extension: String): File =
		fileManager.cacheDir.resolve("import.$extension")

	override fun getLatestDatabaseExport(): Flow<File?> = latestExportFile

	override fun getLatestDatabaseBackup(): Flow<File?> = latestBackupFile

	override val exportFileName: String
		get() {
			val currentDate = Clock.System.now().toLocalDate()
			return "approach_data_$currentDate.zip"
		}

	override suspend fun exportData(destination: Uri) {
		val exportFile = getLatestDatabaseExport().firstOrNull() ?: getOrCreateDatabaseExport()
		context.contentResolver.openOutputStream(destination)?.use {
			exportFile.inputStream().use { inputStream ->
				inputStream.copyTo(it)
			}
		} ?: throw IllegalArgumentException("Unable to write to file: $destination")
	}

	override suspend fun importData(source: Uri) {
		importData(source = source, performBackUp = true)
	}

	private suspend fun importData(source: Uri, performBackUp: Boolean) {
		if (performBackUp) {
			val backupFile = getOrCreateDatabaseExport()
			backupFile.copyTo(backupDirectory.resolve(backupFile.name), overwrite = true)
			latestBackupFile.value = backupFile
		}

		val importFile = getTemporaryImportFile(fileManager.getExtension(source) ?: "")
		context.contentResolver.openInputStream(source)?.use {
			it.copyTo(FileOutputStream(importFile, false))
		} ?: throw IllegalArgumentException("Unable to read file: $source")

		when (importFile.extension) {
			"zip", "bin" -> importZipFile(importFile)
			"db", "sqlite" -> importDatabaseFile(importFile)
			else -> throw IllegalArgumentException("Unsupported file type: ${importFile.extension}")
		}

		when (migrationService.getDatabaseType(importFile.name)) {
			null -> throw IllegalArgumentException("Unsupported database type: ${importFile.name}")
			DatabaseType.APPROACH -> Unit
			DatabaseType.BOWLING_COMPANION -> migrationService.migrateDatabase(importFile.name)
		}
	}

	override suspend fun restoreData() {
		val backupFile = latestBackupFile.value ?: return
		importData(backupFile.toUri(), performBackUp = false)
	}

	override suspend fun getOrCreateDatabaseExport(): File = withContext(ioDispatcher) {
		recordCheckpoint()

		val destinationFile = exportDirectory
			.resolve(exportFileName)

		destinationFile.parentFile?.mkdirs()

		fileManager.zipFiles(
			destinationFile,
			databaseFiles,
		)

		latestExportFile.value = destinationFile
		destinationFile
	}

	private fun recordCheckpoint() {
		checkpointDao.recordCheckpoint()
	}

	private fun importZipFile(file: File) {
		databaseFiles.forEach(File::delete)

		ZipFile(file).use { zip ->
			zip.entries().asSequence()
				.filter { databaseFiles.map(File::getName).contains(it.name) }
				.forEach { entry ->
					val destinationFile = when (entry.name) {
						databaseFile.name -> databaseFile
						databaseShmFile.name -> databaseShmFile
						databaseWalFile.name -> databaseWalFile
						else -> throw IllegalArgumentException("Unsupported file type: ${entry.name}")
					}

					zip.getInputStream(entry).use { inputStream ->
						destinationFile.outputStream().use { outputStream ->
							inputStream.copyTo(outputStream)
						}
					}
				}
		}
	}

	private fun importDatabaseFile(file: File) {
		databaseFiles.forEach(File::delete)
		file.copyTo(databaseFile, overwrite = true)
	}
}