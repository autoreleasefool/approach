package ca.josephroque.bowlingcompanion.utils

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.annotation.StringRes
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AlertDialog
import ca.josephroque.bowlingcompanion.R

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Record and display user errors in the app.
 */
class BCError(@StringRes private val title: Int = R.string.error_unknown_title,
              @StringRes private val message: Int = R.string.error_unknown_message,
              private val severity: Severity = Severity.Error) {

    /**
     * Severity of the error.
     */
    enum class Severity {
        Error,
        Warning,
        Info,
    }

    /**
     * Show the error in a dialog with an appropriate icon.
     *
     * @param context parent context
     */
    fun show(context: Context) {
        var icon: Drawable?
        val color: Int

        when (severity) {
            Severity.Error -> {
                icon = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_error, null)
                color = ResourcesCompat.getColor(context.resources, R.color.dangerRed, null)
            }
            Severity.Warning -> {
                icon = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_warning, null)
                color = ResourcesCompat.getColor(context.resources, R.color.warningYellow, null)
            }
            Severity.Info -> {
                icon = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_info, null)
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
