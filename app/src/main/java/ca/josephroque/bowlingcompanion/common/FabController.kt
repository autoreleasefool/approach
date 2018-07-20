package ca.josephroque.bowlingcompanion.common

import android.graphics.Color
import android.support.design.widget.FloatingActionButton
import android.view.View

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Control the floating action button with common actions.
 */
class FabController(
    private val floatingActionButton: FloatingActionButton,
    listener: View.OnClickListener
) {

    init {
        floatingActionButton.setOnClickListener(listener)
    }

    /** Drawable to display in the floating action button. */
    var image: Int? = null
        set(value) {
            field = value
            if (floatingActionButton.visibility == View.VISIBLE) {
                floatingActionButton.hide(fabVisibilityChangeListener)
            } else {
                fabVisibilityChangeListener.onHidden(floatingActionButton)
            }
        }

    /** Handle visibility changes in the fab. */
    private val fabVisibilityChangeListener = object : FloatingActionButton.OnVisibilityChangedListener() {
        override fun onHidden(fab: FloatingActionButton?) {
            fab?.let {
                it.setColorFilter(Color.BLACK)
                val image = image ?: return
                it.setImageResource(image)
                it.show()
            }
        }
    }
}
