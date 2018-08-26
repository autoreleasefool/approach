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

    // MARK: Team events

    fun trackSelectTeam() {
        if (disableTracking) return
        mixpanel.track("Teams - Selected")
    }

    fun trackCreateTeam(numberOfMembers: Int) {
        if (disableTracking) return
        mixpanel.track("Teams - Create")
    }

    fun trackDeleteTeam() {
        if (disableTracking) return
        mixpanel.track("Teams - Deleted")
    }

    fun trackEditTeam() {
        if (disableTracking) return
        mixpanel.track("Teams - Edited")
    }

    fun trackRorderTeamMembers() {
        if (disableTracking) return
        mixpanel.track("Teams - Reorder")
    }

    // MARK: Bowler events

    fun trackSelectBowler() {
        if (disableTracking) return
        mixpanel.track("Bowlers - Select")
    }

    fun trackCreateBowler() {
        if (disableTracking) return
        mixpanel.track("Bowlers - Create")
    }

    fun trackDeleteBowler() {
        if (disableTracking) return
        mixpanel.track("Bowlers - Delete")
    }

    fun trackEditBowler() {
        if (disableTracking) return
        mixpanel.track("Bowlers - Edit")
    }

    fun trackSortedBowlers(order: Bowler.Companion.Sort) {
        if (disableTracking) return
        mixpanel.track("Bowlers - Sorted")
    }

    // MARK: League events

    fun trackSelectLeague(isPractice: Boolean, isEvent: Boolean) {
        if (disableTracking) return
        mixpanel.track("Leagues - Select")
    }

    fun trackCreateLeague(isEvent: Boolean, numberOfGames: Int, hasAdditionalInfo: Boolean) {
        if (disableTracking) return
        mixpanel.track("Leagues - Create")
    }

    fun trackDeleteLeague() {
        if (disableTracking) return
        mixpanel.track("Leagues - Delete")
    }

    fun trackEditLeague() {
        if (disableTracking) return
        mixpanel.track("Leagues - Edit")
    }

    fun trackSortedLeagues(order: League.Companion.Sort) {
        if (disableTracking) return
        mixpanel.track("Leagues - Sorted")
    }

    // MARK: Series events

    fun trackSelectSeries() {
        if (disableTracking) return
        mixpanel.track("Series - Select")
    }

    fun trackCreateSeries() {
        if (disableTracking) return
        mixpanel.track("Series - Create")
    }

    fun trackDeleteSeries() {
        if (disableTracking) return
        mixpanel.track("Series - Delete")
    }

    fun trackEditSeries() {
        if (disableTracking) return
        mixpanel.track("Series - Edit")
    }

    fun trackToggledSeriesView(view: Series.Companion.View) {
        if (disableTracking) return
        mixpanel.track("Series - Toggled View")
    }

    // MARK: Game events

    fun trackChangedGame() {
        if (disableTracking) return
        mixpanel.track("Game - Select")
    }

    fun trackSetGameManualScore() {
        if (disableTracking) return
        mixpanel.track("Games - Manual Score")
    }

    fun trackLockGame() {
        if (disableTracking) return
        mixpanel.track("Games - Locked")
    }

    fun trackGameViewedPossibleScore(possibleScore: Int, frame: Int) {
        if (disableTracking) return
        mixpanel.track("Games - View Possible Score")
    }

    fun trackResetGame() {
        if (disableTracking) return
        mixpanel.track("Games - Reset")
    }

    // MARK: Match Play events

    fun trackRecordMatchPlay() {
        if (disableTracking) return
        mixpanel.track("Match Play - Record")
    }

    // MARK: Statistics events

    fun trackViewStatisticsList() {
        if (disableTracking) return
        mixpanel.track("Statistics - View List")
    }

    fun beginTrackingStatisticsLoaded() {
        if (disableTracking) return
        mixpanel.timeEvent("Statistics - Load")
    }

    fun trackStatisticsLoaded() {
        if (disableTracking) return
        mixpanel.track("Statistics - Load")
    }

    fun trackViewStatisticsGraph(statisticName: String) {
        if (disableTracking) return
        mixpanel.track("Statistics - View Graph")
    }

    // MARK: Settings

    fun trackViewSettings() {
        if (disableTracking) return
        mixpanel.track("Settings - View")
    }

    fun trackEnableAutoAdvance() {
        if (disableTracking) return
        mixpanel.track("Auto Advance - Enable")
    }

    fun trackDisableAutoAdvance() {
        if (disableTracking) return
        mixpanel.track("Auto Advance - Disable")
    }

    fun trackEnableAutoLock() {
        if (disableTracking) return
        mixpanel.track("Auto Lock - Enable")
    }

    fun trackDisableAutoLock() {
        if (disableTracking) return
        mixpanel.track("Auto Lock - Disable")
    }

    fun trackRate() {
        if (disableTracking) return
        mixpanel.track("Settings - Rate")
    }

    fun trackReportBug() {
        if (disableTracking) return
        mixpanel.track("Settings - Report Bug")
    }

    fun trackSendFeedback() {
        if (disableTracking) return
        mixpanel.track("Settings - Send Feedback")
    }

    fun trackViewFacebook() {
        if (disableTracking) return
        mixpanel.track("Settings - View Facebook")
    }

    fun trackViewWebsite() {
        if (disableTracking) return
        mixpanel.track("Settings - View Website")
    }

    fun trackViewSource() {
        if (disableTracking) return
        mixpanel.track("Settings - View Source")
    }

    fun trackViewAttributions() {
        if (disableTracking) return
        mixpanel.track("Settings - View Attributions")
    }

    /**
     * Flush events which have not been recorded yet to the server.
     */
    fun flush() {
        if (disableTracking) return
        mixpanel.flush()
    }
}
