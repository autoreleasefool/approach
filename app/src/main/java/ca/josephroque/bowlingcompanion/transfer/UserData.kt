package ca.josephroque.bowlingcompanion.transfer

import android.content.Context
import ca.josephroque.bowlingcompanion.database.DatabaseHelper
import ca.josephroque.bowlingcompanion.utils.Files
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.io.File
import java.lang.ref.WeakReference

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * User data which can be downloaded or uploaded.
 */
class UserData(context: Context) {

    private val context: WeakReference<Context> = WeakReference(context)

    val sourceFile by lazy { File(sourcePath) }
    private val sourcePath by lazy {
        this@UserData.context.get()?.getDatabasePath(DatabaseHelper.DATABASE_NAME)?.absolutePath
    }

    val importFile by lazy { File(importPath) }
    private val importPath by lazy {
        val cacheDir = this@UserData.context.get()?.cacheDir?.absolutePath
        cacheDir?.let { return@lazy "${it}/imports/bowling_db_import.db" }
        return@lazy null
    }

    val exportFile by lazy { File(exportPath) }
    private val exportPath by lazy {
        val cacheDir = this@UserData.context.get()?.cacheDir?.absolutePath
        cacheDir?.let { return@lazy "${it}/exports/bowling_db_export.db" }
        return@lazy null
    }

    val backupFile by lazy { File(backupPath) }
    private val backupPath by lazy {
        val filesDir = this@UserData.context.get()?.filesDir?.absolutePath
        filesDir?.let { return@lazy "${it}/bowling_db_backup.db" }
        return@lazy null
    }

    fun exportData(): Deferred<Boolean> {
        return Files.copyFile(sourceFile, exportFile)
    }

    fun overwriteDataWithImport(): Deferred<Boolean> {
        return async(CommonPool) {
            if (importFile.exists()) {
                return@async Files.copyFile(importFile, sourceFile).await()
            }

            return@async false
        }
    }

    fun deleteImport(): Deferred<Boolean> {
        return async(CommonPool) {
            return@async importFile.delete()
        }
    }

    fun backupData(): Deferred<Boolean> {
        return Files.copyFile(sourceFile, backupFile)
    }

    fun restoreBackup(): Deferred<Boolean> {
        return async(CommonPool) {
            if (backupFile.exists()) {
                return@async Files.copyFile(backupFile, sourceFile).await()
            }

            return@async false
        }
    }

    fun deleteBackup(): Deferred<Boolean> {
        return async(CommonPool) {
            return@async backupFile.delete()
        }
    }
}
