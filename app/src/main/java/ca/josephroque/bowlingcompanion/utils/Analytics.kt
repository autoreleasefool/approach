package ca.josephroque.bowlingcompanion.utils

import android.content.Context
import ca.josephroque.bowlingcompanion.BuildConfig
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.series.Series
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
            assert(!dangerousInstance.disableTracking) { "You cannot initialize analytics once tracking has been disabled." }
            val initInstance = dangerousInstance
            val projectToken = context.resources.getString(R.string.mixpanelToken)
            initInstance.mixpanel = MixpanelAPI.getInstance(context, projectToken)
            initInstance.initialized = true
        }

        /**
         * Disable tracking in DEBUG mode.
         */
        @Suppress("unused")
        fun disableTracking() {
            if (BuildConfig.DEBUG) {
                assert(!instance.initialized) { "You must disable tracking before initializing analytics." }
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

        // MARK: Team events

        fun trackSelectTeam() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Teams - Selected")
        }

        fun trackCreateTeam(numberOfMembers: Int) {
            if (instance.disableTracking) return
            val properties: MutableMap<String, Any> = hashMapOf("Members" to numberOfMembers.toString())
            instance.mixpanel.trackMap("Teams - Create", properties)
        }

        fun trackDeleteTeam() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Teams - Deleted")
        }

        fun trackEditTeam() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Teams - Edited")
        }

        fun trackRorderTeamMembers() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Teams - Reorder")
        }

        // MARK: Bowler events

        fun trackSelectBowler() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Bowlers - Select")
        }

        fun trackCreateBowler() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Bowlers - Create")
        }

        fun trackDeleteBowler() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Bowlers - Delete")
        }

        fun trackEditBowler() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Bowlers - Edit")
        }

        fun trackSortedBowlers(order: Bowler.Companion.Sort) {
            if (instance.disableTracking) return
            val properties: MutableMap<String, Any> = hashMapOf("Sort" to order.name)
            instance.mixpanel.trackMap("Bowlers - Sorted", properties)
        }

        // MARK: League events

        fun trackSelectLeague(isPractice: Boolean, isEvent: Boolean) {
            if (instance.disableTracking) return
            val properties: MutableMap<String, Any> = hashMapOf(
                    "Practice" to isPractice.toString(),
                    "Event" to isEvent.toString()
            )
            instance.mixpanel.trackMap("Leagues - Select", properties)
        }

        fun trackCreateLeague(isEvent: Boolean, numberOfGames: Int, hasAdditionalInfo: Boolean) {
            if (instance.disableTracking) return
            val properties: MutableMap<String, Any> = hashMapOf(
                    "Event" to isEvent.toString(),
                    "Games" to numberOfGames.toString(),
                    "Additional Info" to hasAdditionalInfo.toString()
            )
            instance.mixpanel.trackMap("Leagues - Create", properties)
        }

        fun trackDeleteLeague() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Leagues - Delete")
        }

        fun trackEditLeague() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Leagues - Edit")
        }

        fun trackSortedLeagues(order: League.Companion.Sort) {
            if (instance.disableTracking) return
            val properties: MutableMap<String, Any> = hashMapOf("Sort" to order.name)
            instance.mixpanel.trackMap("Leagues - Sorted", properties)
        }

        // MARK: Series events

        fun trackSelectSeries() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Series - Select")
        }

        fun trackCreateSeries() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Series - Create")
        }

        fun trackDeleteSeries() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Series - Delete")
        }

        fun trackEditSeries() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Series - Edit")
        }

        fun trackToggledSeriesView(view: Series.Companion.View) {
            if (instance.disableTracking) return
            val properties: MutableMap<String, Any> = hashMapOf("View" to view.name)
            instance.mixpanel.trackMap("Series - Toggled View", properties)
        }

        // MARK: Game events

        fun trackChangedGame() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Game - Select")
        }

        fun trackSetGameManualScore() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Games - Manual Score")
        }

        fun trackLockGame() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Games - Locked")
        }

        fun trackGameViewedPossibleScore(possibleScore: Int, frame: Int) {
            if (instance.disableTracking) return
            val properties: MutableMap<String, Any> = hashMapOf(
                    "Score" to possibleScore.toString(),
                    "Frame" to frame.toString()
            )
            instance.mixpanel.trackMap("Games - View Possible Score", properties)
        }

        fun trackResetGame() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Games - Reset")
        }

        // MARK: Match Play events

        fun trackRecordMatchPlay() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Match Play - Record")
        }

        // MARK: Statistics events

        fun trackViewStatisticsList() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Statistics - View List")
        }

        fun beginTrackingStatisticsLoaded() {
            if (instance.disableTracking) return
            instance.mixpanel.timeEvent("Statistics - Load")
        }

        fun trackStatisticsLoaded() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Statistics - Load")
        }

        fun trackViewStatisticsGraph(statisticName: String) {
            if (instance.disableTracking) return
            val properties: MutableMap<String, Any> = hashMapOf("Statistic" to statisticName)
            instance.mixpanel.trackMap("Statistics - View Graph", properties)
        }

        // MARK: Settings

        fun trackViewSettings() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Settings - View")
        }

        fun trackEnableAutoAdvance() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Auto Advance - Enable")
        }

        fun trackDisableAutoAdvance() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Auto Advance - Disable")
        }

        fun trackEnableAutoLock() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Auto Lock - Enable")
        }

        fun trackDisableAutoLock() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Auto Lock - Disable")
        }

        fun trackRate() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Settings - Rate")
        }

        fun trackReportBug() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Settings - Report Bug")
        }

        fun trackSendFeedback() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Settings - Send Feedback")
        }

        fun trackViewFacebook() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Settings - View Facebook")
        }

        fun trackViewWebsite() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Settings - View Website")
        }

        fun trackViewSource() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Settings - View Source")
        }

        fun trackViewAttributions() {
            if (instance.disableTracking) return
            instance.mixpanel.track("Settings - View Attributions")
        }

        /**
         * Flush events which have not been recorded yet to the server.
         */
        fun flush() {
            if (instance.disableTracking) return
            instance.mixpanel.flush()
        }
    }

    /** Indicates if the analytics instance has been initialized yet or not. */
    private var initialized: Boolean = false

    /** Disable tracking in debug when `disableTracking()` is invoked so mixpanel token is not required. */
    private var disableTracking: Boolean = false

    /** Instance of Mixpanel to record events. */
    private lateinit var mixpanel: MixpanelAPI
}