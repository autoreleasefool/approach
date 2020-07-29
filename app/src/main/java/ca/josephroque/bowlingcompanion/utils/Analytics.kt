@file:Suppress("UNUSED_PARAMETER")

package ca.josephroque.bowlingcompanion.utils

import android.content.Context
import ca.josephroque.bowlingcompanion.BuildConfig
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.series.Series

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Analytics engine to record user events.
 */
class Analytics private constructor() {

    // Wrapper for the singleton instance
    private object HOLDER { val INSTANCE = Analytics() }

    companion object {
        private val instance: Analytics by lazy {
            assert(HOLDER.INSTANCE.initialized) { "The Mixpanel instance was accessed before being initialized." }
            HOLDER.INSTANCE
        }

        private val dangerousInstance: Analytics by lazy {
            HOLDER.INSTANCE
        }

        fun initialize(context: Context) {
            assert(!dangerousInstance.disableTracking) { "You cannot initialize analytics once tracking has been disabled." }
            dangerousInstance.initialized = true
        }

        @Suppress("unused")
        fun disableTracking() {
            if (BuildConfig.DEBUG) {
                assert(!instance.initialized) { "You must disable tracking before initializing analytics." }
                dangerousInstance.disableTracking = true
            }
        }

        enum class EventTime {
            Begin, End
        }

        // MARK: Team events

        fun trackSelectTeam() {}

        fun trackCreateTeam(numberOfMembers: Int) {}

        fun trackDeleteTeam() {}

        fun trackEditTeam() {}

        fun trackReorderTeamMembers() {}

        // MARK: Bowler events

        fun trackSelectBowler() {}

        fun trackCreateBowler() {}

        fun trackDeleteBowler() {}

        fun trackEditBowler() {}

        fun trackSortedBowlers(order: Bowler.Companion.Sort) {}

        // MARK: League events

        fun trackSelectLeague(isPractice: Boolean, isEvent: Boolean) {}

        fun trackCreateLeague(isEvent: Boolean, numberOfGames: Int, hasAdditionalInfo: Boolean) {}

        fun trackDeleteLeague() {}

        fun trackEditLeague() {}

        fun trackSortedLeagues(order: League.Companion.Sort) {}

        // MARK: Series events

        fun trackSelectSeries() {}

        fun trackCreateSeries() {}

        fun trackDeleteSeries() {}

        fun trackEditSeries() {}

        fun trackToggledSeriesView(view: Series.Companion.View) {}

        // MARK: Game events

        fun trackChangedGame() {}

        fun trackSetGameManualScore() {}

        fun trackLockGame() {}

        fun trackViewPossibleScore(possibleScore: Int, frame: Int) {}

        fun trackResetGame() {}

        // MARK: Match Play events

        fun trackRecordMatchPlay() {}

        // MARK: Statistics events

        fun trackViewStatisticsList() {}

        fun trackStatisticsLoaded(time: EventTime) {}

        fun trackViewStatisticsGraph(statisticName: String) {}

        // MARK: Transfer events

        fun trackViewTransferMenu() {}

        fun trackTransferExport(time: EventTime) {}

        fun trackTransferImport(time: EventTime) {}

        fun trackTransferRestoreBackup() {}

        fun trackTransferDeleteBackup() {}

        // MARK: Settings

        fun trackViewSettings() {}

        fun trackEnableAutoAdvance() {}

        fun trackDisableAutoAdvance() {}

        fun trackEnableAutoLock() {}

        fun trackDisableAutoLock() {}

        fun trackRate() {}

        fun trackReportBug() {}

        fun trackSendFeedback() {}

        fun trackViewFacebook() {}

        fun trackViewWebsite() {}

        fun trackViewSource() {}

        fun trackViewAttributions() {}

        fun trackViewPrivacyPolicy() {}

        // MARK: App Rate events

        fun trackViewAppRateDialog() {}

        fun trackAppRateDialogRate() {}

        fun trackAppRateDialogIgnore() {}

        fun trackAppRateDialogDisable() {}

        // MARK: Sharing

        fun trackViewOverview() {}

        fun trackShareImage(numberOfGames: Int) {}

        fun trackSaveImage(numberOfGames: Int) {}

        fun trackSaveImageFailed(numberOfGames: Int) {}

        // MARK: Other

        fun flush() {}
    }

    private var initialized: Boolean = false

    private var disableTracking: Boolean = false
}
