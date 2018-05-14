package ca.josephroque.bowlingcompanion.common


import android.app.Dialog
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import ca.josephroque.bowlingcompanion.R
import kotlinx.android.synthetic.main.dialog_scrollable_text.*
import kotlinx.android.synthetic.main.dialog_scrollable_text.view.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Presents scrollable text.
 */
class ScrollableTextDialog : DialogFragment() {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "ScrollableTextDialog"

        /** Argument identifier for title of fragment. */
        private const val TITLE = "${TAG}_TITLE"

        /** Argument identifier for text to display. */
        private const val TEXT = "${TAG}_TEXT"

        /**
         * Create a new instance of [ScrollableTextDialog].
         *
         * @param text the text to display
         * @return the new instance
         */
        fun newInstance(@StringRes title: Int, text: CharSequence): ScrollableTextDialog {
            val fragment = ScrollableTextDialog()
            val args = Bundle().apply {
                putInt(TITLE, title)
                putCharSequence(TEXT, text)
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
        savedInstanceState?.let {
            title = it.getInt(TITLE)
            text = it.getCharSequence(TEXT)
        } ?: run {
            arguments?.let {
                title = it.getInt(TITLE)
                text = it.getCharSequence(TEXT)
            }
        }

        val rootView = inflater.inflate(R.layout.dialog_scrollable_text, container, false)

        val activity = activity as? AppCompatActivity
        activity?.setSupportActionBar(rootView.toolbar_scrollable)

        activity?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        }

        setHasOptionsMenu(true)
        return rootView
    }

    /** @Override */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putInt(TITLE, title)
            putCharSequence(TEXT, text)
        }
    }

    /** @Override */
    override fun onResume() {
        super.onResume()

        toolbar_scrollable.setTitle(title)
        tv_scrollable.text = text
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
