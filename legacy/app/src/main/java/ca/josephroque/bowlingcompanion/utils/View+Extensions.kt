package ca.josephroque.bowlingcompanion.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * View utilities
 */
val View.isVisible: Boolean
    get() = visibility == View.VISIBLE

fun View.toBitmap(): Bitmap {
    measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    layout(0, 0, measuredWidth, measuredHeight)
    draw(canvas)
    return bitmap
}
