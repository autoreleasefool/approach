package ca.josephroque.bowlingcompanion.settings

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Keys for user settings.
 */
object Settings {

    // General

    // TODO: decide if keeping theme
    /** Identifier for preference which allows user to select a theme.  */
    // val CURRENT_THEME = "pref_current_theme"

    /** Identifier for preference to enable highlighting scores.  */
    const val HIGHLIGHT_SCORE_ENABLED = "pref_highlight_score_enabled"

    /** Identifier for preference which allows user to select a minimum score to be highlighted.  */
    const val HIGHLIGHT_SCORE = "pref_highlight_score"

    /** Identifier for preference to enable highlighting series.  */
    const val HIGHLIGHT_SERIES_ENABLED = "pref_highlight_series_enabled"

    /** Identifier for preference which allows user to select a minimum series total to be highlighted.  */
    const val HIGHLIGHT_SERIES = "pref_highlight_series"

    /** Identifier for preference to show averages with up to 1 decimal place of accuracy.  */
    const val AVERAGE_AS_DECIMAL = "pref_average_as_decimal"

    /** Identifier for preference for if app should ask user to combine similar series.  */
    const val ASK_COMBINE = "pref_ask_combine"

    // Scoring

    /** Identifier for preference which allows user to enable auto advancing frames.  */
    const val ENABLE_AUTO_ADVANCE = "pref_enable_auto_advance"

    /** Identifier for preference which allows user to select time interval before auto advance.  */
    const val AUTO_ADVANCE_TIME = "pref_auto_advance_time"

    /** Identifier for preference for if floating buttons should be shown when editing a game.  */
    const val ENABLE_FAB = "pref_enable_fab"

    /** Identifier for preference if pins should be displayed behind or above floating action buttons.  */
    const val PINS_BEHIND_FABS = "pref_pins_behind_fabs"

    /** Identifier for preference for strikes and spares should be highlighted while editing a game.  */
    const val ENABLE_STRIKE_HIGHLIGHTS = "pref_enable_strike_highlights"

    /** Identifier for preference for strikes and spares should be highlighted while editing a game.  */
    const val ENABLE_AUTO_LOCK = "pref_enable_auto_lock"

    // Stats

    /** Identifier for preference which indicates if events should be included in stats.  */
    const val INCLUDE_EVENTS = "pref_include_events"

    /** Identifier for preference which indicates if open games should be included in stats.  */
    const val INCLUDE_OPEN = "pref_include_open"

    /** Identifier for preference to count Headpin + 2 as a Headpin in statistics.  */
    const val COUNT_H2_AS_H = "pref_count_h2_as_h"

    /** Identifier for preference to count Split + 2 as a Split in statistics.  */
    const val COUNT_S2_AS_S = "pref_count_s2_as_s"

    // Match play

    /** Identifier for preference to show or hide match play results in series view.  */
    const val SHOW_MATCH_RESULTS = "pref_show_match_results"

    /** Identifier for preference to highlight match play results in series view.  */
    const val HIGHLIGHT_MATCH_RESULTS = "pref_highlight_match_results"

    // Feedback

    /** Identifier for preference which should open app in play store.  */
    const val RATE = "pref_rate"

    /** Identifier for preference which should open email intent to report a bug.  */
    const val REPORT_BUG = "pref_report_bug"

    /** Identifier for preference which should open email intent to send feedback.  */
    const val SEND_FEEDBACK = "pref_send_feedback"

    // Other

    /** Identifier for preference for opening the app's Facebook page.  */
    const val FACEBOOK = "pref_facebook"

    /** Identifier for preference to view developer's website. */
    const val DEVELOPER_WEBSITE = "pref_developer_website"

    /** Identifier for preference to view open source repository. */
    const val VIEW_SOURCE = "pref_view_source"

    /** Identifier for preference to display legal attribution from Open Source Software.  */
    const val ATTRIBUTIONS = "pref_attributions"

    /** Identifier for preference to show current app version.  */
    const val VERSION_NAME = "pref_version_name"

}
