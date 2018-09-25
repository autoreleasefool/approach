package ca.josephroque.bowlingcompanion.utils

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Keys for user preferences.
 */
object Preferences {

    /** Identifier for if user has opened the facebook page in the past.  */
    const val FACEBOOK_PAGE_OPENED = "fb_page_opened"

    /** Identifier for if user has closed the facebook promotional content since opening the app.  */
    const val FACEBOOK_CLOSED = "fb_closed"

    /**
     * Identifier for preference indicating sort order which user prefers for bowlers.
     */
    const val BOWLER_SORT_ORDER = "pref_bowler_sort_order"

    /**
     * Identifier for preference indicating sort order which user prefers for teams.
     * Identical sort order to [BOWLER_SORT_ORDER].
     */
    const val TEAM_SORT_ORDER = BOWLER_SORT_ORDER

    /**
     * Identifier for preference indicating sort order which user prefers for leagues.
     */
    const val LEAGUE_SORT_ORDER = "pref_league_sort_order"
}
