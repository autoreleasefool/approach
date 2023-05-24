package ca.josephroque.bowlingcompanion.common

import android.app.Dialog
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.Window
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import kotlinx.android.synthetic.main.dialog_scrollable_text.toolbar_scrollable as scrollableToolbar
import kotlinx.android.synthetic.main.dialog_scrollable_text.tv_scrollable as scrollableTextView
import kotlinx.android.synthetic.main.dialog_scrollable_text.view.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Presents scrollable text.
 */
class ScrollableTextDialog : DialogFragment() {

    companion object {
        @Suppress("unused")
        private const val TAG = "ScrollableTextDialog"

        private const val ARG_TITLE = "${TAG}_title"
        private const val ARG_TEXT = "${TAG}_text"

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

    private var title: Int = 0
    private var text: CharSequence? = null

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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

    override fun onStart() {
        super.onStart()

        scrollableToolbar.setTitle(title)
        scrollableTextView.text = text
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        activity?.supportFragmentManager?.popBackStack()
        dismiss()
        return true
    }
}
