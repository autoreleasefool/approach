package ca.josephroque.bowlingcompanion.core.common.filesystem

import java.io.File

interface FileManager {
	val cacheDir: File
	val filesDir: File

	val sharedImagesDir: File
	val exportsDir: File

	fun filePathExists(fileName: String): Boolean
	fun fileExists(file: File): Boolean
	fun getFileType(file: File): FileType?

	fun getDatabasePath(fileName: String): File

	fun getAssets(directory: String): List<String>
	fun getAsset(fileName: String): String

	fun zipFiles(zipFile: File, files: List<File>)
}

enum class FileType(val signature: String) {
	Zip("504B0304"),
	SQLite("53514C69"),
}
