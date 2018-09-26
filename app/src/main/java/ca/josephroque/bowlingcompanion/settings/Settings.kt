package ca.josephroque.bowlingcompanion.settings

import android.content.SharedPreferences

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Keys for user settings.
 */
object Settings {

    interface BaseSetting {
        val prefName: String
    }

    enum class BooleanSetting(override val prefName: String) : BaseSetting {
        /** Identifier for preference to accumulate statistics over time vs week by week. */
        AccumulateStatistics("accumulate_statistics"),

        /* Keep in line with preferences_defaults.xml */

        // MARK: General

        /** Identifier for preference to enable highlighting scores.  */
        HighlightScoreEnabled("pref_highlight_score_enabled"),

        /** Identifier for preference to enable highlighting series.  */
        HighlightSeriesEnabled("pref_highlight_series_enabled"),

        /** Identifier for preference to show averages with up to 1 decimal place of accuracy.  */
        AverageAsDecimal("pref_average_as_decimal"),

        /** Identifier for preference for if app should ask user to combine similar series.  */
        // FIXME: setting unused
        AskCombine("pref_ask_combine"),

        // MARK: Scoring

        /** Identifier for preference which allows user to enable auto advancing frames.  */
        EnableAutoAdvance("pref_enable_auto_advance"),

        /** Identifier for preference for strikes and spares should be highlighted while editing a game.  */
        EnableAutoLock("pref_enable_auto_lock"),

        /** Identifier for preference for if floating buttons should be shown when editing a game.  */
        EnableFab("pref_enable_fab"),

        /** Identifier for preference for strikes and spares should be highlighted while editing a game.  */
        EnableStrikeHighlights("pref_enable_strike_highlights"),

        // MARK: Stats

        /** Identifier for preference which indicates if events should be included in stats.  */
        IncludeEvents("pref_include_events"),

        /** Identifier for preference which indicates if open games should be included in stats.  */
        IncludeOpen("pref_include_open"),

        /** Identifier for preference to count Headpin + 2 as a Headpin in statistics.  */
        CountH2AsH("pref_count_h2_as_h"),

        /** Identifier for preference to count Split + 2 as a Split in statistics.  */
        CountS2AsS("pref_count_s2_as_s"),

        // MARK: Match play

        /** Identifier for preference to show or hide match play results in series view.  */
        ShowMatchResults("pref_show_match_results"),

        /** Identifier for preference to highlight match play results in series view.  */
        HighlightMatchResults("pref_highlight_match_results");

        val default: Boolean
            get() {
                return when (this) {
                    HighlightScoreEnabled -> true
                    HighlightSeriesEnabled -> true
                    AverageAsDecimal -> true
                    AskCombine -> true
                    EnableAutoAdvance -> false
                    EnableAutoLock -> false
                    EnableFab -> true
                    EnableStrikeHighlights -> true
                    IncludeEvents -> true
                    IncludeOpen -> true
                    CountH2AsH -> false
                    CountS2AsS -> false
                    ShowMatchResults -> true
                    HighlightMatchResults -> true
                    AccumulateStatistics -> false
                }
            }

        fun getValue(preferences: SharedPreferences): Boolean {
            return preferences.getBoolean(this.prefName, this.default)
        }
    }

    enum class StringSetting(override val prefName: String) : BaseSetting {
        /** Identifier for preference which allows user to select time interval before auto advance.  */
        AutoAdvanceTime("pref_auto_advance_time");

        val default: String
            get() {
                return when (this) {
                    AutoAdvanceTime -> "10"
                }
            }

        fun getValue(preferences: SharedPreferences): String {
            return preferences.getString(this.prefName, this.default)!!
        }
    }

    enum class StaticSetting(override val prefName: String) : BaseSetting {
        // MARK: Feedback

        /** Identifier for preference which should open app in play store.  */
        Rate("pref_rate"),

        /** Identifier for preference which should open email intent to report a bug.  */
        ReportBug("pref_report_bug"),

        /** Identifier for preference which should open email intent to send feedback.  */
        SendFeedback("pref_send_feedback"),

        // MARK: Other

        /** Identifier for preference for opening the app's Facebook page.  */
        Facebook("pref_facebook"),

        /** Identifier for preference to view developer's website. */
        DeveloperWebsite("pref_developer_website"),

        /** Identifier for preference to view open source repository. */
        ViewSource("pref_view_source"),

        /** Identifier for preference to display legal attribution from Open Source Software.  */
        Attributions("pref_attributions"),

        /** Identifier for preference to view the app privacy policy. */
        PrivacyPolicy("pref_view_privacy_policy"),

        /** Identifier for preference to show current app version.  */
        VersionName("pref_version_name");
    }
}
