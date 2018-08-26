package ca.josephroque.bowlingcompanion.utils

import android.content.Context
import ca.josephroque.bowlingcompanion.BuildConfig
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.database.Contract.BowlerEntry
import ca.josephroque.bowlingcompanion.database.Contract.FrameEntry
import ca.josephroque.bowlingcompanion.database.Contract.GameEntry
import ca.josephroque.bowlingcompanion.database.Contract.LeagueEntry
import ca.josephroque.bowlingcompanion.database.Contract.MatchPlayEntry
import ca.josephroque.bowlingcompanion.database.Contract.SeriesEntry
import ca.josephroque.bowlingcompanion.database.Contract.TeamEntry
import ca.josephroque.bowlingcompanion.database.DatabaseHelper
import com.mixpanel.android.mpmetrics.MixpanelAPI
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.io.File

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Analytics engine to record user events.
 */
class Analytics private constructor() {

    // Wrapper for the singleton instance
    private object HOLDER { val INSTANCE = Analytics() }

    companion object {
        /**
         * Initialize the analytics engine.
         *
         * @param context for analytics
         */
        fun initialize(context: Context) {
            assert(!dangerousInstance.disableTracking) { "You cannot initialize analytics once tracking has been disabled." }
            val initInstance = dangerousInstance
            val projectToken = context.resources.getString(R.string.mixpanelToken)
            initInstance.mixpanel = MixpanelAPI.getInstance(context, projectToken)
            initInstance.refreshSuperProperties(context)
            initInstance.initialized = true
        }

        /**
         * Disable tracking in DEBUG mode.
         */
        @Suppress("unused")
        fun disableTracking() {
            if (BuildConfig.DEBUG) {
                assert(!instance.initialized) { "You must disable tracking before initializing analytics. "}
                dangerousInstance.disableTracking = true
            }
        }

        /** Singleton instance */
        val instance: Analytics by lazy {
            assert(instance.initialized) { "The Mixpanel instance was accessed before being initialized." }
            HOLDER.INSTANCE
        }

        /** Singleton instance without accessor assertion. */
        private val dangerousInstance: Analytics by lazy {
            HOLDER.INSTANCE
        }
    }

    /** Indicates if the analytics instance has been initialized yet or not. */
    private var initialized: Boolean = false

    /** Disable tracking in debug when `disableTracking()` is invoked so mixpanel token is not required. */
    private var disableTracking: Boolean = false

    /** Instance of Mixpanel to record events. */
    private lateinit var mixpanel: MixpanelAPI

    /**
     * Set user super properties.
     */
    fun refreshSuperProperties(context: Context) {
        launch (CommonPool) {
            val properties: MutableMap<String, Any> = HashMap()

            // Get database properties
            val db = DatabaseHelper.getInstance(context).readableDatabase
            val dbSize = File(db.path).length()
            properties["Database size"] = dbSize

            // Get row counts for each table
            val tables = hashMapOf(
                TeamEntry.TABLE_NAME to "Team",
                BowlerEntry.TABLE_NAME to "Bowler",
                LeagueEntry.TABLE_NAME to "League",
                SeriesEntry.TABLE_NAME to "Series",
                GameEntry.TABLE_NAME to "Game",
                FrameEntry.TABLE_NAME to "Frame",
                MatchPlayEntry.TABLE_NAME to "MatchPlay"
            )

            for (table in tables) {
                val tableName = table.key
                val property = table.value

                val cursor = db.rawQuery("SELECT COALESCE(MAX(id)+1, 0) FROM $tableName", null, null)
                val count = cursor.getInt(0)
                properties["$property row count"] = count
                cursor.close()
            }

            mixpanel.registerSuperPropertiesMap(properties)
        }
    }

    /**
     * Flush events which have not been recorded yet to the server.
     */
    fun flush() {
        if (disableTracking) return
        mixpanel.flush()
    }
}
