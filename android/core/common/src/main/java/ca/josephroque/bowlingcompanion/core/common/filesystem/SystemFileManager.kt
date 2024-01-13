package ca.josephroque.bowlingcompanion.core.common.filesystem

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject

class SystemFileManager @Inject constructor(
	@ApplicationContext private val context: Context,
): FileManager {
	override val cacheDir: File
		get() = context.cacheDir

	override val filesDir: File
		get() = context.filesDir

	override fun filePathExists(fileName: String): Boolean =
		fileExists(File(fileName))

	override fun fileExists(file: File): Boolean =
		file.exists()

	override fun getExtension(uri: Uri): String? {
		val mimeTypeMap = MimeTypeMap.getSingleton()
		val type = context.contentResolver.getType(uri) ?: return null
		return mimeTypeMap.getExtensionFromMimeType(type)
	}

	override fun getDatabasePath(fileName: String): File =
		context.getDatabasePath(fileName)

	override fun getAssets(directory: String): List<String> =
		context.assets.list(directory)?.toList() ?: emptyList()

	override fun getAsset(fileName: String): String =
		context.assets.open(fileName).bufferedReader().use { it.readText() }

	override fun zipFiles(zipFile: File, files: List<File>) {
		ZipOutputStream(zipFile.outputStream()).use { zipOutputStream ->
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
		}
	}
}