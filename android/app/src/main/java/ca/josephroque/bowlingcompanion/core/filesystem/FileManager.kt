package ca.josephroque.bowlingcompanion.core.filesystem

import java.io.File

interface FileManager {
	fun filePathExists(fileName: String): Boolean
	fun fileExists(file: File): Boolean

	val legacyDatabaseFile: File
}