package ca.josephroque.bowlingcompanion.core.data.repository

import android.content.Context
import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.common.filesystem.FileManager
import ca.josephroque.bowlingcompanion.core.common.utils.toLocalDate
import ca.josephroque.bowlingcompanion.core.database.DATABASE_NAME
import ca.josephroque.bowlingcompanion.core.database.dao.CheckpointDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.io.File
import javax.inject.Inject

private const val EXPORT_DIRECTORY = "exports"

class OfflineFirstDataTransferRepository @Inject constructor(
	private val fileManager: FileManager,
	private val checkpointDao: CheckpointDao,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
	@ApplicationContext private val context: Context,
): DataTransferRepository {
	override suspend fun getExistingDatabaseBackup(): File? {
		val backup = fileManager.getDatabasePath(DATABASE_NAME)
		return if (backup.exists()) backup else null
	}

	override suspend fun getOrCreateDatabaseBackup(): File = withContext(ioDispatcher) {
		recordCheckpoint()
		val currentDate = Clock.System.now().toLocalDate()

		val databaseFile = fileManager.getDatabasePath(DATABASE_NAME)
		val destinationFile = context.cacheDir
			.resolve(EXPORT_DIRECTORY)
			.resolve("approach_data_$currentDate.sqlite")

		databaseFile.copyTo(destinationFile, overwrite = true)
	}

	override suspend fun recordCheckpoint() {
		checkpointDao.recordCheckpoint()
	}
}