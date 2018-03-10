package ca.josephroque.bowlingcompanion.utils

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import ca.josephroque.bowlingcompanion.R

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Record and display user errors in the app.
 */
class BCError(private val title: String, private val message: String, private val severity: Severity) {

    enum class Severity {
        Error,
        Warning,
        Info,
    }

    fun show(context: Context) {
        var icon: Drawable?
        val color: Int

        when (severity) {
            Severity.Error -> {
                icon = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_error_white_24dp, null)
                color = ResourcesCompat.getColor(context.resources, R.color.dangerRed, null)
            }
            Severity.Warning -> {
                icon = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_warning_white_24dp, null)
                color = ResourcesCompat.getColor(context.resources, R.color.warningYellow, null)
            }
            Severity.Info -> {
                icon = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_info_white_24dp, null)
                color = ResourcesCompat.getColor(context.resources, R.color.infoBlue, null)
            }
        }

        icon = icon?.constantState?.newDrawable()
        icon?.mutate()?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)

        AlertDialog.Builder(context)
                .setIcon(icon)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.okay, null)
                .create()
                .show()
    }

}