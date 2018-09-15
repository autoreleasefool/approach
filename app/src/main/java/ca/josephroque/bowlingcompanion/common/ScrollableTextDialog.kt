package ca.josephroque.bowlingcompanion.common

import android.app.Dialog
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.Window
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_scrollable_text.toolbar_scrollable as scrollableToolbar
import kotlinx.android.synthetic.main.dialog_scrollable_text.tv_scrollable as scrollableTextView
import kotlinx.android.synthetic.main.dialog_scrollable_text.view.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Presents scrollable text.
 */
class ScrollableTextDialog : BaseDialogFragment() {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "ScrollableTextDialog"

        /** Argument identifier for title of fragment. */
        private const val ARG_TITLE = "${TAG}_title"

        /** Argument identifier for text to display. */
        private const val ARG_TEXT = "${TAG}_text"

        /**
         * Create a new instance of [ScrollableTextDialog].
         *
         * @param text the text to display
         * @return the new instance
         */
        fun newInstance(@StringRes title: Int, text: CharSequence): ScrollableTextDialog {
            val fragment = ScrollableTextDialog()
            val args = Bundle().apply {
                putInt(ARG_TITLE, title)
                putCharSequence(ARG_TEXT, text)
            }

            fragment.arguments = args
            return fragment
        }
    }

    /** Title of the fragment. */
    private var title: Int = 0

    /** String to display. */
    private var text: CharSequence? = null

    /** @Override */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            title = it.getInt(ARG_TITLE)
            text = it.getCharSequence(ARG_TEXT)
        }

        val rootView = inflater.inflate(R.layout.dialog_scrollable_text, container, false)

        val activity = activity as? AppCompatActivity
        activity?.setSupportActionBar(rootView.toolbar_scrollable)

        activity?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_dismiss)
        }

        setHasOptionsMenu(true)
        return rootView
    }

    /** @Override */
    override fun onStart() {
        super.onStart()

        scrollableToolbar.setTitle(title)
        scrollableTextView.text = text
    }

    /** @Override */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    /** @Override */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        activity?.supportFragmentManager?.popBackStack()
        dismiss()
        return true
    }
}
