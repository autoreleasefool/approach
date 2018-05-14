package ca.josephroque.bowlingcompanion.utils

import android.os.Build
import android.text.Html
import android.text.Spanned

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * String utilities.
 */

/**
 * Create a spanned version of this string.
 *
 * @return the string as a [Spanned]
 */
fun String.toSpanned(): Spanned {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        return Html.fromHtml(this)
    }
}
