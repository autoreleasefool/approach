package ca.josephroque.bowlingcompanion.utils

import android.content.Context
import ca.josephroque.bowlingcompanion.R
import com.mixpanel.android.mpmetrics.MixpanelAPI

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
            val projectToken = context.resources.getString(R.string.mixpanelToken)
            dangerousInstance.mixpanel = MixpanelAPI.getInstance(context, projectToken)
            dangerousInstance.initialized = true
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

    /** Instance of Mixpanel to record events. */
    private lateinit var mixpanel: MixpanelAPI

    /**
     * Flush events which have not been recorded yet to the server.
     */
    fun flush() {
        mixpanel.flush()
    }
}
