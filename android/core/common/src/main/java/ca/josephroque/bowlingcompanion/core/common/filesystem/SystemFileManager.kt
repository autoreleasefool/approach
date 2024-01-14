package ca.josephroque.bowlingcompanion.core.common.filesystem

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.RandomAccessFile
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

	override fun getFileType(file: File): FileType? {
		try {
			RandomAccessFile(file, "r").use { randomAccessFile ->
				val bytes = ByteArray(4)
				randomAccessFile.read(bytes)
				val fileSignature = bytes.joinToString("") { "%02X".format(it) }
				return FileType.entries
					.firstOrNull {
						fileSignature.uppercase().startsWith(it.signature.uppercase())
					}
			}
		} catch (e: Exception) {
			return null
		}
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