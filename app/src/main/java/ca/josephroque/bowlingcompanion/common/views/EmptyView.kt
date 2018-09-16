package ca.josephroque.bowlingcompanion.common.views

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import ca.josephroque.bowlingcompanion.R
import kotlinx.android.synthetic.main.view_empty.view.empty_image as emptyImage
import kotlinx.android.synthetic.main.view_empty.view.empty_text as emptyText

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Displays an image and a notice describing the current empty state of a fragment.
 */
class EmptyView : ConstraintLayout {

    companion object {
        @Suppress("unused")
        private const val TAG = "EmptyView"
    }

    // MARK: Constructors

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.view_empty, this, true)
    }

    var emptyImageId: Int? = null
        set(value) {
            if (value != null) {
                emptyImage.setImageResource(value)
                emptyImage.visibility = View.VISIBLE
            } else {
                emptyImage.visibility = View.INVISIBLE
            }
        }

    var emptyTextId: Int? = null
        set(value) {
            if (value != null) {
                emptyText.setText(value)
            } else {
                emptyText.text = null
            }
        }
}
