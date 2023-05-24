package ca.josephroque.bowlingcompanion.transfer.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import ca.josephroque.bowlingcompanion.R
import kotlinx.android.synthetic.main.view_progress.view.progress_bar as progressBar
import kotlinx.android.synthetic.main.view_progress.view.progress_status as progressStatus

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * View to display a progress bar and text.
 */
class ProgressView : LinearLayout {

    companion object {
        @Suppress("unused")
        private const val TAG = "ProgressView"
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.view_progress, this, true)
    }

    fun setProgress(progress: Int) {
        assert(progress in 0..100) { "Progress can only be between 0% and 100%" }
        progressBar.progress = progress
    }

    fun setStatus(status: CharSequence?) {
        progressStatus.text = status
    }
}
