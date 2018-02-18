package ca.josephroque.bowlingcompanion.utils

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Keys for user preferences.
 */
object Preferences {

    /** Identifier for if user has opened the facebook page in the past.  */
    val FACEBOOK_PAGE_OPENED = "fb_page_opened"

    /** Identifier for if user has closed the facebook promotional content since opening the app.  */
    val FACEBOOK_CLOSED = "fb_closed"

    /**
     * Identifier for preference containing the version of the application. If the value is not equivalent to the
     * current version, then the app has been updated.
     */
    val VERSION = "pref_version"


}