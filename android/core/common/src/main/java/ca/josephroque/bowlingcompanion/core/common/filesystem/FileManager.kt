package ca.josephroque.bowlingcompanion.core.common.filesystem

import java.io.File

interface FileManager {
	fun filePathExists(fileName: String): Boolean
	fun fileExists(file: File): Boolean

	fun getDatabasePath(fileName: String): File
}