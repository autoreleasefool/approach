package ca.josephroque.bowlingcompanion.core.filesystem

import android.content.Context
import ca.josephroque.bowlingcompanion.core.database.legacy.LegacyDatabaseHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class SystemFileManager @Inject constructor(
	@ApplicationContext private val context: Context,
): FileManager {

	override val legacyDatabaseFile: File
		get() = context.getDatabasePath(LegacyDatabaseHelper.DATABASE_NAME)

	override fun filePathExists(fileName: String): Boolean =
		fileExists(File(fileName))

	override fun fileExists(file: File): Boolean =
		file.exists()
}