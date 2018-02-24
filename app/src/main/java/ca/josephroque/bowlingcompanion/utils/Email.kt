package ca.josephroque.bowlingcompanion.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import ca.josephroque.bowlingcompanion.R

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Provides methods related to creating and formatting emails
 */
class Email {

    companion object {
        /** Logging identifier. */
        private val TAG = "Email"

        /**
         * Prompts user to send an email with the provided parameters.
         *
         * @param activity context to send email from
         * @param recipient email recipient
         * @param subject subject of the email
         * @param body body of the email
         */
        fun sendEmail(activity: Activity, recipient: String?, subject: String?, body: String?) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.data = Uri.parse("mailto:")
            intent.type = "message/rfc822"

            recipient?.let { intent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(it)) }
            subject?.let { intent.putExtra(Intent.EXTRA_SUBJECT, it) }
            body?.let { intent.putExtra(Intent.EXTRA_TEXT, it) }

            activity.startActivity(Intent.createChooser(intent, activity.resources.getString(R.string.send_email)))
        }
    }
}