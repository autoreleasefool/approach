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

    val dataFile by lazy { File(dataPath) }
    private val dataPath by lazy {
        this@UserData.context.get()?.getDatabasePath(DatabaseHelper.DATABASE_NAME)?.absolutePath
    }


    val downloadFile by lazy { File(downloadPath) }
    private val downloadPath by lazy {
        val dbPath = this@UserData.context.get()?.getDatabasePath(DatabaseHelper.DATABASE_NAME)?.absolutePath
        dbPath?.let { return@lazy "${dbPath}_dl" }
        return@lazy null
    }

    val backupFile by lazy { File(backupPath) }
    private val backupPath by lazy {
        val dbPath = this@UserData.context.get()?.getDatabasePath(DatabaseHelper.DATABASE_NAME)?.absolutePath
        dbPath?.let { return@lazy "${dbPath}_backup" }
        return@lazy null
    }

    fun backup(): Deferred<Boolean> {
        return Files.copyFile(dataFile, backupFile)
    }

    fun restoreBackup(): Deferred<Boolean> {
        return async(CommonPool) {
            if (backupFile.exists()) {
                return@async Files.copyFile(backupFile, dataFile).await()
            }

            return@async false
        }
    }

    fun deleteBackup(): Deferred<Boolean> {
        return async(CommonPool) {
            return@async backupFile.delete()
        }
    }

    fun deleteDownload(): Deferred<Boolean> {
        return async(CommonPool) {
            return@async downloadFile.delete()
        }
    }
}
