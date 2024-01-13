package ca.josephroque.bowlingcompanion.core.common.filesystem

import android.net.Uri
import java.io.File

interface FileManager {
	val cacheDir: File
	val filesDir: File

	fun filePathExists(fileName: String): Boolean
	fun fileExists(file: File): Boolean
	fun getExtension(uri: Uri): String?

	fun getDatabasePath(fileName: String): File

	fun getAssets(directory: String): List<String>
	fun getAsset(fileName: String): String

	fun zipFiles(zipFile: File, files: List<File>)
}