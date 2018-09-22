package ca.josephroque.bowlingcompanion.database

import android.content.Context
import android.util.Log
import ca.josephroque.bowlingcompanion.App
import ca.josephroque.bowlingcompanion.common.Android
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Managing deleting from the database synchronously and waiting for deletion tasks
 * to finish before other database tasks can process.
 */
class Annihilator private constructor() {

    companion object {
        @Suppress("unused")
        private const val TAG = "Saviour"

        val instance: Annihilator by lazy { Holder.INSTANCE }
    }

    private object Holder { val INSTANCE = Annihilator() }

    private var annihilatorIsRunning = AtomicBoolean(false)

    private val deletionQueue: Queue<Job> = LinkedList()

    init {
        launchAnnihilatorProcessor()
    }

    // MARK: Annihilator

    fun wait(): Deferred<Unit> {
        return async(CommonPool) {
            launchAnnihilatorProcessor()
            while (deletionQueue.isNotEmpty()) {
                delay(50)
            }
        }
    }

    fun delete(weakContext: WeakReference<Context>, tableName: String, whereClause: String, whereArgs: Array<String>) {
        val job = launch(context = Android, start = CoroutineStart.LAZY) {
            val strongContext = weakContext.get() ?: return@launch
            val database = DatabaseHelper.getInstance(strongContext).writableDatabase

            database.beginTransaction()
            try {
                database.delete(tableName, whereClause, whereArgs)
                database.setTransactionSuccessful()
            } catch (e: Exception) {
                // Does nothing
                // If there's an error deleting this item, then they just remain in the
                // user's data and no harm is done.
            } finally {
                database.endTransaction()
            }
        }

        deletionQueue.offer(job)
        launchAnnihilatorProcessor()
    }

    // MARK: Private functions

    private fun launchAnnihilatorProcessor() {
        if (annihilatorIsRunning.get()) {
            return
        }

        annihilatorIsRunning.set(true)
        launch(CommonPool) {
            while (App.isRunning.get() || deletionQueue.isNotEmpty()) {
                val deletionRoutine = deletionQueue.poll()
                if (!deletionRoutine.isCompleted && !deletionRoutine.isCancelled) {
                    deletionRoutine.join()
                } else {
                    Log.e(TAG, "Annihilator thread already processed - $deletionRoutine")
                }
            }

            annihilatorIsRunning.set(false)
        }
    }

}
