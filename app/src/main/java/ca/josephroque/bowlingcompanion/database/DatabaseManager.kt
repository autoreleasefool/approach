package ca.josephroque.bowlingcompanion.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Manage access to the database.
 */
object DatabaseManager {
    fun getReadableDatabase(context: Context): Deferred<SQLiteDatabase> {
        return async(CommonPool) {
            Annihilator.instance.wait().await()
            Saviour.instance.wait().await()
            return@async DatabaseHelper.getInstance(context).readableDatabase
        }
    }

    fun getWritableDatabase(context: Context): Deferred<SQLiteDatabase> {
        return async(CommonPool) {
            Annihilator.instance.wait().await()
            Saviour.instance.wait().await()
            return@async DatabaseHelper.getInstance(context).writableDatabase
        }
    }
}
