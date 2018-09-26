package ca.josephroque.bowlingcompanion.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import ca.josephroque.bowlingcompanion.R

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Handles Facebook related actions.
 */
object Facebook {
        @Suppress("unused")
        private const val TAG = "Facebook"

    /**
     * Intent to open the official Facebook app. If the Facebook app is not installed then the
     * default web browser will be used.
     *
     * @param context context to get resources and packages
     * @return An intent that will open the Facebook page/profile
     */
    fun getFacebookPageIntent(context: Context): Intent {
        val facebookPageUrl = context.resources.getString(R.string.facebook_page)
        var uri = Uri.parse(facebookPageUrl)
        val pm = context.packageManager
        try {
            val applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0)
            if (applicationInfo.enabled) {
                // http://stackoverflow.com/a/24547437/1048340
                uri = Uri.parse(String.format("fb://facewebmodal/f?href=%s", facebookPageUrl))
            }
        } catch (ignored: PackageManager.NameNotFoundException) {}

        return Intent(Intent.ACTION_VIEW, uri)
    }
}
