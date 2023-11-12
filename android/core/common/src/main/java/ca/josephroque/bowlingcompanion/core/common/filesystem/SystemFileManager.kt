package ca.josephroque.bowlingcompanion.core.common.filesystem

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class SystemFileManager @Inject constructor(
	@ApplicationContext private val context: Context,
): FileManager {
	override fun filePathExists(fileName: String): Boolean =
		fileExists(File(fileName))

	override fun fileExists(file: File): Boolean =
		file.exists()

	override fun getDatabasePath(fileName: String): File =
		context.getDatabasePath(fileName)

	override fun getAssets(directory: String): List<String> =
		context.assets.list(directory)?.toList() ?: emptyList()

	override fun getAsset(fileName: String): String =
		context.assets.open(fileName).bufferedReader().use { it.readText() }
}